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

@SqliteTestDatabase("weight-measurement-sqlite-schema")
class WeightMeasurementSqliteSchemaTest {

    private JdbcClient jdbcClient;

    @BeforeEach
    void setUp(TestSqliteDatabase database) {
        jdbcClient = database.jdbcClient();

        jdbcClient.sql("DELETE FROM weight_measurement").update();
    }

    @Test
    void acceptsValidDateAndWeight() {
        insert("2024-02-29", "123.0");

        String weight =
                jdbcClient
                        .sql(
                                """
                SELECT weight_in_kg
                FROM weight_measurement
                WHERE date = :date
                """)
                        .param("date", "2024-02-29")
                        .query(String.class)
                        .single();

        assertThat(weight).isEqualTo("123.0");
    }

    @ParameterizedTest
    @ValueSource(strings = {"2025-02-29", "2025-04-31", "2025-2-01", "2025/02/01", "not-a-date"})
    void rejectsInvalidDates(String date) {
        assertThatThrownBy(() -> insert(date, "123.0"))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("CHECK constraint failed");
    }

    @Test
    void rejectsNegativeWeight() {
        assertThatThrownBy(() -> insert("2025-01-01", "-123.0"))
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
                    INSERT INTO weight_measurement (
                        weight_in_kg
                    )
                    VALUES (
                        '123.0'
                    )
                    """)
                                        .update())
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("NOT NULL constraint failed: weight_measurement.date");
    }

    @Test
    void rejectsMissingWeight() {
        assertThatThrownBy(
                        () ->
                                jdbcClient
                                        .sql(
                                                """
                    INSERT INTO weight_measurement (
                        date
                    )
                    VALUES (
                        '2025-01-01'
                    )
                    """)
                                        .update())
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining(
                        "NOT NULL constraint failed: weight_measurement.weight_in_kg");
    }

    @Test
    void rejectsMissingCompletionStatus() {
        assertThatThrownBy(
                        () ->
                                jdbcClient
                                        .sql(
                                                """
                            INSERT INTO weight_measurement (
                                date
                            )
                            VALUES (
                                '2025-01-01'
                            )
                            """)
                                        .update())
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining(
                        "NOT NULL constraint failed: weight_measurement.weight_in_kg");
    }

    private void insert(String date, String weight) {
        jdbcClient
                .sql(
                        """
                INSERT INTO weight_measurement (
                    date,
                    weight_in_kg
                )
                VALUES (
                    :date,
                    :weight
                )
                """)
                .param("date", date)
                .param("weight", weight)
                .update();
    }
}
