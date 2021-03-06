package pt.brene.adsb.redis;

import com.eaio.uuid.UUID;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import pt.brene.adsb.AdsbConnector;
import pt.brene.adsb.FlightInterface;
import pt.brene.adsb.redis.domain.FlightEntry;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static pt.brene.adsb.redis.RedisConfiguration.CLIENT_TEMPLATE;
import static pt.brene.adsb.redis.RedisConfiguration.FLIGHT_TEMPLATE;

@Component
@ConditionalOnProperty("redis")
public class RedisConnector implements AdsbConnector {

    private final static String CLIENT_LIST = "clients";

    private final RedisTemplate<String, UUID> redisTemplateClients;
    private final RedisTemplate<UUID, FlightEntry> redisTemplateFlights;

    public RedisConnector(@Qualifier(CLIENT_TEMPLATE) RedisTemplate<String, UUID> redisTemplateClients
        , @Qualifier(FLIGHT_TEMPLATE) RedisTemplate<UUID, FlightEntry> redisTemplateFlights) {
        this.redisTemplateClients = redisTemplateClients;
        this.redisTemplateFlights = redisTemplateFlights;
    }

    private double getScore(FlightEntry entry) {
        return getScore(entry.getTimestamp());
    }

    private double getScore(Timestamp ts) {
        return (double) ts.getTime();
    }

    @Override
    public void deleteOldEntries() {
        ZSetOperations<UUID, FlightEntry> ops = redisTemplateFlights.opsForZSet();
        Timestamp past60mins = new Timestamp(System.currentTimeMillis() - 60000);
        getClients().forEach(uuid -> ops.removeRangeByScore(uuid, 0.0, getScore(past60mins)));
    }

    @Override
    public Set<UUID> getClients() {
        return redisTemplateClients.opsForSet().members(CLIENT_LIST);
    }

    @Override
    public UUID getKey() {
        val uuid = new UUID();
        redisTemplateClients.opsForSet().add(CLIENT_LIST, uuid);
        return uuid;
    }

    @Override
    public List<? extends FlightInterface> pollState(UUID uuid) {
        ZSetOperations<UUID, FlightEntry> ops = redisTemplateFlights.opsForZSet();
        Map<String, String> flightIds = new HashMap<>();
        List<? extends FlightInterface> response = ops.reverseRange(uuid, 0, -1)
            .stream()
            .peek(flight -> {
                flight.setTimestamp(new Timestamp(ops.score(uuid, flight).longValue()));
                if (flight.getFlightId().startsWith("*")) {
                    flight.setFlightId(flightIds.getOrDefault(flight.getHexId(), flight.getFlightId()));
                } else {
                    flightIds.put(flight.getHexId(), flight.getFlightId());
                }
            }).collect(Collectors.toList());
        if (!response.isEmpty()) {
            ops.remove(uuid, response.toArray(new Object[response.size()]));
        }
        return response;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends FlightInterface> void insertFlight(T entry) {
        ZSetOperations<UUID, FlightEntry> ops = redisTemplateFlights.opsForZSet();
        double score = getScore((FlightEntry) entry);
        ((FlightEntry) entry).setTimestamp(null); // same entry with diff ts is the same entry
        getClients().forEach(uuid -> ops.add(uuid, (FlightEntry) entry, score));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends FlightInterface> T createFlight(Long id, byte[] client, Timestamp timestamp, String flightId, String hexId, Double latitude, Double longitude, Double altitude, Double speed) {
        return (T) new FlightEntry(id, client, timestamp, flightId, hexId, latitude, longitude, altitude, speed);
    }
}
