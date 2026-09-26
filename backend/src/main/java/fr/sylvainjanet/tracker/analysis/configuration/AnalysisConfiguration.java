package fr.sylvainjanet.tracker.analysis.configuration;

import fr.sylvainjanet.tracker.analysis.application.port.in.usecase.GetWeightAnalysisUseCase;
import fr.sylvainjanet.tracker.analysis.application.service.GetWeightAnalysisService;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.GetFirstWeightMeasurementDateUseCase;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.GetWeightMeasurementInDateRangeUseCase;
import fr.sylvainjanet.tracker.statistics.application.port.in.usecase.CalculateRollingAveragesUseCase;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class AnalysisConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    GetWeightAnalysisUseCase getWeightAnalysisUseCase(
            GetFirstWeightMeasurementDateUseCase getFirstDate,
            GetWeightMeasurementInDateRangeUseCase getMeasurementsInRange,
            CalculateRollingAveragesUseCase calculateRollingAverages,
            Clock clock) {
        return new GetWeightAnalysisService(
                getFirstDate, getMeasurementsInRange, calculateRollingAverages, clock);
    }
}
