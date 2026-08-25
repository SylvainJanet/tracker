package fr.sylvainjanet.tracker.tracking.adapter.out.persistence.repository;

import fr.sylvainjanet.tracker.tracking.adapter.out.persistence.exceptions.UnsupportedSqliteDailyRecordDateException;
import fr.sylvainjanet.tracker.tracking.application.port.out.dtos.outcome.DailyRecordCreationOutcome;
import fr.sylvainjanet.tracker.tracking.application.port.out.gateway.store.DailyRecordStore;
import fr.sylvainjanet.tracker.tracking.domain.CompletionStatus;
import fr.sylvainjanet.tracker.tracking.domain.DailyRecord;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import org.springframework.jdbc.core.simple.JdbcClient;

public final class SqliteDailyRecordRepository implements DailyRecordStore {

    private final JdbcClient jdbcClient;

    public SqliteDailyRecordRepository(JdbcClient jdbcClient) {
        this.jdbcClient = Objects.requireNonNull(jdbcClient, "jdbcClient must not be null");
    }

    @Override
    public Optional<DailyRecord> findByDate(LocalDate criteria) {
        Objects.requireNonNull(criteria, "date must not be null");

        return jdbcClient
                .sql(
                        """
                    SELECT record_date, completion_status
                    FROM tracking_daily_record
                    WHERE record_date = :date
                    """)
                .param("date", criteria.toString())
                .query(
                        (resultSet, rowNumber) ->
                                DailyRecord.reconstitute(
                                        LocalDate.parse(resultSet.getString("record_date")),
                                        CompletionStatus.valueOf(
                                                resultSet.getString("completion_status"))))
                .optional();
    }

    @Override
    public DailyRecordCreationOutcome create(DailyRecord instruction) {
        Objects.requireNonNull(instruction, "dailyRecord must not be null");

        int insertedRows =
                jdbcClient
                        .sql(
                                """
                    INSERT INTO tracking_daily_record (
                        record_date,
                        completion_status
                    )
                    VALUES (
                        :date,
                        :status
                    )
                    ON CONFLICT (record_date) DO NOTHING
                    """)
                        .param("date", persistedDate(instruction.date()))
                        .param("status", instruction.status().name())
                        .update();

        return insertedRows == 1
                ? DailyRecordCreationOutcome.CREATED
                : DailyRecordCreationOutcome.ALREADY_EXISTS;
    }

    private static String persistedDate(LocalDate date) {
        int year = date.getYear();

        if (year < 0 || year > 9_999) {
            throw new UnsupportedSqliteDailyRecordDateException();
        }

        return date.toString();
    }
}
