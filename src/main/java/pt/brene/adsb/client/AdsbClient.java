package pt.brene.adsb.client;

import com.eaio.uuid.UUID;
import com.google.common.base.Charsets;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.jooq.DSLContext;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.brene.adsb.Utils;
import pt.brene.adsb.client.consumer.FlightConsumer;
import pt.brene.adsb.client.consumer.MessageReceiver;
import pt.brene.adsb.client.message.AdsbMessage;
import pt.brene.adsb.config.AdsbConfiguration;
import pt.brene.adsb.domain.tables.pojos.FlightEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static pt.brene.adsb.domain.Tables.FLIGHT_ENTRY;
import static pt.brene.adsb.domain.tables.Consumer.CONSUMER;

@Service
public class AdsbClient {

    private final EventBus bus = new AsyncEventBus(Executors.newWorkStealingPool(4));
    private final AdsbConfiguration config;
    private final DSLContext dsl;

    public AdsbClient(AdsbConfiguration config, DSLContext dsl) {
        this.config = config;
        this.dsl = dsl;
        bus.register(new MessageReceiver(bus));
    }

    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void serviceStartup() throws IOException {
        dsl.selectFrom(CONSUMER)
                .fetch(CONSUMER.CLIENT)
                .stream()
                .map(Utils::convert)
                .forEach(uuid -> bus.register(new FlightConsumer(uuid, dsl)));
        try (Socket socket = new Socket(config.getHost(), config.getPort());
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charsets.UTF_8))) {
            reader.lines()
                    .map(AdsbMessage::newMessage)
                    .filter(Objects::nonNull)
                    .forEach(bus::post);
        }
    }

    @Transactional
    public UUID getKey() {
        UUID uuid = new UUID();
        dsl.insertInto(CONSUMER)
                .set(CONSUMER.CLIENT, Utils.convert(uuid))
                .execute();
        bus.register(new FlightConsumer(uuid, dsl));
        return uuid;
    }

    public List<UUID> getClients() {
        return dsl.select(CONSUMER.CLIENT)
                .from(CONSUMER)
                .stream()
                .map(bytes -> Utils.convert(bytes.value1()))
                .collect(Collectors.toList());
    }

    public List<FlightEntry> pollState(UUID uuid) {
        List<FlightEntry> entries = dsl.selectFrom(FLIGHT_ENTRY)
                .where(FLIGHT_ENTRY.CLIENT.eq(Utils.convert(uuid)))
                .orderBy(FLIGHT_ENTRY.FLIGHT_ID.asc(),
                        FLIGHT_ENTRY.TIMESTAMP.desc())
                .fetchInto(FlightEntry.class);
        dsl.deleteFrom(FLIGHT_ENTRY)
                .where(FLIGHT_ENTRY.ID.in(entries
                        .stream()
                        .map(FlightEntry::getId)
                        .collect(Collectors.toList())))
                .execute();
        return entries;
    }
}

