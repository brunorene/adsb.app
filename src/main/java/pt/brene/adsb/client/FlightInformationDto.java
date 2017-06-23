package pt.brene.adsb.client;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import pt.brene.adsb.DateTimeConverter;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class FlightInformationDto implements Comparable<FlightInformationDto> {

    private final LocalDateTime dateTime;
    private final Double latitude;
    private final Double longitude;
    private final Double altitude;
    private final Double speed;
    private final Double distanceFromHome;

    @JsonSerialize(converter = DateTimeConverter.class)
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public int compareTo(FlightInformationDto flightInformationDto) {
        return -getDateTime().compareTo(flightInformationDto.getDateTime());
    }
}
