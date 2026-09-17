package fr.sylvainjanet.tracker.journal.adapter.out.persistence;

import static fr.sylvainjanet.tracker.journal.application.port.out.dtos.builders.instruction.LogWeightMeasurementInstructionTestBuilder.aLogWeightMeasurementInstruction;
import static fr.sylvainjanet.tracker.journal.application.port.out.dtos.builders.outcome.LogWeightMeasurementOutcomeTestBuilder.aLogWeightMeasurementOutcome;
import static org.assertj.core.api.Assertions.assertThat;

import fr.sylvainjanet.tracker.configuration.sqlite.environment.SqliteTestDatabase;
import fr.sylvainjanet.tracker.configuration.sqlite.environment.TestSqliteDatabase;
import fr.sylvainjanet.tracker.journal.adapter.out.persistence.repository.LogWeightMeasurementSqliteRepository;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.instruction.LogWeightMeasurementInstruction;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.LogWeightMeasurementOutcome;
import fr.sylvainjanet.tracker.journal.application.port.out.gateway.store.LogWeightMeasurementStore;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.simple.JdbcClient;

@SqliteTestDatabase("log-weight-measurement-sqlite-repository")
class LogWeightMeasurementSqliteRepositoryTest {

    private LogWeightMeasurementStore store;

    @BeforeEach
    void setUp(TestSqliteDatabase database) {
        JdbcClient jdbcClient = database.jdbcClient();
        jdbcClient.sql("DELETE FROM weight_measurement").update();

        store = new LogWeightMeasurementSqliteRepository(jdbcClient);
    }

    @Nested
    class Create {

        @Test
        void savesNewWeightMeasurement() {
            LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
            float weight = 123.0f;
            LogWeightMeasurementInstruction instruction =
                    aLogWeightMeasurementInstruction()
                            .withDate(date)
                            .withWeightInKg(weight)
                            .build();
            LogWeightMeasurementOutcome expectedOutcome =
                    aLogWeightMeasurementOutcome().withDate(date).withWeightInKg(weight).build();

            assertThat(store.log(instruction)).isEqualTo(expectedOutcome);
        }

        @Test
        void updatesExistingWeightMeasurement() {
            LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
            float weight = 123.0f;
            float newWeight = 124.0f;
            LogWeightMeasurementInstruction instruction =
                    aLogWeightMeasurementInstruction()
                            .withDate(date)
                            .withWeightInKg(weight)
                            .build();
            store.log(instruction);

            LogWeightMeasurementInstruction newInstruction =
                    aLogWeightMeasurementInstruction()
                            .withDate(date)
                            .withWeightInKg(newWeight)
                            .build();
            LogWeightMeasurementOutcome expectedOutcome =
                    aLogWeightMeasurementOutcome().withDate(date).withWeightInKg(newWeight).build();

            assertThat(store.log(newInstruction)).isEqualTo(expectedOutcome);
        }
    }
}
