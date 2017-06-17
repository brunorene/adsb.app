package pt.brene.adsb.client.consumer;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import pt.brene.adsb.client.message.AdsbMessage;
import pt.brene.adsb.client.message.EsAirbornePosition;
import pt.brene.adsb.client.message.EsAirborneVelocity;
import pt.brene.adsb.client.message.EsIdentificationAndCategory;
import pt.brene.adsb.domain.tables.pojos.FlightEntry;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.MINUTES;

@Slf4j
public class MessageReceiver {

    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(5);

    private final Map<Tuple2<String, String>, ScheduledFuture<?>> futures = new HashMap<>();

    private final Map<String, EsAirbornePosition> positions = new HashMap<>();
    private final Map<String, EsAirborneVelocity> speeds = new HashMap<>();
    private final Map<String, EsIdentificationAndCategory> identifiers = new HashMap<>();

    private final EventBus bus;

    public MessageReceiver(EventBus bus) {
        this.bus = bus;
    }

    private <T extends AdsbMessage> void processMsg(Map<String, T> map, T msg) {
        boolean logFlight = !map.containsKey(msg.getHexId());
        map.put(msg.getHexId(), msg);
        if (logFlight) {
            logFlight(msg.getHexId());
        }
    }

    @Subscribe
    public void receive(EsAirbornePosition position) {
        processMsg(positions, position);
    }

    @Subscribe
    public void receive(EsAirborneVelocity speed) {
        processMsg(speeds, speed);
    }

    @Subscribe
    public void receive(EsIdentificationAndCategory identifier) {
        processMsg(identifiers, identifier);
    }

    private void logFlight(String hexId) {
        if (identifiers.containsKey(hexId)
                && positions.containsKey(hexId)
                && speeds.containsKey(hexId)) {
            bus.post(new FlightEntry(null
                    , new Timestamp(new Date().getTime())
                    , identifiers.get(hexId).getCallSign()
                    , positions.get(hexId).getLatitude()
                    , positions.get(hexId).getLongitude()
                    , positions.get(hexId).getAltitude()
                    , speeds.get(hexId).getGroundSpeed()));
            positions.remove(hexId);
            speeds.remove(hexId);
        }
    }

}