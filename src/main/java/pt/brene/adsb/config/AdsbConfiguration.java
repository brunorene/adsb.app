package pt.brene.adsb.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "adsb")
@Data
public class AdsbConfiguration {

    private String host;
    private int port;

}
