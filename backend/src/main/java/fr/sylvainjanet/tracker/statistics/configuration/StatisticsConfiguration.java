package fr.sylvainjanet.tracker.statistics.configuration;

import fr.sylvainjanet.tracker.statistics.application.port.in.usecase.CalculateRollingAveragesUseCase;
import fr.sylvainjanet.tracker.statistics.application.service.CalculateRollingAveragesService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class StatisticsConfiguration {

    @Bean
    CalculateRollingAveragesUseCase calculateRollingAveragesUseCase() {
        return new CalculateRollingAveragesService();
    }
}
