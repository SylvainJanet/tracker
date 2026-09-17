package fr.sylvainjanet.tracker.journal.configuration;

import fr.sylvainjanet.tracker.journal.adapter.out.persistence.repository.LogWeightMeasurementSqliteRepository;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.LogWeightMeasurementUseCase;
import fr.sylvainjanet.tracker.journal.application.port.out.gateway.store.LogWeightMeasurementStore;
import fr.sylvainjanet.tracker.journal.application.service.LogWeightMeasurementService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration(proxyBeanMethods = false)
public class JournalConfiguration {

    @Bean
    LogWeightMeasurementStore logWeightMeasurementStore(JdbcClient jdbcClient) {
        return new LogWeightMeasurementSqliteRepository(jdbcClient);
    }

    @Bean
    LogWeightMeasurementUseCase logWeightMeasurementUseCase(LogWeightMeasurementStore store) {
        return new LogWeightMeasurementService(store);
    }
}
