package pt.brene.adsb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableTransactionManagement
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
}
