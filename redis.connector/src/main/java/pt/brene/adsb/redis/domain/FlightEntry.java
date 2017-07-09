package pt.brene.adsb.redis.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import pt.brene.adsb.FlightInterface;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class FlightEntry implements FlightInterface, Serializable {

    private Long id;
    private byte[] client;
    private Timestamp timestamp;
    private String flightId;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private Double speed;

    @Override
    @SuppressWarnings("unchecked")
    public <T extends FlightInterface> T setClient(byte[] client) {
        this.client = client;
        return (T) this;
    }

}
