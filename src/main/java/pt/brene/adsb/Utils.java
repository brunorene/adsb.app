package pt.brene.adsb;

import com.eaio.uuid.UUID;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Utils {

    public static byte[] convert(UUID uuid) {
        return Bytes.concat(Longs.toByteArray(uuid.getTime()), Longs.toByteArray(uuid.getClockSeqAndNode()));
    }

    public static UUID convert(byte[] array) {
        return new UUID(
                Longs.fromByteArray(Arrays.copyOf(array, Longs.BYTES)),
                Longs.fromByteArray(Arrays.copyOfRange(array, Longs.BYTES, Longs.BYTES * 2)));
    }
}
