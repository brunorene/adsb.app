package pt.brene.adsb.redis;

import com.eaio.uuid.UUID;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import pt.brene.adsb.Utils;

public class UUIDSerializer implements RedisSerializer<UUID> {
    @Override
    public byte[] serialize(UUID uuid) throws SerializationException {
        return Utils.convert(uuid);
    }

    @Override
    public UUID deserialize(byte[] bytes) throws SerializationException {
        return Utils.convert(bytes);
    }
}
