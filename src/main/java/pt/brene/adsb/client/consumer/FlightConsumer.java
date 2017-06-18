package pt.brene.adsb.client.consumer;

import com.eaio.uuid.UUID;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.google.common.primitives.Bytes;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import pt.brene.adsb.Utils;
import pt.brene.adsb.domain.tables.pojos.FlightEntry;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    private byte[] sha256(FlightEntry entry) throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] hold = new byte[8];
        sha.update(Bytes.concat(ByteBuffer.wrap(hold).putLong(entry.getTimestamp().getTime()).array()
                , ByteBuffer.wrap(hold).putDouble(entry.getAltitude()).array()
                , ByteBuffer.wrap(hold).putDouble(entry.getLatitude()).array()
                , ByteBuffer.wrap(hold).putDouble(entry.getLongitude()).array()
                , ByteBuffer.wrap(hold).putDouble(entry.getSpeed()).array()));
        return sha.digest();
    }

    @Subscribe
    @AllowConcurrentEvents
    @Transactional
    public void newFlightEntry(FlightEntry entry) throws NoSuchAlgorithmException {
        byte[] sha256 = sha256(entry);
        if (dsl.selectFrom(FLIGHT_ENTRY)
                .where(FLIGHT_ENTRY.TIMESTAMP.greaterThan(new Timestamp(entry.getTimestamp().getTime() - 5000L)))
                .or(FLIGHT_ENTRY.SHA256.eq(sha256))
                .fetch().isEmpty()) {
            entry.setClient(Utils.convert(key));
            entry.setSha256(sha256);
            dsl.executeInsert(dsl.newRecord(FLIGHT_ENTRY, entry));
        }
    }

}
