package pt.brene.adsb.redis;

import com.eaio.uuid.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import pt.brene.adsb.AdsbConnector;
import pt.brene.adsb.FlightInterface;
import pt.brene.adsb.redis.domain.FlightEntry;

import java.sql.Timestamp;
import java.util.List;

import static pt.brene.adsb.redis.RedisConfiguration.CLIENT_TEMPLATE;
import static pt.brene.adsb.redis.RedisConfiguration.FLIGHT_TEMPLATE;

@Component
@ConditionalOnProperty("redis")
public class RedisConnector implements AdsbConnector {

    private final static String CLIENT_LIST = "clients";

    public RedisConnector(@Qualifier(CLIENT_TEMPLATE) RedisTemplate<String, UUID> redisTemplateClients
            , @Qualifier(FLIGHT_TEMPLATE) RedisTemplate<UUID, FlightEntry> redisTemplateFlights) {
        this.redisTemplateClients = redisTemplateClients;
        this.redisTemplateFlights = redisTemplateFlights;
    }

    private final RedisTemplate<String, UUID> redisTemplateClients;
    private final RedisTemplate<UUID, FlightEntry> redisTemplateFlights;

    @Override
    public void deleteOldEntries() {

    }

    @Override
    public List<UUID> getClients() {
        return redisTemplateClients.opsForList().range(CLIENT_LIST, 0, -1);
    }

    @Override
    public UUID getKey() {
        return null;
    }

    @Override
    public List<? extends FlightInterface> pollState(UUID uuid) {
        return null;
    }

    @Override
    public <T extends FlightInterface> void insertFlight(T entry) {

    }

    @Override
    public <T extends FlightInterface> T createFlight(Long id, byte[] client, Timestamp timestamp, String flightId, Double latitude, Double longitude, Double altitude, Double speed) {
        return null;
    }
}
