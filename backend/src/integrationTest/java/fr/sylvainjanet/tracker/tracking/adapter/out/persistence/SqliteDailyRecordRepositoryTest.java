package fr.sylvainjanet.tracker.tracking.adapter.out.persistence;

import static fr.sylvainjanet.tracker.tracking.domain.builders.DailyRecordTestBuilder.aDailyRecord;
import static org.assertj.core.api.Assertions.assertThat;

import fr.sylvainjanet.tracker.configuration.sqlite.environment.SqliteTestDatabase;
import fr.sylvainjanet.tracker.configuration.sqlite.environment.TestSqliteDatabase;
import fr.sylvainjanet.tracker.tracking.adapter.out.persistence.repository.SqliteDailyRecordRepository;
import fr.sylvainjanet.tracker.tracking.application.port.out.dtos.outcome.DailyRecordCreationOutcome;
import fr.sylvainjanet.tracker.tracking.application.port.out.gateway.store.DailyRecordStore;
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
    class Create {

        @Test
        void savesNewRecord() {
            LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
            float weight = 123.0f;
            DailyRecord instruction = aDailyRecord().withDate(date).withWeightInKg(weight).build();

            assertThat(repository.create(instruction))
                    .isEqualTo(DailyRecordCreationOutcome.CREATED);
        }

        @Test
        void reportsDuplicateDateToApplication() {
            LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
            float weight = 123.0f;
            DailyRecord instruction = aDailyRecord().withDate(date).withWeightInKg(weight).build();
            repository.create(instruction);

            assertThat(repository.create(instruction))
                    .isEqualTo(DailyRecordCreationOutcome.ALREADY_EXISTS);
        }
    }
}
