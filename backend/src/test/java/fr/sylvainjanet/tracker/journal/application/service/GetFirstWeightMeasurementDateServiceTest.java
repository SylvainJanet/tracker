package fr.sylvainjanet.tracker.journal.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetFirstWeightMeasurementDateResult;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.GetFirstWeightMeasurementDateOutcome;
import fr.sylvainjanet.tracker.journal.application.port.out.gateway.store.WeightMeasurementStore;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetFirstWeightMeasurementDateServiceTest {

    @Mock private WeightMeasurementStore store;

    private GetFirstWeightMeasurementDateService service;

    @BeforeEach
    void setUp() {
        service = new GetFirstWeightMeasurementDateService(store);
    }

    @Test
    void getsTheFirstWeightMeasurementDate() {
        LocalDate measurementDate = LocalDate.of(2026, Month.AUGUST, 25);

        when(store.getFirstWeightMeasurementDate())
                .thenReturn(Optional.of(new GetFirstWeightMeasurementDateOutcome(measurementDate)));

        Optional<GetFirstWeightMeasurementDateResult> result = service.get();

        verify(store).getFirstWeightMeasurementDate();

        assertThat(result).contains(new GetFirstWeightMeasurementDateResult(measurementDate));
    }

    @Test
    void returnsEmptyWhenNoWeightMeasurementExists() {
        when(store.getFirstWeightMeasurementDate()).thenReturn(Optional.empty());

        Optional<GetFirstWeightMeasurementDateResult> result = service.get();

        verify(store).getFirstWeightMeasurementDate();

        assertThat(result).isEmpty();
    }

    @Test
    void rejectsANullStore() {
        assertThatThrownBy(() -> new GetFirstWeightMeasurementDateService(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("store must not be null");
    }
}
