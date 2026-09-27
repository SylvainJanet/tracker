package fr.sylvainjanet.tracker.journal.application.service;

import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.FIRST_DATE_OUTCOME;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.FIRST_DATE_RESULT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.sylvainjanet.tracker.journal.application.port.out.gateway.store.WeightMeasurementStore;
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
    void returnsFirstWeightMeasurementDate() {
        when(store.getFirstWeightMeasurementDate()).thenReturn(Optional.of(FIRST_DATE_OUTCOME));

        assertThat(service.get()).contains(FIRST_DATE_RESULT);

        verify(store).getFirstWeightMeasurementDate();
    }

    @Test
    void returnsEmptyWhenNoWeightMeasurementExists() {
        when(store.getFirstWeightMeasurementDate()).thenReturn(Optional.empty());

        assertThat(service.get()).isEmpty();

        verify(store).getFirstWeightMeasurementDate();
    }

    @Test
    void rejectsNullStore() {
        assertThatThrownBy(() -> new GetFirstWeightMeasurementDateService(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("store must not be null");
    }
}
