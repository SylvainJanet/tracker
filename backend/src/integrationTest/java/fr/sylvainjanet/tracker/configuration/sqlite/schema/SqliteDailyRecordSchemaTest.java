package fr.sylvainjanet.tracker.configuration.sqlite.schema;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.configuration.sqlite.environment.SqliteTestDatabase;
import fr.sylvainjanet.tracker.configuration.sqlite.environment.TestSqliteDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;

@SqliteTestDatabase("sqlite-daily-record-schema")
class SqliteDailyRecordSchemaTest {

    private JdbcClient jdbcClient;

    @BeforeEach
    void setUp(TestSqliteDatabase database) {
        jdbcClient = database.jdbcClient();

        jdbcClient.sql("DELETE FROM tracking_daily_record").update();
    }

    @Test
    void acceptsValidDateAndCompletionStatus() {
        insert("2024-02-29", "IN_PROGRESS");

        String status =
                jdbcClient
                        .sql(
                                """
                SELECT completion_status
                FROM tracking_daily_record
                WHERE record_date = :date
                """)
                        .param("date", "2024-02-29")
                        .query(String.class)
                        .single();

        assertThat(status).isEqualTo("IN_PROGRESS");
    }

    @ParameterizedTest
    @ValueSource(strings = {"2025-02-29", "2025-04-31", "2025-2-01", "2025/02/01", "not-a-date"})
    void rejectsInvalidDates(String date) {
        assertThatThrownBy(() -> insert(date, "IN_PROGRESS"))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("CHECK constraint failed");
    }

    @Test
    void rejectsUnknownCompletionStatus() {
        assertThatThrownBy(() -> insert("2025-01-01", "UNKNOWN"))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("CHECK constraint failed");
    }

    @Test
    void rejectsMissingDate() {
        assertThatThrownBy(
                        () ->
                                jdbcClient
                                        .sql(
                                                """
                            INSERT INTO tracking_daily_record (
                                completion_status
                            )
                            VALUES (
                                'IN_PROGRESS'
                            )
                            """)
                                        .update())
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining(
                        "NOT NULL constraint failed: tracking_daily_record.record_date");
    }

    @Test
    void rejectsMissingCompletionStatus() {
        assertThatThrownBy(
                        () ->
                                jdbcClient
                                        .sql(
                                                """
                            INSERT INTO tracking_daily_record (
                                record_date
                            )
                            VALUES (
                                '2025-01-01'
                            )
                            """)
                                        .update())
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining(
                        "NOT NULL constraint failed: tracking_daily_record.completion_status");
    }

    @Test
    void rejectsDuplicateDates() {
        insert("2025-01-01", "IN_PROGRESS");

        assertThatThrownBy(() -> insert("2025-01-01", "COMPLETED"))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("UNIQUE constraint failed");
    }

    private void insert(String date, String completionStatus) {
        jdbcClient
                .sql(
                        """
                INSERT INTO tracking_daily_record (
                    record_date,
                    completion_status
                )
                VALUES (
                    :date,
                    :completionStatus
                )
                """)
                .param("date", date)
                .param("completionStatus", completionStatus)
                .update();
    }
}
