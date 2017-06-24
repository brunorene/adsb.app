package pt.brene.adsb.client;

import com.eaio.uuid.UUID;
import com.google.common.base.Charsets;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalPosition;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pt.brene.adsb.AdsbConnector;
import pt.brene.adsb.FlightInterface;
import pt.brene.adsb.client.consumer.FlightConsumer;
import pt.brene.adsb.client.consumer.MessageReceiver;
import pt.brene.adsb.client.message.AdsbMessage;
import pt.brene.adsb.config.AdsbConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

@Service
public class AdsbClient {

    private final static GlobalPosition HOME = new GlobalPosition(38.651518, -9.185236, 68.7);
    private final static GeodeticCalculator CALC = new GeodeticCalculator();

    private final EventBus bus = new AsyncEventBus(Executors.newWorkStealingPool(4));
    private final AdsbConfiguration config;
    private final AdsbConnector connector;

    public AdsbClient(AdsbConfiguration config, AdsbConnector connector) {
        this.config = config;
        this.connector = connector;
        bus.register(new MessageReceiver(bus, connector));
    }

    @Scheduled(fixedDelay = 60000)
    public void deleteOldEntries() {
        connector.deleteOldEntries();
    }

    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void serviceStartup() throws IOException {
        connector.getClients()
                .forEach(uuid -> bus.register(new FlightConsumer(uuid, connector)));
        try (Socket socket = new Socket(config.getHost(), config.getPort());
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charsets.UTF_8))) {
            reader.lines()
                    .map(AdsbMessage::newMessage)
                    .filter(Objects::nonNull)
                    .forEach(bus::post);
        }
    }

    public UUID getKey() {
        UUID uuid = connector.getKey();
        bus.register(new FlightConsumer(uuid, connector));
        return uuid;
    }

    public List<UUID> getClients() {
        return connector.getClients();
    }

    public List<? extends FlightInterface> pollState(UUID uuid) {
        return connector.pollState(uuid);
    }

    public double distanceFromHome(double latitude, double longitude, double elevation) {
        GlobalPosition here = new GlobalPosition(latitude, longitude, elevation);
        return Math.round(CALC.calculateGeodeticMeasurement(Ellipsoid.WGS84, here, HOME).getPointToPointDistance()) / 1000.0;
    }
}

