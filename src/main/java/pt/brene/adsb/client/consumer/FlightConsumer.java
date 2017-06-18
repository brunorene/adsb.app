package pt.brene.adsb.client.consumer;

import com.eaio.uuid.UUID;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import pt.brene.adsb.Utils;
import pt.brene.adsb.domain.tables.pojos.FlightEntry;

import java.sql.Timestamp;

import static pt.brene.adsb.domain.Tables.FLIGHT_ENTRY;

@RequiredArgsConstructor
public class FlightConsumer {

    private final UUID key;
    private final DSLContext dsl;

    @Scheduled(fixedDelay = 60000)
    public void deleteOldEntries() {
        dsl.deleteFrom(FLIGHT_ENTRY)
                .where(FLIGHT_ENTRY.TIMESTAMP.lessOrEqual(new Timestamp(System.currentTimeMillis() - 60000)))
                .execute();
    }

    @Subscribe
    @AllowConcurrentEvents
    @Transactional
    public void newFlightEntry(FlightEntry entry) {
        entry.setClient(Utils.convert(key));
        dsl.executeInsert(dsl.newRecord(FLIGHT_ENTRY, entry));
    }

}
