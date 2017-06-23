package pt.brene.adsb.client;

import com.eaio.uuid.UUID;
import com.google.common.base.Charsets;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.gavaghan.geodesy.*;
import org.jooq.DSLContext;
import org.jooq.lambda.Seq;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
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
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pt.brene.adsb.domain.Tables.FLIGHT_ENTRY;
import static pt.brene.adsb.domain.tables.Consumer.CONSUMER;

@Service
public class AdsbClient {

    private final static GlobalPosition HOME = new GlobalPosition(38.651518, -9.185236, 68.7);
    private final static GeodeticCalculator CALC = new GeodeticCalculator();

    private final EventBus bus = new AsyncEventBus(Executors.newWorkStealingPool(4));
    private final AdsbConfiguration config;
    private final DSLContext dsl;

    public AdsbClient(AdsbConfiguration config, DSLContext dsl) {
        this.config = config;
        this.dsl = dsl;
        bus.register(new MessageReceiver(bus));
    }

    @Scheduled(fixedDelay = 60000)
    public void deleteOldEntries() {
        dsl.deleteFrom(FLIGHT_ENTRY)
                .where(FLIGHT_ENTRY.TIMESTAMP.lessOrEqual(new Timestamp(System.currentTimeMillis() - 60000)))
                .execute();
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
        // Never delete the current values
        if (entries.size() > 1) {
            List<String> ids = Seq.zip(Seq.seq(entries.stream()),
                    Seq.concat(entries.subList(1, entries.size()).stream(), Stream.of(entries.get(entries.size() - 1))))
                    .filter(tup -> !tup.v1.getFlightId().equals(tup.v2.getFlightId()))
                    .map(tup -> tup.v2.getFlightId())
                    .collect(Collectors.toList());
            dsl.deleteFrom(FLIGHT_ENTRY)
                    .where(FLIGHT_ENTRY.ID.in(ids))
                    .execute();
        }
        return entries;
    }

    public double distanceFromHome(double latitude, double longitude, double elevation) {
        GlobalPosition here = new GlobalPosition(latitude, longitude, elevation);
        return Math.round(CALC.calculateGeodeticMeasurement(Ellipsoid.WGS84, here, HOME).getPointToPointDistance()) / 1000.0;
    }
}

