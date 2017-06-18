package pt.brene.adsb.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class FlightEntryDto {

    private final String flightId;
    private final Collection<FlightInformationDto> info;
}
