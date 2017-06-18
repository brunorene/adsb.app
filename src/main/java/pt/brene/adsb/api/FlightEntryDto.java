package pt.brene.adsb.api;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pt.brene.adsb.DateTimeConverter;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
class FlightEntryDto {

    private final LocalDateTime dateTime;
    private final String flightId;
    private final Double latitude;
    private final Double longitude;
    private final Double altitude;
    private final Double speed;

    @JsonSerialize(converter = DateTimeConverter.class)
    private LocalDateTime getDateTime() {
        return dateTime;
    }
}
