package pt.brene.adsb.api;

import com.eaio.uuid.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pt.brene.adsb.client.AdsbClient;
import pt.brene.adsb.client.FlightEntryDto;
import pt.brene.adsb.client.FlightInformationDto;
import pt.brene.adsb.domain.tables.pojos.FlightEntry;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
@RequiredArgsConstructor
class AdsbController {

    private final AdsbClient client;

    @GetMapping(value = "/key", produces = TEXT_PLAIN_VALUE)
    @ResponseBody
    public String getKey() {
        return client.getKey().toString();
    }

    @GetMapping("/clients")
    public List<String> getClients() {
        return client.getClients()
                .stream()
                .map(UUID::toString)
                .collect(toList());
    }

    @GetMapping("poll/{key}")
    public List<FlightEntryDto> pollState(@PathVariable UUID key) {
        return client.pollState(key)
                .stream()
                .collect(Collectors.groupingBy(FlightEntry::getFlightId
                        , Collectors.mapping(fe -> new FlightInformationDto(fe.getTimestamp().toLocalDateTime()
                                , fe.getLatitude()
                                , fe.getLongitude()
                                , fe.getAltitude()
                                , fe.getSpeed()
                                , client.distanceFromHome(fe.getLatitude()
                                , fe.getLongitude()
                                , fe.getAltitude())), toCollection(TreeSet::new))))
                .entrySet()
                .stream()
                .map(map -> new FlightEntryDto(map.getKey(), map.getValue()))
                .collect(toList());
    }
}
