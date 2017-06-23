package pt.brene.adsb.client.consumer;

import com.eaio.uuid.UUID;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.transaction.annotation.Transactional;
import pt.brene.adsb.Utils;
import pt.brene.adsb.domain.tables.pojos.FlightEntry;

import java.security.NoSuchAlgorithmException;

import static pt.brene.adsb.domain.Tables.FLIGHT_ENTRY;

@RequiredArgsConstructor
public class FlightConsumer {

    private final UUID key;
    private final DSLContext dsl;

    @Subscribe
    @AllowConcurrentEvents
    @Transactional
    public void newFlightEntry(FlightEntry entry) throws NoSuchAlgorithmException {
        entry.setClient(Utils.convert(key));
        int count = dsl.selectCount()
                .from(FLIGHT_ENTRY)
                .where(FLIGHT_ENTRY.LATITUDE.equal(entry.getLatitude()))
                .and(FLIGHT_ENTRY.LONGITUDE.equal(entry.getLongitude()))
                .and(FLIGHT_ENTRY.ALTITUDE.equal(entry.getAltitude()))
                .and(FLIGHT_ENTRY.SPEED.equal(entry.getSpeed()))
                .and(FLIGHT_ENTRY.FLIGHT_ID.equalIgnoreCase(entry.getFlightId()))
                .and(FLIGHT_ENTRY.CLIENT.equal(entry.getClient()))
                .fetch()
                .get(0)
                .value1();
        if (count == 0) {
            dsl.executeInsert(dsl.newRecord(FLIGHT_ENTRY, entry));
        }
    }

}
