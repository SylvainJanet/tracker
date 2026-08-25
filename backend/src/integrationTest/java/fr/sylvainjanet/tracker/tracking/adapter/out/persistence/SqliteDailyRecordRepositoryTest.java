package fr.sylvainjanet.tracker.tracking.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import fr.sylvainjanet.tracker.configuration.sqlite.environment.SqliteTestDatabase;
import fr.sylvainjanet.tracker.configuration.sqlite.environment.TestSqliteDatabase;
import fr.sylvainjanet.tracker.tracking.adapter.out.persistence.repository.SqliteDailyRecordRepository;
import fr.sylvainjanet.tracker.tracking.application.port.out.dtos.outcome.DailyRecordCreationOutcome;
import fr.sylvainjanet.tracker.tracking.application.port.out.gateway.store.DailyRecordStore;
import fr.sylvainjanet.tracker.tracking.domain.CompletionStatus;
import fr.sylvainjanet.tracker.tracking.domain.DailyRecord;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.simple.JdbcClient;

@SqliteTestDatabase("sqlite-daily-record-repository")
class SqliteDailyRecordRepositoryTest {

    private DailyRecordStore repository;

    @BeforeEach
    void setUp(TestSqliteDatabase database) {
        JdbcClient jdbcClient = database.jdbcClient();
        jdbcClient.sql("DELETE FROM tracking_daily_record").update();

        repository = new SqliteDailyRecordRepository(jdbcClient);
    }

    @Nested
    class FindByDate {

        @Test
        void returnsEmptyWhenNoRecordExistsForDate() {
            LocalDate criteria = LocalDate.of(2026, Month.AUGUST, 25);

            assertThat(repository.findByDate(criteria)).isEmpty();
        }

        @Test
        void returnsFoundDailyRecord() {
            LocalDate criteria = LocalDate.of(2026, Month.AUGUST, 24);
            repository.create(DailyRecord.reconstitute(criteria, CompletionStatus.COMPLETED));

            DailyRecord found = repository.findByDate(criteria).orElseThrow();

            assertThat(found.date()).isEqualTo(criteria);
            assertThat(found.status()).isEqualTo(CompletionStatus.COMPLETED);
        }
    }

    @Nested
    class Create {

        @Test
        void savesAndFindsNewRecord() {
            LocalDate criteria = LocalDate.of(2026, Month.AUGUST, 25);

            assertThat(repository.create(DailyRecord.create(criteria)))
                    .isEqualTo(DailyRecordCreationOutcome.CREATED);

            DailyRecord found = repository.findByDate(criteria).orElseThrow();

            assertThat(found.date()).isEqualTo(criteria);
            assertThat(found.status()).isEqualTo(CompletionStatus.IN_PROGRESS);
        }

        @Test
        void reportsDuplicateDateToApplication() {
            LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
            repository.create(DailyRecord.reconstitute(date, CompletionStatus.IN_PROGRESS));

            assertThat(
                            repository.create(
                                    DailyRecord.reconstitute(date, CompletionStatus.COMPLETED)))
                    .isEqualTo(DailyRecordCreationOutcome.ALREADY_EXISTS);
        }
    }
}
