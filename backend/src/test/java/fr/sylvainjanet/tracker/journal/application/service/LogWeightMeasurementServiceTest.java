package fr.sylvainjanet.tracker.journal.application.service;

import static fr.sylvainjanet.tracker.journal.application.port.in.dtos.builders.command.LogWeightMeasurementCommandTestBuilder.aLogWeightMeasurementCommand;
import static fr.sylvainjanet.tracker.journal.application.port.out.dtos.builders.instruction.LogWeightMeasurementInstructionTestBuilder.aLogWeightMeasurementInstruction;
import static fr.sylvainjanet.tracker.journal.application.port.out.dtos.builders.outcome.LogWeightMeasurementOutcomeTestBuilder.aLogWeightMeasurementOutcome;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.command.LogWeightMeasurementCommand;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.LogWeightMeasurementResult;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.LogWeightMeasurementOutcome;
import fr.sylvainjanet.tracker.journal.application.port.out.gateway.store.LogWeightMeasurementStore;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LogWeightMeasurementServiceTest {

    @Mock private LogWeightMeasurementStore store;

    private LogWeightMeasurementService service;

    @BeforeEach
    void setUp() {
        service = new LogWeightMeasurementService(store);
    }

    @Test
    void createsAndPersistsWeightMeasurement() {
        LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
        BigDecimal weight = BigDecimal.valueOf(123.0f).setScale(2, RoundingMode.UNNECESSARY);

        LogWeightMeasurementCommand command =
                aLogWeightMeasurementCommand().withDate(date).withWeightInKg(weight).build();
        LogWeightMeasurementOutcome outcome =
                aLogWeightMeasurementOutcome().withDate(date).withWeightInKg(weight).build();
        when(store.log(any())).thenReturn(outcome);

        LogWeightMeasurementResult result = service.log(command);

        verify(store)
                .log(
                        aLogWeightMeasurementInstruction()
                                .withDate(date)
                                .withWeightInKg(weight)
                                .build());
        assertThat(result.date()).isEqualTo(date);
        assertThat(result.weightInKg()).isEqualTo(weight);
    }

    @Test
    void doesNotStoreInvalidWeightMeasurement() {
        LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
        BigDecimal weight = BigDecimal.valueOf(-123.0f).setScale(2, RoundingMode.UNNECESSARY);

        LogWeightMeasurementCommand command =
                aLogWeightMeasurementCommand().withDate(date).withWeightInKg(weight).build();

        assertThatThrownBy(() -> service.log(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "weight must be a positive number of grams that is a multiple of 50 grams");
        verifyNoInteractions(store);
    }

    @Test
    void rejectsNullCommand() {
        assertThatThrownBy(() -> service.log(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("command must not be null");
    }

    @Test
    void rejectsNullStore() {
        assertThatThrownBy(() -> new LogWeightMeasurementService(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("store must not be null");
    }
}
