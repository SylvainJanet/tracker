package fr.sylvainjanet.tracker.analysis.application.service.mapper;

import static fr.sylvainjanet.tracker.analysis.application.service.mapper.GetWeightAnalysisServiceMapper.analysisToResult;
import static fr.sylvainjanet.tracker.analysis.application.service.mapper.GetWeightAnalysisServiceMapper.dateRangeToQuery;
import static fr.sylvainjanet.tracker.analysis.application.service.mapper.GetWeightAnalysisServiceMapper.measurementsToDomain;
import static fr.sylvainjanet.tracker.analysis.domain.builder.WeightAnalysisBuilder.aWeightAnalysis;
import static fr.sylvainjanet.tracker.shared.domain.builder.DateRangeBuilder.aDateRange;
import static fr.sylvainjanet.tracker.shared.domain.builder.WeightMeasurementBuilder.aWeightMeasurement;
import static org.assertj.core.api.Assertions.assertThat;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult.DateRangeResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult.WeightMeasurementResult;
import fr.sylvainjanet.tracker.analysis.domain.WeightAnalysis;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementInDateRangeQuery;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementInDateRangeResult;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementInDateRangeResult.WeightMeasurementByDateResult;
import fr.sylvainjanet.tracker.shared.domain.DateRange;
import fr.sylvainjanet.tracker.shared.domain.WeightMeasurement;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class GetWeightAnalysisServiceMapperTest {

    private static final LocalDate FIRST_DATE = LocalDate.parse("2026-09-20");
    private static final LocalDate SECOND_DATE = LocalDate.parse("2026-09-23");
    private static final LocalDate END_DATE = LocalDate.parse("2026-09-25");

    @Test
    void mapsADateRangeToAQuery() {
        DateRange range = aDateRange().withStartDate(FIRST_DATE).withEndDate(END_DATE).build();

        GetWeightMeasurementInDateRangeQuery query = dateRangeToQuery(range);

        assertThat(query.startDate()).isEqualTo(FIRST_DATE);
        assertThat(query.endDate()).isEqualTo(END_DATE);
    }

    @Test
    void mapsMeasurementResultsToDomainMeasurements() {
        GetWeightMeasurementInDateRangeResult result =
                new GetWeightMeasurementInDateRangeResult(
                        List.of(
                                new WeightMeasurementByDateResult(
                                        FIRST_DATE, new BigDecimal("82.10")),
                                new WeightMeasurementByDateResult(
                                        SECOND_DATE, new BigDecimal("81.90"))));

        List<WeightMeasurement> measurements = measurementsToDomain(result);

        assertThat(measurements).hasSize(2);

        WeightMeasurement firstMeasurement = measurements.get(0);
        assertThat(firstMeasurement.date()).isEqualTo(FIRST_DATE);
        assertThat(firstMeasurement.weightInKilograms()).isEqualByComparingTo("82.10");

        WeightMeasurement secondMeasurement = measurements.get(1);
        assertThat(secondMeasurement.date()).isEqualTo(SECOND_DATE);
        assertThat(secondMeasurement.weightInKilograms()).isEqualByComparingTo("81.90");
    }

    @Test
    void mapsAWeightAnalysisToAResult() {
        DateRange range = aDateRange().withStartDate(FIRST_DATE).withEndDate(END_DATE).build();
        WeightAnalysis analysis =
                aWeightAnalysis()
                        .withTimelineStartDate(FIRST_DATE)
                        .withRange(range)
                        .withWeightMeasurements(
                                List.of(
                                        aWeightMeasurement()
                                                .withDate(FIRST_DATE)
                                                .withWeightInKg(new BigDecimal("82.10"))
                                                .build(),
                                        aWeightMeasurement()
                                                .withDate(SECOND_DATE)
                                                .withWeightInKg(new BigDecimal("81.90"))
                                                .build()))
                        .build();

        GetWeightAnalysisResult result = analysisToResult(analysis);

        assertThat(result.timelineStartDate()).isEqualTo(FIRST_DATE);

        DateRangeResult resultRange = result.range();
        assertThat(resultRange).isNotNull();
        assertThat(resultRange.startDate()).isEqualTo(FIRST_DATE);
        assertThat(resultRange.endDate()).isEqualTo(END_DATE);

        assertThat(result.weightMeasurements()).hasSize(2);

        WeightMeasurementResult firstMeasurement = result.weightMeasurements().get(0);
        assertThat(firstMeasurement.date()).isEqualTo(FIRST_DATE);
        assertThat(firstMeasurement.dayNumber()).isEqualTo(1L);
        assertThat(firstMeasurement.weightInKg()).isEqualByComparingTo("82.10");

        WeightMeasurementResult secondMeasurement = result.weightMeasurements().get(1);
        assertThat(secondMeasurement.date()).isEqualTo(SECOND_DATE);
        assertThat(secondMeasurement.dayNumber()).isEqualTo(4L);
        assertThat(secondMeasurement.weightInKg()).isEqualByComparingTo("81.90");
    }
}
