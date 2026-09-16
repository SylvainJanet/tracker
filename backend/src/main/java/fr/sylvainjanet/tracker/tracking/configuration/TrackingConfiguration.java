package fr.sylvainjanet.tracker.tracking.configuration;

import fr.sylvainjanet.tracker.tracking.adapter.out.persistence.repository.SqliteDailyRecordRepository;
import fr.sylvainjanet.tracker.tracking.application.port.in.usecase.CreateDailyRecordUseCase;
import fr.sylvainjanet.tracker.tracking.application.port.out.gateway.store.DailyRecordStore;
import fr.sylvainjanet.tracker.tracking.application.service.CreateDailyRecordService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration(proxyBeanMethods = false)
public class TrackingConfiguration {

    @Bean
    DailyRecordStore dailyRecordRepository(JdbcClient jdbcClient) {
        return new SqliteDailyRecordRepository(jdbcClient);
    }

    @Bean
    CreateDailyRecordUseCase createDailyRecordUseCase(DailyRecordStore repository) {
        return new CreateDailyRecordService(repository);
    }
}
