package fr.sylvainjanet.tracker.journal.application.service;

import static fr.sylvainjanet.tracker.journal.application.port.in.dtos.builders.query.GetWeightMeasurementByDateQueryTestBuilder.aGetWeightMeasurementByDateQuery;
import static fr.sylvainjanet.tracker.journal.application.port.out.dtos.builders.criteria.GetWeightMeasurementByDateCriteriaTestBuilder.aGetWeightMeasurementByDateCriteria;
import static fr.sylvainjanet.tracker.journal.application.port.out.dtos.builders.outcome.GetWeightMeasurementByDateOutcomeTestBuilder.aGetWeightMeasurementByDateOutcome;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementByDateQuery;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementByDateResult;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.GetWeightMeasurementByDateOutcome;
import fr.sylvainjanet.tracker.journal.application.port.out.gateway.store.WeightMeasurementStore;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetWeightMeasurementByDateServiceTest {

    @Mock private WeightMeasurementStore store;

    private GetWeightMeasurementByDateService service;

    @BeforeEach
    void setUp() {
        service = new GetWeightMeasurementByDateService(store);
    }

    @Test
    void getALoggedWeightMeasurementByDate() {
        LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
        BigDecimal weight = BigDecimal.valueOf(123.0f).setScale(2, RoundingMode.UNNECESSARY);

        GetWeightMeasurementByDateQuery query =
                aGetWeightMeasurementByDateQuery().withDate(date).build();
        GetWeightMeasurementByDateOutcome outcome =
                aGetWeightMeasurementByDateOutcome().withDate(date).withWeightInKg(weight).build();
        when(store.getByDate(any())).thenReturn(Optional.ofNullable(outcome));

        Optional<GetWeightMeasurementByDateResult> result = service.get(query);

        verify(store).getByDate(aGetWeightMeasurementByDateCriteria().withDate(date).build());
        assertThat(result).isPresent();
        assertThat(result.get().date()).isEqualTo(date);
        assertThat(result.get().weightInKg()).isEqualTo(weight);
    }

    @Test
    void doesNotFindAMissingWeightMeasurementByDate() {
        LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);

        GetWeightMeasurementByDateQuery query =
                aGetWeightMeasurementByDateQuery().withDate(date).build();
        when(store.getByDate(any())).thenReturn(Optional.empty());

        Optional<GetWeightMeasurementByDateResult> result = service.get(query);

        verify(store).getByDate(aGetWeightMeasurementByDateCriteria().withDate(date).build());
        assertThat(result).isEmpty();
    }

    @Test
    void shouldRejectInvalidOutcome() {
        LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
        BigDecimal weight = BigDecimal.valueOf(-123.0f).setScale(2, RoundingMode.UNNECESSARY);

        GetWeightMeasurementByDateQuery query =
                aGetWeightMeasurementByDateQuery().withDate(date).build();
        GetWeightMeasurementByDateOutcome outcome =
                aGetWeightMeasurementByDateOutcome().withDate(date).withWeightInKg(weight).build();
        when(store.getByDate(any())).thenReturn(Optional.ofNullable(outcome));

        assertThatThrownBy(() -> service.get(query))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "weight must be a positive number of grams that is a multiple of 50 grams");
    }

    @Test
    void rejectsNullQuery() {
        assertThatThrownBy(() -> service.get(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("query must not be null");
    }

    @Test
    void rejectsNullStore() {
        assertThatThrownBy(() -> new GetWeightMeasurementByDateService(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("store must not be null");
    }
}
