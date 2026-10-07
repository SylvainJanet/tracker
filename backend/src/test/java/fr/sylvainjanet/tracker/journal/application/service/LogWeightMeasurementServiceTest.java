package fr.sylvainjanet.tracker.journal.application.service;

import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.INVALID_LOG_COMMAND;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.INVALID_OUTCOME;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.LOG_COMMAND;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.LOG_INSTRUCTION;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.START_OUTCOME;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.START_RESULT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import fr.sylvainjanet.tracker.journal.application.port.out.gateway.store.WeightMeasurementStore;
import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LogWeightMeasurementServiceTest {

    @Mock private WeightMeasurementStore store;

    private LogWeightMeasurementService service;

    @BeforeEach
    void setUp() {
        service = new LogWeightMeasurementService(store);
    }

    @Test
    void logsWeightMeasurement() {
        when(store.log(LOG_INSTRUCTION)).thenReturn(START_OUTCOME);

        assertThat(service.log(LOG_COMMAND)).isEqualTo(START_RESULT);

        verify(store).log(LOG_INSTRUCTION);
    }

    @Test
    void rejectsInvalidCommandBeforeCallingStore() {
        assertThatThrownBy(() -> service.log(INVALID_LOG_COMMAND))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Weight must be positive");

        verifyNoInteractions(store);
    }

    @Test
    void rejectsInvalidStoreOutcome() {
        when(store.log(LOG_INSTRUCTION)).thenReturn(INVALID_OUTCOME);

        assertThatThrownBy(() -> service.log(LOG_COMMAND))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Weight must be positive");

        verify(store).log(LOG_INSTRUCTION);
    }

    @Test
    void rejectsNullCommand() {
        assertThatThrownBy(() -> service.log(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("command must not be null");

        verifyNoInteractions(store);
    }

    @Test
    void rejectsNullStore() {
        assertThatThrownBy(() -> new LogWeightMeasurementService(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("store must not be null");
    }
}
