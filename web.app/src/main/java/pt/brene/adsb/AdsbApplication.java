package pt.brene.adsb;

import org.jooq.DSLContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import pt.brene.adsb.h2.H2Connector;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class AdsbApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdsbApplication.class, args);
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(5);
    }

    @Bean
    public TaskExecutor taskExecutor(ExecutorService executorService) {
        return executorService::execute;
    }

    @Bean
    @ConditionalOnProperty("h2")
    public AdsbConnector adsbConnector(DSLContext dsl) {
        return new H2Connector(dsl);
    }
}
