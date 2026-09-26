package fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.mapper;

import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder.GetWeightAnalysisResultBuilder.DateRangeResultBuilder.aDateRangeResult;
import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder.GetWeightAnalysisResultBuilder.WeightMeasurementResultBuilder.aWeightMeasurementResult;
import static fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.builder.GetWeightAnalysisResultBuilder.aGetWeightAnalysisResult;
import static org.assertj.core.api.Assertions.assertThat;

import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.GetWeightAnalysisResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.GetWeightAnalysisResponse.DateRangeResponse;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.GetWeightAnalysisResponse.WeightMeasurementResponse;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class GetWeightAnalysisResponseMapperTest {

    @Test
    void mapsAWeightAnalysisResultToAResponse() {
        LocalDate firstDate = LocalDate.parse("2026-09-20");
        LocalDate secondDate = LocalDate.parse("2026-09-23");
        LocalDate endDate = LocalDate.parse("2026-09-25");

        GetWeightAnalysisResult result =
                aGetWeightAnalysisResult()
                        .withTimelineStartDate(firstDate)
                        .withRange(
                                aDateRangeResult()
                                        .withStartDate(firstDate)
                                        .withEndDate(endDate)
                                        .build())
                        .withWeightMeasurements(
                                List.of(
                                        aWeightMeasurementResult()
                                                .withDate(firstDate)
                                                .withDayNumber(1L)
                                                .withWeightInKg(new BigDecimal("82.10"))
                                                .build(),
                                        aWeightMeasurementResult()
                                                .withDate(secondDate)
                                                .withDayNumber(4L)
                                                .withWeightInKg(new BigDecimal("81.90"))
                                                .build()))
                        .build();

        GetWeightAnalysisResponse response =
                GetWeightAnalysisResponseMapper.resultToResponse(result);

        assertThat(response.timelineStartDate()).isEqualTo(firstDate);

        DateRangeResponse range = response.range();
        assertThat(range).isNotNull();
        assertThat(range.startDate()).isEqualTo(firstDate);
        assertThat(range.endDate()).isEqualTo(endDate);

        assertThat(response.weightMeasurements()).hasSize(2);

        WeightMeasurementResponse firstMeasurement = response.weightMeasurements().get(0);
        assertThat(firstMeasurement.date()).isEqualTo(firstDate);
        assertThat(firstMeasurement.dayNumber()).isEqualTo(1L);
        assertThat(firstMeasurement.weightInKg()).isEqualByComparingTo("82.10");

        WeightMeasurementResponse secondMeasurement = response.weightMeasurements().get(1);
        assertThat(secondMeasurement.date()).isEqualTo(secondDate);
        assertThat(secondMeasurement.dayNumber()).isEqualTo(4L);
        assertThat(secondMeasurement.weightInKg()).isEqualByComparingTo("81.90");

        assertThat(response.rollingAverages()).isEmpty();
    }

    @Test
    void mapsAnEmptyWeightAnalysisResultToAnEmptyResponse() {
        GetWeightAnalysisResponse response =
                GetWeightAnalysisResponseMapper.resultToResponse(GetWeightAnalysisResult.empty());

        assertThat(response.timelineStartDate()).isNull();
        assertThat(response.range()).isNull();
        assertThat(response.weightMeasurements()).isEmpty();
        assertThat(response.rollingAverages()).isEmpty();
    }
}
