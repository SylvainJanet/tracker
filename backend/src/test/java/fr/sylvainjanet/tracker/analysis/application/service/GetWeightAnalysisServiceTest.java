package fr.sylvainjanet.tracker.analysis.application.service;

import static fr.sylvainjanet.tracker.analysis.fixture.WeightAnalysisFixtures.ANALYSIS_RESULT;
import static fr.sylvainjanet.tracker.analysis.fixture.WeightAnalysisFixtures.CLOCK;
import static fr.sylvainjanet.tracker.analysis.fixture.WeightAnalysisFixtures.DATE_RANGE_QUERY;
import static fr.sylvainjanet.tracker.analysis.fixture.WeightAnalysisFixtures.EMPTY_MEASUREMENTS_RESULT;
import static fr.sylvainjanet.tracker.analysis.fixture.WeightAnalysisFixtures.FIRST_DATE_RESULT;
import static fr.sylvainjanet.tracker.analysis.fixture.WeightAnalysisFixtures.FUTURE_FIRST_DATE_RESULT;
import static fr.sylvainjanet.tracker.analysis.fixture.WeightAnalysisFixtures.MEASUREMENTS_RESULT;
import static fr.sylvainjanet.tracker.analysis.fixture.WeightAnalysisFixtures.NO_MEASUREMENTS_RESULT;
import static fr.sylvainjanet.tracker.analysis.fixture.WeightAnalysisFixtures.OUTSIDE_RANGE_MEASUREMENTS_RESULT;
import static fr.sylvainjanet.tracker.analysis.fixture.WeightAnalysisFixtures.ROLLING_AVERAGES_COMMAND;
import static fr.sylvainjanet.tracker.analysis.fixture.WeightAnalysisFixtures.ROLLING_AVERAGES_RESULT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import fr.sylvainjanet.tracker.journal.application.port.in.usecase.GetFirstWeightMeasurementDateUseCase;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.GetWeightMeasurementInDateRangeUseCase;
import fr.sylvainjanet.tracker.statistics.application.port.in.usecase.CalculateRollingAveragesUseCase;
import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetWeightAnalysisServiceTest {

    @Mock private GetFirstWeightMeasurementDateUseCase getFirstDate;
    @Mock private GetWeightMeasurementInDateRangeUseCase getMeasurementsInRange;
    @Mock private CalculateRollingAveragesUseCase calculateRollingAverages;

    private GetWeightAnalysisService service;

    @BeforeEach
    void setUp() {
        service =
                new GetWeightAnalysisService(
                        getFirstDate, getMeasurementsInRange, calculateRollingAverages, CLOCK);
    }

    @Test
    void returnsWeightAnalysisThroughToday() {
        when(getFirstDate.get()).thenReturn(Optional.of(FIRST_DATE_RESULT));
        when(getMeasurementsInRange.get(DATE_RANGE_QUERY)).thenReturn(MEASUREMENTS_RESULT);
        when(calculateRollingAverages.calculate(ROLLING_AVERAGES_COMMAND))
                .thenReturn(ROLLING_AVERAGES_RESULT);

        assertThat(service.get()).isEqualTo(ANALYSIS_RESULT);

        verify(getFirstDate).get();
        verify(getMeasurementsInRange).get(DATE_RANGE_QUERY);
        verify(calculateRollingAverages).calculate(ROLLING_AVERAGES_COMMAND);
    }

    @Test
    void returnsNoMeasurementsWhenNoWeightHasBeenLogged() {
        when(getFirstDate.get()).thenReturn(Optional.empty());

        assertThat(service.get()).isEqualTo(NO_MEASUREMENTS_RESULT);

        verify(getFirstDate).get();
        verifyNoInteractions(getMeasurementsInRange, calculateRollingAverages);
    }

    @Test
    void returnsNoMeasurementsWhenFirstLoggedWeightIsAfterToday() {
        when(getFirstDate.get()).thenReturn(Optional.of(FUTURE_FIRST_DATE_RESULT));

        assertThat(service.get()).isEqualTo(NO_MEASUREMENTS_RESULT);

        verify(getFirstDate).get();
        verifyNoInteractions(getMeasurementsInRange, calculateRollingAverages);
    }

    @Test
    void returnsNoMeasurementsWhenDefaultDateRangeContainsNoMeasurement() {
        when(getFirstDate.get()).thenReturn(Optional.of(FIRST_DATE_RESULT));
        when(getMeasurementsInRange.get(DATE_RANGE_QUERY)).thenReturn(EMPTY_MEASUREMENTS_RESULT);

        assertThat(service.get()).isEqualTo(NO_MEASUREMENTS_RESULT);

        verify(getMeasurementsInRange).get(DATE_RANGE_QUERY);
        verifyNoInteractions(calculateRollingAverages);
    }

    @Test
    void rejectsMeasurementOutsideRequestedDateRange() {
        when(getFirstDate.get()).thenReturn(Optional.of(FIRST_DATE_RESULT));
        when(getMeasurementsInRange.get(DATE_RANGE_QUERY))
                .thenReturn(OUTSIDE_RANGE_MEASUREMENTS_RESULT);

        assertThatThrownBy(service::get)
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("dataSeries date range must be within dataCompleteRange");

        verify(getMeasurementsInRange).get(DATE_RANGE_QUERY);
        verifyNoInteractions(calculateRollingAverages);
    }

    @Test
    void rejectsNullFirstWeightMeasurementDateUseCase() {
        assertThatThrownBy(
                        () ->
                                new GetWeightAnalysisService(
                                        null,
                                        getMeasurementsInRange,
                                        calculateRollingAverages,
                                        CLOCK))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("get first weight measurement date must not be null");
    }

    @Test
    void rejectsNullWeightMeasurementsInRangeUseCase() {
        assertThatThrownBy(
                        () ->
                                new GetWeightAnalysisService(
                                        getFirstDate, null, calculateRollingAverages, CLOCK))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("get weight measurements in dateRange must not be null");
    }

    @Test
    void rejectsNullCalculateRollingAveragesUseCase() {
        assertThatThrownBy(
                        () ->
                                new GetWeightAnalysisService(
                                        getFirstDate, getMeasurementsInRange, null, CLOCK))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("calculate rolling averages must not be null");
    }

    @Test
    void rejectsNullClock() {
        assertThatThrownBy(
                        () ->
                                new GetWeightAnalysisService(
                                        getFirstDate,
                                        getMeasurementsInRange,
                                        calculateRollingAverages,
                                        null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("clock must not be null");
    }
}
