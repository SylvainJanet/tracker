package fr.sylvainjanet.tracker.journal.application.service;

import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.BY_DATE_CRITERIA;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.BY_DATE_QUERY;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.INVALID_OUTCOME;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.START_OUTCOME;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.START_RESULT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import fr.sylvainjanet.tracker.journal.application.port.out.gateway.store.WeightMeasurementStore;
import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
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
    void returnsWeightMeasurementForDate() {
        when(store.getByDate(BY_DATE_CRITERIA)).thenReturn(Optional.of(START_OUTCOME));

        assertThat(service.get(BY_DATE_QUERY)).contains(START_RESULT);

        verify(store).getByDate(BY_DATE_CRITERIA);
    }

    @Test
    void returnsEmptyWhenNoWeightMeasurementExistsForDate() {
        when(store.getByDate(BY_DATE_CRITERIA)).thenReturn(Optional.empty());

        assertThat(service.get(BY_DATE_QUERY)).isEmpty();

        verify(store).getByDate(BY_DATE_CRITERIA);
    }

    @Test
    void rejectsInvalidStoreOutcome() {
        when(store.getByDate(BY_DATE_CRITERIA)).thenReturn(Optional.of(INVALID_OUTCOME));

        assertThatThrownBy(() -> service.get(BY_DATE_QUERY))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Weight must be positive");

        verify(store).getByDate(BY_DATE_CRITERIA);
    }

    @Test
    void rejectsNullQuery() {
        assertThatThrownBy(() -> service.get(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("query must not be null");

        verifyNoInteractions(store);
    }

    @Test
    void rejectsNullStore() {
        assertThatThrownBy(() -> new GetWeightMeasurementByDateService(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("store must not be null");
    }
}
