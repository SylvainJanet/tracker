package fr.sylvainjanet.tracker.journal.adapter.out.persistence.repository;

import fr.sylvainjanet.tracker.journal.adapter.out.persistence.exceptions.UnsupportedDateSqliteException;
import fr.sylvainjanet.tracker.journal.adapter.out.persistence.exceptions.UnsupportedWeightSqliteException;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.instruction.LogWeightMeasurementInstruction;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.LogWeightMeasurementOutcome;
import fr.sylvainjanet.tracker.journal.application.port.out.gateway.store.LogWeightMeasurementStore;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.jdbc.core.simple.JdbcClient;

public final class LogWeightMeasurementSqliteRepository implements LogWeightMeasurementStore {

    private final JdbcClient jdbcClient;

    public LogWeightMeasurementSqliteRepository(JdbcClient jdbcClient) {
        this.jdbcClient = Objects.requireNonNull(jdbcClient, "jdbcClient must not be null");
    }

    @Override
    public LogWeightMeasurementOutcome log(LogWeightMeasurementInstruction instruction) {
        Objects.requireNonNull(instruction, "instruction must not be null");

        return jdbcClient
                .sql(
                        """
                INSERT INTO weight_measurement (
                    date,
                    weight_in_kg
                )
                VALUES (
                    :date,
                    :weight_in_kg
                )
                ON CONFLICT (date) DO UPDATE SET
                    weight_in_kg = excluded.weight_in_kg
                RETURNING
                    date,
                    weight_in_kg
                """)
                .param("date", persistedDate(instruction.date()))
                .param("weight_in_kg", persistedWeight(instruction.weightInKg()))
                .query((resultSet, rowNum) -> toOutcome(resultSet, rowNum))
                .single();
    }

    private LogWeightMeasurementOutcome toOutcome(ResultSet resultSet, int rowNum)
            throws SQLException {
        return new LogWeightMeasurementOutcome(
                LocalDate.parse(resultSet.getString("date")), resultSet.getFloat("weight_in_kg"));
    }

    private static Float persistedWeight(Float weightInKilograms) {
        if (weightInKilograms < 0) {
            throw new UnsupportedWeightSqliteException();
        }

        return weightInKilograms;
    }

    private static String persistedDate(LocalDate date) {
        int year = date.getYear();

        if (year < 0 || year > 9_999) {
            throw new UnsupportedDateSqliteException();
        }

        return date.toString();
    }
}
