package fr.sylvainjanet.tracker.journal.configuration;

import fr.sylvainjanet.tracker.journal.adapter.out.persistence.repository.WeightMeasurementSqliteRepository;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.GetFirstWeightMeasurementDateUseCase;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.GetWeightMeasurementByDateUseCase;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.GetWeightMeasurementInDateRangeUseCase;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.LogWeightMeasurementUseCase;
import fr.sylvainjanet.tracker.journal.application.port.out.gateway.store.WeightMeasurementStore;
import fr.sylvainjanet.tracker.journal.application.service.GetFirstWeightMeasurementDateService;
import fr.sylvainjanet.tracker.journal.application.service.GetWeightMeasurementByDateService;
import fr.sylvainjanet.tracker.journal.application.service.GetWeightMeasurementInDateRangeService;
import fr.sylvainjanet.tracker.journal.application.service.LogWeightMeasurementService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration(proxyBeanMethods = false)
public class JournalConfiguration {

    @Bean
    WeightMeasurementStore logWeightMeasurementStore(JdbcClient jdbcClient) {
        return new WeightMeasurementSqliteRepository(jdbcClient);
    }

    @Bean
    LogWeightMeasurementUseCase logWeightMeasurementUseCase(WeightMeasurementStore store) {
        return new LogWeightMeasurementService(store);
    }

    @Bean
    GetWeightMeasurementByDateUseCase getWeightMeasurementByDateUseCase(
            WeightMeasurementStore store) {
        return new GetWeightMeasurementByDateService(store);
    }

    @Bean
    GetWeightMeasurementInDateRangeUseCase getWeightMeasurementInDateRangeUseCase(
            WeightMeasurementStore store) {
        return new GetWeightMeasurementInDateRangeService(store);
    }

    @Bean
    GetFirstWeightMeasurementDateUseCase getFirstWeightMeasurementDateUseCase(
            WeightMeasurementStore store) {
        return new GetFirstWeightMeasurementDateService(store);
    }
}
