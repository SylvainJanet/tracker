package fr.sylvainjanet.tracker.journal.adapter.out.persistence;

import static fr.sylvainjanet.tracker.journal.application.port.out.dtos.builders.criteria.GetWeightMeasurementByDateCriteriaTestBuilder.aGetWeightMeasurementByDateCriteria;
import static fr.sylvainjanet.tracker.journal.application.port.out.dtos.builders.instruction.LogWeightMeasurementInstructionTestBuilder.aLogWeightMeasurementInstruction;
import static fr.sylvainjanet.tracker.journal.application.port.out.dtos.builders.outcome.GetWeightMeasurementByDateOutcomeTestBuilder.aGetWeightMeasurementByDateOutcome;
import static fr.sylvainjanet.tracker.journal.application.port.out.dtos.builders.outcome.LogWeightMeasurementOutcomeTestBuilder.aLogWeightMeasurementOutcome;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.configuration.sqlite.environment.SqliteTestDatabase;
import fr.sylvainjanet.tracker.configuration.sqlite.environment.TestSqliteDatabase;
import fr.sylvainjanet.tracker.journal.adapter.out.persistence.repository.WeightMeasurementSqliteRepository;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.criteria.GetWeightMeasurementByDateCriteria;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.instruction.LogWeightMeasurementInstruction;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.GetWeightMeasurementByDateOutcome;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.LogWeightMeasurementOutcome;
import fr.sylvainjanet.tracker.journal.application.port.out.gateway.store.WeightMeasurementStore;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.simple.JdbcClient;

@SqliteTestDatabase("log-weight-measurement-sqlite-repository")
class WeightMeasurementSqliteRepositoryTest {

    private WeightMeasurementStore store;

    @BeforeEach
    void setUp(TestSqliteDatabase database) {
        JdbcClient jdbcClient = database.jdbcClient();
        jdbcClient.sql("DELETE FROM weight_measurement").update();

        store = new WeightMeasurementSqliteRepository(jdbcClient);
    }

    @Nested
    class Create {

        @Test
        void savesNewWeightMeasurement() {
            LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
            BigDecimal weight = BigDecimal.valueOf(1234);
            LogWeightMeasurementInstruction instruction =
                    aLogWeightMeasurementInstruction()
                            .withDate(date)
                            .withWeightInKg(weight)
                            .build();
            LogWeightMeasurementOutcome expectedOutcome =
                    aLogWeightMeasurementOutcome()
                            .withDate(date)
                            .withWeightInKg(weight.setScale(2, RoundingMode.UNNECESSARY))
                            .build();

            assertThat(store.log(instruction)).isEqualTo(expectedOutcome);
        }

        @Test
        void updatesExistingWeightMeasurement() {
            LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
            BigDecimal weight = BigDecimal.valueOf(1234);
            BigDecimal newWeight = BigDecimal.valueOf(1244);
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
                    aLogWeightMeasurementOutcome()
                            .withDate(date)
                            .withWeightInKg(newWeight.setScale(2, RoundingMode.UNNECESSARY))
                            .build();

            assertThat(store.log(newInstruction)).isEqualTo(expectedOutcome);
        }

        @Test
        void rejectsNullInstruction() {
            assertThatThrownBy(() -> store.log(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("instruction must not be null");
        }
    }

    @Nested
    class Get {
        @Test
        void getALoggedWeightMeasurementByDate() {
            LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
            BigDecimal weight = BigDecimal.valueOf(1234);
            LogWeightMeasurementInstruction instruction =
                    aLogWeightMeasurementInstruction()
                            .withDate(date)
                            .withWeightInKg(weight)
                            .build();
            store.log(instruction);

            GetWeightMeasurementByDateCriteria criteria =
                    aGetWeightMeasurementByDateCriteria().withDate(date).build();
            GetWeightMeasurementByDateOutcome expectedOutcome =
                    aGetWeightMeasurementByDateOutcome()
                            .withDate(date)
                            .withWeightInKg(weight.setScale(2, RoundingMode.UNNECESSARY))
                            .build();

            assertThat(store.getByDate(criteria)).isPresent().get().isEqualTo(expectedOutcome);
        }

        @Test
        void doesNotFindAMissingWeightMeasurementByDate() {
            LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);

            GetWeightMeasurementByDateCriteria criteria =
                    aGetWeightMeasurementByDateCriteria().withDate(date).build();

            assertThat(store.getByDate(criteria)).isEmpty();
        }

        @Test
        void rejectsNullCriteria() {
            assertThatThrownBy(() -> store.getByDate(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("criteria must not be null");
        }
    }
}
