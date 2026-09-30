package fr.sylvainjanet.tracker.journal.adapter.out.persistence.repository;

import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.AFTER_RANGE_LOG_INSTRUCTION;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.BEFORE_RANGE_LOG_INSTRUCTION;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.BY_DATE_CRITERIA;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.DATE_RANGE_CRITERIA;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.DATE_RANGE_OUTCOME;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.EMPTY_DATE_RANGE_OUTCOME;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.END_LOG_INSTRUCTION;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.FIRST_DATE_OUTCOME;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.LOG_INSTRUCTION;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.MIDDLE_LOG_INSTRUCTION;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.START_OUTCOME;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.UPDATED_START_OUTCOME;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.UPDATE_LOG_INSTRUCTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.configuration.sqlite.environment.SqliteTestDatabase;
import fr.sylvainjanet.tracker.configuration.sqlite.environment.TestSqliteDatabase;
import fr.sylvainjanet.tracker.journal.application.port.out.gateway.store.WeightMeasurementStore;
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
    class Log {

        @Test
        void savesNewWeightMeasurement() {
            assertThat(store.log(LOG_INSTRUCTION)).isEqualTo(START_OUTCOME);
        }

        @Test
        void updatesExistingWeightMeasurement() {
            store.log(LOG_INSTRUCTION);

            assertThat(store.log(UPDATE_LOG_INSTRUCTION)).isEqualTo(UPDATED_START_OUTCOME);
            assertThat(store.getByDate(BY_DATE_CRITERIA)).contains(UPDATED_START_OUTCOME);
        }

        @Test
        void rejectsNullInstruction() {
            assertThatThrownBy(() -> store.log(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("instruction must not be null");
        }
    }

    @Nested
    class GetByDate {

        @Test
        void returnsLoggedWeightMeasurement() {
            store.log(LOG_INSTRUCTION);

            assertThat(store.getByDate(BY_DATE_CRITERIA)).contains(START_OUTCOME);
        }

        @Test
        void returnsEmptyWhenWeightMeasurementDoesNotExist() {
            assertThat(store.getByDate(BY_DATE_CRITERIA)).isEmpty();
        }

        @Test
        void rejectsNullCriteria() {
            assertThatThrownBy(() -> store.getByDate(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("criteria must not be null");
        }
    }

    @Nested
    class GetInDateRange {

        @Test
        void returnsInclusiveRangeOrderedByDateWithoutMissingPlaceholders() {
            store.log(AFTER_RANGE_LOG_INSTRUCTION);
            store.log(MIDDLE_LOG_INSTRUCTION);
            store.log(END_LOG_INSTRUCTION);
            store.log(BEFORE_RANGE_LOG_INSTRUCTION);
            store.log(LOG_INSTRUCTION);

            assertThat(store.getInDateRange(DATE_RANGE_CRITERIA)).isEqualTo(DATE_RANGE_OUTCOME);
        }

        @Test
        void returnsEmptyResultWhenRangeContainsNoMeasurement() {
            assertThat(store.getInDateRange(DATE_RANGE_CRITERIA))
                    .isEqualTo(EMPTY_DATE_RANGE_OUTCOME);
        }

        @Test
        void rejectsNullCriteria() {
            assertThatThrownBy(() -> store.getInDateRange(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("criteria must not be null");
        }
    }

    @Nested
    class GetFirstWeightMeasurementDate {

        @Test
        void returnsEarliestWeightMeasurementDate() {
            store.log(END_LOG_INSTRUCTION);
            store.log(LOG_INSTRUCTION);
            store.log(MIDDLE_LOG_INSTRUCTION);

            assertThat(store.getFirstWeightMeasurementDate()).contains(FIRST_DATE_OUTCOME);
        }

        @Test
        void returnsEmptyWhenNoWeightMeasurementExists() {
            assertThat(store.getFirstWeightMeasurementDate()).isEmpty();
        }
    }
}
