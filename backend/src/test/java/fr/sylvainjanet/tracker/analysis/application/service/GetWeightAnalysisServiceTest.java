package fr.sylvainjanet.tracker.analysis.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult.DateRangeResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult.WeightMeasurementResult;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementInDateRangeQuery;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetFirstWeightMeasurementDateResult;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementInDateRangeResult;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementInDateRangeResult.WeightMeasurementByDateResult;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.GetFirstWeightMeasurementDateUseCase;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.GetWeightMeasurementInDateRangeUseCase;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.CalculateRollingAveragesCommand;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.CalculateRollingAveragesCommand.IndexedValueCommand;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculateRollingAveragesResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.usecase.CalculateRollingAveragesUseCase;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetWeightAnalysisServiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, Month.SEPTEMBER, 25);
    private static final Clock CLOCK =
            Clock.fixed(Instant.parse("2026-09-25T12:00:00Z"), ZoneOffset.UTC);

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
    void getsWeightMeasurementsAndRequestsTheirStatistics() {
        LocalDate firstDate = LocalDate.of(2026, Month.SEPTEMBER, 20);
        LocalDate secondDate = LocalDate.of(2026, Month.SEPTEMBER, 23);

        when(getFirstDate.get())
                .thenReturn(Optional.of(new GetFirstWeightMeasurementDateResult(firstDate)));
        when(getMeasurementsInRange.get(new GetWeightMeasurementInDateRangeQuery(firstDate, TODAY)))
                .thenReturn(
                        new GetWeightMeasurementInDateRangeResult(
                                List.of(
                                        new WeightMeasurementByDateResult(
                                                firstDate, new BigDecimal("82.10")),
                                        new WeightMeasurementByDateResult(
                                                secondDate, new BigDecimal("81.90")))));

        CalculateRollingAveragesCommand statisticsCommand =
                new CalculateRollingAveragesCommand(
                        List.of(
                                new IndexedValueCommand(1L, new BigDecimal("82.10")),
                                new IndexedValueCommand(4L, new BigDecimal("81.90"))),
                        List.of());

        when(calculateRollingAverages.calculate(statisticsCommand))
                .thenReturn(CalculateRollingAveragesResult.empty());

        GetWeightAnalysisResult result = service.get();

        assertThat(result)
                .isEqualTo(
                        new GetWeightAnalysisResult(
                                firstDate,
                                new DateRangeResult(firstDate, TODAY),
                                List.of(
                                        new WeightMeasurementResult(
                                                firstDate, 1L, new BigDecimal("82.10")),
                                        new WeightMeasurementResult(
                                                secondDate, 4L, new BigDecimal("81.90"))),
                                List.of()));

        verify(calculateRollingAverages).calculate(statisticsCommand);
    }

    @Test
    void returnsEmptyWhenNoWeightHasBeenLogged() {
        when(getFirstDate.get()).thenReturn(Optional.empty());

        GetWeightAnalysisResult result = service.get();

        assertThat(result).isEqualTo(GetWeightAnalysisResult.empty());
        verifyNoInteractions(getMeasurementsInRange);
    }

    @Test
    void returnsEmptyWhenTheFirstLoggedWeightIsAfterToday() {
        LocalDate futureDate = TODAY.plusDays(1);

        when(getFirstDate.get())
                .thenReturn(Optional.of(new GetFirstWeightMeasurementDateResult(futureDate)));

        GetWeightAnalysisResult result = service.get();

        assertThat(result).isEqualTo(GetWeightAnalysisResult.empty());
        verifyNoInteractions(getMeasurementsInRange);
    }

    @Test
    void returnsEmptyWhenTheDefaultRangeContainsNoMeasurements() {
        LocalDate firstDate = LocalDate.of(2026, Month.SEPTEMBER, 20);

        when(getFirstDate.get())
                .thenReturn(Optional.of(new GetFirstWeightMeasurementDateResult(firstDate)));
        when(getMeasurementsInRange.get(new GetWeightMeasurementInDateRangeQuery(firstDate, TODAY)))
                .thenReturn(new GetWeightMeasurementInDateRangeResult(List.of()));

        GetWeightAnalysisResult result = service.get();

        assertThat(result).isEqualTo(GetWeightAnalysisResult.empty());
    }

    @Test
    void rejectsAnIncoherentAnalysisDatasetPublishedByJournal() {
        LocalDate firstDate = LocalDate.of(2026, Month.SEPTEMBER, 20);
        LocalDate outsideRange = TODAY.plusDays(1);

        when(getFirstDate.get())
                .thenReturn(Optional.of(new GetFirstWeightMeasurementDateResult(firstDate)));
        when(getMeasurementsInRange.get(new GetWeightMeasurementInDateRangeQuery(firstDate, TODAY)))
                .thenReturn(
                        new GetWeightMeasurementInDateRangeResult(
                                List.of(
                                        new WeightMeasurementByDateResult(
                                                outsideRange, new BigDecimal("81.90")))));

        assertThatThrownBy(service::get)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be inside the represented range");
    }
}
