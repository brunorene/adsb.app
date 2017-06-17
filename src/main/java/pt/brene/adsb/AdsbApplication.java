package pt.brene.adsb;

import com.eaio.uuid.UUID;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;

@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
public class AdsbApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdsbApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new UUIDModule());
        return mapper;
    }

    public static class UUIDModule extends SimpleModule {

        public UUIDModule() {
            addSerializer(UUID.class, new StdSerializer<UUID>(UUID.class) {
                @Override
                public void serialize(UUID value, JsonGenerator gen, SerializerProvider provider) throws IOException {
                    gen.writeString(value.toString());
                }
            });
            addDeserializer(UUID.class, new StdDeserializer<UUID>(UUID.class) {
                @Override
                public UUID deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                    return new UUID(p.getText());
                }
            });
        }
    }

}
