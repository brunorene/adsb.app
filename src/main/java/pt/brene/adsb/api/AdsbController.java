package pt.brene.adsb.api;

import com.eaio.uuid.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pt.brene.adsb.client.AdsbClient;
import pt.brene.adsb.domain.tables.pojos.FlightEntry;

import java.util.List;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
@RequiredArgsConstructor
public class AdsbController {

    private final AdsbClient client;

    @GetMapping(value = "/key", produces = TEXT_PLAIN_VALUE)
    @ResponseBody
    public String getKey() {
        return client.getKey().toString();
    }

    @GetMapping("/clients")
    public List<UUID> getClients() {
        return client.getClients();
    }

    @GetMapping("poll/{key}")
    public List<FlightEntry> pollState(@PathVariable UUID key) {
        return client.pollState(key);
    }
}
