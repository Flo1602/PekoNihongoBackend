package at.primetshofer.pekoNihongoBackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "asyncTaskExecutor")
    public Executor taskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(3);
        executor.setThreadNamePrefix("Async-");
        executor.initialize();

        return executor;
    }
}
