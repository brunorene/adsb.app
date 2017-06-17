package pt.brene.adsb.api;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import pt.brene.adsb.DateTimeConverter;

import java.time.LocalDateTime;

@Builder
@Data
class FlightEntryDto {

    private LocalDateTime dateTime;
    private String flightId;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private Double speed;

    @JsonSerialize(converter = DateTimeConverter.class)
    private LocalDateTime getDateTime() {
        return dateTime;
    }
}
