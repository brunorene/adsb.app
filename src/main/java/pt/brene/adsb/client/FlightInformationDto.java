package pt.brene.adsb.client;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pt.brene.adsb.DateTimeConverter;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class FlightInformationDto {

    private final LocalDateTime dateTime;
    private final Double latitude;
    private final Double longitude;
    private final Double altitude;
    private final Double speed;

    @JsonSerialize(converter = DateTimeConverter.class)
    private LocalDateTime getDateTime() {
        return dateTime;
    }
}
