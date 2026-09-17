package fr.sylvainjanet.tracker.tracking.adapter.out.persistence.repository;

import fr.sylvainjanet.tracker.tracking.adapter.out.persistence.exceptions.UnsupportedSqliteDailyRecordDateException;
import fr.sylvainjanet.tracker.tracking.adapter.out.persistence.exceptions.UnsupportedSqliteDailyRecordWeightException;
import fr.sylvainjanet.tracker.tracking.application.port.out.dtos.outcome.DailyRecordCreationOutcome;
import fr.sylvainjanet.tracker.tracking.application.port.out.gateway.store.DailyRecordStore;
import fr.sylvainjanet.tracker.tracking.domain.DailyRecord;
import fr.sylvainjanet.tracker.tracking.domain.Weight;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.jdbc.core.simple.JdbcClient;

public final class SqliteDailyRecordRepository implements DailyRecordStore {

    private final JdbcClient jdbcClient;

    public SqliteDailyRecordRepository(JdbcClient jdbcClient) {
        this.jdbcClient = Objects.requireNonNull(jdbcClient, "jdbcClient must not be null");
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
                        weight
                    )
                    VALUES (
                        :date,
                        :weight
                    )
                    ON CONFLICT (record_date) DO NOTHING
                    """)
                        .param("date", persistedDate(instruction.date()))
                        .param("weight", persistedWeight(instruction.weight()))
                        .update();

        return insertedRows == 1
                ? DailyRecordCreationOutcome.CREATED
                : DailyRecordCreationOutcome.ALREADY_EXISTS;
    }

    private static Float persistedWeight(Weight weight) {
        Float kilograms = weight.kilograms();

        if (kilograms < 0) {
            throw new UnsupportedSqliteDailyRecordWeightException();
        }

        return kilograms;
    }

    private static String persistedDate(LocalDate date) {
        int year = date.getYear();

        if (year < 0 || year > 9_999) {
            throw new UnsupportedSqliteDailyRecordDateException();
        }

        return date.toString();
    }
}
