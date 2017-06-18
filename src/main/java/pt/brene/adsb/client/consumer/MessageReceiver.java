package pt.brene.adsb.client.consumer;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import pt.brene.adsb.client.message.AdsbMessage;
import pt.brene.adsb.client.message.EsAirbornePosition;
import pt.brene.adsb.client.message.EsAirborneVelocity;
import pt.brene.adsb.client.message.EsIdentificationAndCategory;
import pt.brene.adsb.domain.tables.pojos.FlightEntry;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class MessageReceiver {

    private final Map<String, EsAirbornePosition> positions = new HashMap<>();
    private final Map<String, EsAirborneVelocity> speeds = new HashMap<>();
    private final Map<String, EsIdentificationAndCategory> identifiers = new HashMap<>();

    private final EventBus bus;

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
                && speeds.containsKey(hexId)
                && ObjectUtils.allNotNull(identifiers.get(hexId).getCallSign()
                , positions.get(hexId).getLatitude()
                , positions.get(hexId).getLongitude()
                , positions.get(hexId).getAltitude()
                , speeds.get(hexId).getGroundSpeed())) {
            bus.post(new FlightEntry(null, null
                    , new Timestamp(new Date().getTime())
                    , identifiers.get(hexId).getCallSign()
                    , positions.get(hexId).getLatitude()
                    , positions.get(hexId).getLongitude()
                    , positions.get(hexId).getAltitude()
                    , speeds.get(hexId).getGroundSpeed()
                    , null));
            positions.remove(hexId);
            speeds.remove(hexId);
        }
    }

}
