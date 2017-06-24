package pt.brene.adsb.client.consumer;

import com.eaio.uuid.UUID;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pt.brene.adsb.AdsbConnector;
import pt.brene.adsb.FlightInterface;
import pt.brene.adsb.Utils;

import java.security.NoSuchAlgorithmException;

@RequiredArgsConstructor
public class FlightConsumer {

    private final UUID key;
    private final AdsbConnector connector;

    @Subscribe
    @AllowConcurrentEvents
    @Transactional
    public void newFlightEntry(FlightInterface entry) throws NoSuchAlgorithmException {
        entry.setClient(Utils.convert(key));
        connector.insertFlight(entry);
    }

}
