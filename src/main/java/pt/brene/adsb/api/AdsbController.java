package pt.brene.adsb.api;

import com.eaio.uuid.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pt.brene.adsb.client.AdsbClient;

import java.util.List;
import java.util.stream.Collectors;

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
                .collect(Collectors.toList());
    }

    @GetMapping("poll/{key}")
    public List<FlightEntryDto> pollState(@PathVariable UUID key) {
        return client.pollState(key)
                .stream()
                .map(fe -> new FlightEntryDto(
                        fe.getTimestamp().toLocalDateTime()
                        , fe.getFlightId()
                        , fe.getLatitude()
                        , fe.getLongitude()
                        , fe.getAltitude()
                        , fe.getSpeed()))
                .collect(Collectors.toList());
    }
}
