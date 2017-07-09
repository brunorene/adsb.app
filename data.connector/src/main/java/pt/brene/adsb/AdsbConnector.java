package pt.brene.adsb;

import com.eaio.uuid.UUID;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public interface AdsbConnector {

    void deleteOldEntries();

    Set<UUID> getClients();

    UUID getKey();

    List<? extends FlightInterface> pollState(UUID uuid);

    <T extends FlightInterface> void insertFlight(T entry);

    <T extends FlightInterface> T createFlight(Long id,
                                               byte[] client,
                                               Timestamp timestamp,
                                               String flightId,
                                               Double latitude,
                                               Double longitude,
                                               Double altitude,
                                               Double speed);
}
