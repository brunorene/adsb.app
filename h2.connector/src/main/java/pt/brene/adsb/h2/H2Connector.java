package pt.brene.adsb.h2;

import com.eaio.uuid.UUID;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.lambda.Seq;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import pt.brene.adsb.AdsbConnector;
import pt.brene.adsb.FlightInterface;
import pt.brene.adsb.Utils;
import pt.brene.adsb.domain.tables.pojos.FlightEntry;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pt.brene.adsb.domain.Tables.CONSUMER;
import static pt.brene.adsb.domain.Tables.FLIGHT_ENTRY;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty("h2")
public class H2Connector implements AdsbConnector {

    private final DSLContext dsl;

    @Override
    public void deleteOldEntries() {
        dsl.deleteFrom(FLIGHT_ENTRY)
                .where(FLIGHT_ENTRY.TIMESTAMP.lessOrEqual(new Timestamp(System.currentTimeMillis() - 60000)))
                .execute();
    }

    @Override
    public List<UUID> getClients() {
        return dsl.selectFrom(CONSUMER)
                .fetch(CONSUMER.CLIENT)
                .stream()
                .map(Utils::convert)
                .collect(Collectors.toList());
    }

    @Override
    public UUID getKey() {
        UUID uuid = new UUID();
        dsl.insertInto(CONSUMER)
                .set(CONSUMER.CLIENT, Utils.convert(uuid))
                .execute();
        return uuid;
    }

    @Override
    public List<? extends FlightInterface> pollState(UUID uuid) {
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

    @Override
    public <T extends FlightInterface> void insertFlight(T entry) {
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
        if (count == 0 && entry instanceof FlightEntry) {
            dsl.executeInsert(dsl.newRecord(FLIGHT_ENTRY, entry));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends FlightInterface> T createFlight(Long id, byte[] client, Timestamp timestamp, String flightId, Double latitude, Double longitude, Double altitude, Double speed) {
        return (T) new FlightEntry(id, client, timestamp, flightId, latitude, longitude, altitude, speed);
    }
}
