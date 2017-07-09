package pt.brene.adsb.client.consumer;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import pt.brene.adsb.AdsbConnector;
import pt.brene.adsb.client.message.AdsbMessage;
import pt.brene.adsb.client.message.EsAirbornePosition;
import pt.brene.adsb.client.message.EsAirborneVelocity;
import pt.brene.adsb.client.message.EsIdentificationAndCategory;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class MessageReceiver {

    private final Map<String, EsAirbornePosition> positions = new HashMap<>();
    private final Map<String, EsAirborneVelocity> speeds = new HashMap<>();
    private final Map<String, EsIdentificationAndCategory> identifiers = new HashMap<>();

    private final EventBus bus;
    private final AdsbConnector connector;

    private <T extends AdsbMessage> void processMsg(Map<String, T> map, T msg) {
        map.put(msg.getHexId(), msg);
        logFlight(msg.getHexId());
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
        String callSign = identifiers.getOrDefault(hexId, new EsIdentificationAndCategory(hexId)).getCallSign();
        if (positions.containsKey(hexId)
            && ObjectUtils.allNotNull(
            positions.get(hexId).getLatitude()
            , positions.get(hexId).getLongitude()
            , positions.get(hexId).getAltitude())) {
            bus.post(connector.createFlight(null, null
                , new Timestamp(new Date().getTime())
                , callSign
                , hexId
                , positions.get(hexId).getLatitude()
                , positions.get(hexId).getLongitude()
                , positions.get(hexId).getAltitude()
                , Optional.ofNullable(speeds.get(hexId))
                    .map(EsAirborneVelocity::getGroundSpeed).orElse(-1.0)));
        }
    }

}
