package pt.brene.adsb.config;

import com.eaio.uuid.UUID;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {
    @Override
    protected void addFormatters(FormatterRegistry registry) {
        //noinspection Convert2Lambda
        registry.addConverter(new Converter<String, UUID>() {
            @Override
            public UUID convert(String source) {
                return new UUID(source);
            }
        });
    }
}
