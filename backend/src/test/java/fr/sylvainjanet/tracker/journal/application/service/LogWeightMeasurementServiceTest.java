package fr.sylvainjanet.tracker.journal.application.service;

import static fr.sylvainjanet.tracker.journal.application.port.in.dtos.builders.command.LogWeightMeasurementCommandTestBuilder.aLogWeightMeasurementCommand;
import static fr.sylvainjanet.tracker.journal.application.port.out.dtos.builders.instruction.LogWeightMeasurementInstructionTestBuilder.aLogWeightMeasurementInstruction;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.journal.adapter.out.persistence.LogWeightMeasurementInMemoryStore;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.command.LogWeightMeasurementCommand;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.LogWeightMeasurementResult;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.instruction.LogWeightMeasurementInstruction;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LogWeightMeasurementServiceTest {

    private final LogWeightMeasurementInMemoryStore repository =
            new LogWeightMeasurementInMemoryStore();
    private final LogWeightMeasurementService service = new LogWeightMeasurementService(repository);

    @BeforeEach
    void setUp() {
        repository.clear();
    }

    @Test
    void createsAndPersistsWeightMeasurement() {
        LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
        float weight = 123.0f;

        LogWeightMeasurementCommand command =
                aLogWeightMeasurementCommand().withDate(date).withWeightInKg(weight).build();

        LogWeightMeasurementResult result = service.log(command);

        assertThat(result.date()).isEqualTo(date);
        assertThat(result.weightInKg()).isEqualTo(weight);
    }

    @Test
    void updatesWeightMeasurement() {
        LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
        float weight = 123.0f;
        float newWeight = 124.0f;
        LogWeightMeasurementCommand command =
                aLogWeightMeasurementCommand().withDate(date).withWeightInKg(newWeight).build();
        LogWeightMeasurementInstruction instruction =
                aLogWeightMeasurementInstruction().withDate(date).withWeightInKg(weight).build();
        repository.log(instruction);

        LogWeightMeasurementResult result = service.log(command);

        assertThat(result.date()).isEqualTo(date);
        assertThat(result.weightInKg()).isEqualTo(newWeight);
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
