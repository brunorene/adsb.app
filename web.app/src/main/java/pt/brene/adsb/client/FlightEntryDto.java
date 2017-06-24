package pt.brene.adsb.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class FlightEntryDto {

    private final String flightId;
    private final Set<FlightInformationDto> info;
}
