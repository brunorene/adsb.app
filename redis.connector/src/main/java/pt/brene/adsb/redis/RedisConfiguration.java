package pt.brene.adsb.redis;

import com.eaio.uuid.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import pt.brene.adsb.redis.domain.FlightEntry;

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedisConfiguration {

    static final String CLIENT_TEMPLATE = "clientTemplate";
    static final String FLIGHT_TEMPLATE = "flightTemplate";

    private String host;

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory(host, 6379);
    }

    @Bean(CLIENT_TEMPLATE)
    public RedisTemplate<String, UUID> redisTemplateForClients() {
        RedisTemplate<String, UUID> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new UUIDSerializer());
        return template;
    }

    @Bean(FLIGHT_TEMPLATE)
    public RedisTemplate<UUID, FlightEntry> redisTemplateForFlights() {
        ObjectMapper mapper = new ObjectMapper();
        RedisTemplate<UUID, FlightEntry> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory());
        template.setKeySerializer(new UUIDSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(FlightEntry.class));
        return template;
    }


}
