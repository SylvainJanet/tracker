package fr.sylvainjanet.tracker.journal.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementInDateRangeQuery;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementInDateRangeResult;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementInDateRangeResult.WeightMeasurementByDateResult;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.criteria.GetWeightMeasurementInDateRangeCriteria;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.GetWeightMeasurementInDateRangeOutcome;
import fr.sylvainjanet.tracker.journal.application.port.out.gateway.store.WeightMeasurementStore;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetWeightMeasurementInDateRangeServiceTest {

    @Mock private WeightMeasurementStore store;

    private GetWeightMeasurementInDateRangeService service;

    @BeforeEach
    void setUp() {
        service = new GetWeightMeasurementInDateRangeService(store);
    }

    @Test
    void getsWeightMeasurementsInTheRequestedDateRange() {
        LocalDate startDate = LocalDate.of(2026, Month.SEPTEMBER, 1);
        LocalDate middleDate = LocalDate.of(2026, Month.SEPTEMBER, 3);
        LocalDate endDate = LocalDate.of(2026, Month.SEPTEMBER, 6);

        BigDecimal startWeight = new BigDecimal("82.10");
        BigDecimal middleWeight = new BigDecimal("81.95");
        BigDecimal endWeight = new BigDecimal("81.80");

        GetWeightMeasurementInDateRangeQuery query =
                new GetWeightMeasurementInDateRangeQuery(startDate, endDate);

        GetWeightMeasurementInDateRangeCriteria criteria =
                new GetWeightMeasurementInDateRangeCriteria(startDate, endDate);

        GetWeightMeasurementInDateRangeOutcome outcome =
                new GetWeightMeasurementInDateRangeOutcome(
                        List.of(
                                new GetWeightMeasurementInDateRangeOutcome
                                        .WeightMeasurementByDateOutcome(startDate, startWeight),
                                new GetWeightMeasurementInDateRangeOutcome
                                        .WeightMeasurementByDateOutcome(middleDate, middleWeight),
                                new GetWeightMeasurementInDateRangeOutcome
                                        .WeightMeasurementByDateOutcome(endDate, endWeight)));

        when(store.getInDateRange(criteria)).thenReturn(outcome);

        GetWeightMeasurementInDateRangeResult result = service.get(query);

        verify(store).getInDateRange(criteria);

        assertThat(result)
                .isEqualTo(
                        new GetWeightMeasurementInDateRangeResult(
                                List.of(
                                        new WeightMeasurementByDateResult(startDate, startWeight),
                                        new WeightMeasurementByDateResult(middleDate, middleWeight),
                                        new WeightMeasurementByDateResult(endDate, endWeight))));
    }

    @Test
    void returnsAnEmptyResultWhenTheStoreReturnsNoMeasurement() {
        LocalDate startDate = LocalDate.of(2026, Month.SEPTEMBER, 1);
        LocalDate endDate = LocalDate.of(2026, Month.SEPTEMBER, 6);

        GetWeightMeasurementInDateRangeCriteria criteria =
                new GetWeightMeasurementInDateRangeCriteria(startDate, endDate);

        when(store.getInDateRange(criteria))
                .thenReturn(new GetWeightMeasurementInDateRangeOutcome(List.of()));

        GetWeightMeasurementInDateRangeResult result =
                service.get(new GetWeightMeasurementInDateRangeQuery(startDate, endDate));

        verify(store).getInDateRange(criteria);

        assertThat(result).isEqualTo(new GetWeightMeasurementInDateRangeResult(List.of()));
    }

    @Test
    void rejectsAStartDateAfterTheEndDate() {
        GetWeightMeasurementInDateRangeQuery query =
                new GetWeightMeasurementInDateRangeQuery(
                        LocalDate.of(2026, Month.SEPTEMBER, 7),
                        LocalDate.of(2026, Month.SEPTEMBER, 6));

        assertThatThrownBy(() -> service.get(query))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("start date must not be after end date");

        verifyNoInteractions(store);
    }

    @Test
    void rejectsANullStartDate() {
        GetWeightMeasurementInDateRangeQuery query =
                new GetWeightMeasurementInDateRangeQuery(
                        null, LocalDate.of(2026, Month.SEPTEMBER, 6));

        assertThatThrownBy(() -> service.get(query))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("start date must not be null");

        verifyNoInteractions(store);
    }

    @Test
    void rejectsANullEndDate() {
        GetWeightMeasurementInDateRangeQuery query =
                new GetWeightMeasurementInDateRangeQuery(
                        LocalDate.of(2026, Month.SEPTEMBER, 1), null);

        assertThatThrownBy(() -> service.get(query))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("end date must not be null");

        verifyNoInteractions(store);
    }

    @Test
    void rejectsAnInvalidWeightFromTheStore() {
        LocalDate date = LocalDate.of(2026, Month.SEPTEMBER, 1);

        GetWeightMeasurementInDateRangeCriteria criteria =
                new GetWeightMeasurementInDateRangeCriteria(date, date);

        when(store.getInDateRange(criteria))
                .thenReturn(
                        new GetWeightMeasurementInDateRangeOutcome(
                                List.of(
                                        new GetWeightMeasurementInDateRangeOutcome
                                                .WeightMeasurementByDateOutcome(
                                                date, new BigDecimal("-82.10")))));

        assertThatThrownBy(() -> service.get(new GetWeightMeasurementInDateRangeQuery(date, date)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "weight must be a positive number of grams that is a multiple of 50 grams");
    }

    @Test
    void rejectsANullQuery() {
        assertThatThrownBy(() -> service.get(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("query must not be null");

        verifyNoInteractions(store);
    }

    @Test
    void rejectsANullStore() {
        assertThatThrownBy(() -> new GetWeightMeasurementInDateRangeService(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("store must not be null");
    }
}
