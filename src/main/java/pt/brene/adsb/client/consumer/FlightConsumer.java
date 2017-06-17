package pt.brene.adsb.client.consumer;

import com.eaio.uuid.UUID;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.transaction.annotation.Transactional;
import pt.brene.adsb.domain.tables.pojos.FlightEntry;

@Slf4j
@RequiredArgsConstructor
public class FlightConsumer {

    private final UUID key;
    private final DSLContext dsl;

    @Subscribe
    @AllowConcurrentEvents
    @Transactional
    public void newFlightEntry(FlightEntry entry) {

    }

}
