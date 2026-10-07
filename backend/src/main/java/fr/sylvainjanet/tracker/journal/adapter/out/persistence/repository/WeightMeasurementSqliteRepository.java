package fr.sylvainjanet.tracker.journal.adapter.out.persistence.repository;

import fr.sylvainjanet.tracker.journal.adapter.out.persistence.exceptions.UnsupportedDateSqliteException;
import fr.sylvainjanet.tracker.journal.adapter.out.persistence.exceptions.UnsupportedWeightSqliteException;
import fr.sylvainjanet.tracker.journal.adapter.out.persistence.repository.mapper.GetFirstWeightMeasurementDateOutcomeMapper;
import fr.sylvainjanet.tracker.journal.adapter.out.persistence.repository.mapper.WeightMeasurementOutcomeMapper;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.criteria.GetWeightMeasurementByDateCriteria;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.criteria.GetWeightMeasurementInDateRangeCriteria;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.instruction.LogWeightMeasurementInstruction;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.GetFirstWeightMeasurementDateOutcome;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.GetWeightMeasurementInDateRangeOutcome;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.WeightMeasurementOutcome;
import fr.sylvainjanet.tracker.journal.application.port.out.gateway.store.WeightMeasurementStore;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.jdbc.core.simple.JdbcClient;

public final class WeightMeasurementSqliteRepository implements WeightMeasurementStore {

    private final JdbcClient jdbcClient;

    public WeightMeasurementSqliteRepository(JdbcClient jdbcClient) {
        this.jdbcClient = Objects.requireNonNull(jdbcClient, "jdbcClient must not be null");
    }

    @Override
    public WeightMeasurementOutcome log(LogWeightMeasurementInstruction instruction) {
        Objects.requireNonNull(instruction, "instruction must not be null");

        return jdbcClient
                .sql(
                        """
                INSERT INTO weight_measurement (
                    date,
                    weight_in_g
                )
                VALUES (
                    :date,
                    :weight_in_g
                )
                ON CONFLICT (date) DO UPDATE SET
                    weight_in_g = excluded.weight_in_g
                RETURNING
                    date,
                    weight_in_g
                """)
                .param("date", persistedDate(instruction.date()))
                .param("weight_in_g", persistedWeight(instruction.weightInKg()))
                .query((resultSet, i) -> WeightMeasurementOutcomeMapper.outcome(resultSet))
                .single();
    }

    @Override
    public Optional<WeightMeasurementOutcome> getByDate(
            GetWeightMeasurementByDateCriteria criteria) {
        Objects.requireNonNull(criteria, "criteria must not be null");

        return jdbcClient
                .sql(
                        """
                SELECT
                    date,
                    weight_in_g
                FROM weight_measurement
                WHERE date = :date
                """)
                .param("date", persistedDate(criteria.date()))
                .query((resultSet, i) -> WeightMeasurementOutcomeMapper.outcome(resultSet))
                .optional();
    }

    @Override
    public GetWeightMeasurementInDateRangeOutcome getInDateRange(
            GetWeightMeasurementInDateRangeCriteria criteria) {
        Objects.requireNonNull(criteria, "criteria must not be null");

        List<WeightMeasurementOutcome> measurements =
                jdbcClient
                        .sql(
                                """
                        SELECT
                            date,
                            weight_in_g
                        FROM weight_measurement
                        WHERE date >= :start_date
                          AND date <= :end_date
                        ORDER BY date
                        """)
                        .param("start_date", persistedDate(criteria.startDate()))
                        .param("end_date", persistedDate(criteria.endDate()))
                        .query((resultSet, i) -> WeightMeasurementOutcomeMapper.outcome(resultSet))
                        .list();

        return new GetWeightMeasurementInDateRangeOutcome(measurements);
    }

    @Override
    public Optional<GetFirstWeightMeasurementDateOutcome> getFirstWeightMeasurementDate() {
        return jdbcClient
                .sql(
                        """
                SELECT date
                FROM weight_measurement
                ORDER BY date
                LIMIT 1
                """)
                .query(
                        (resultSet, i) ->
                                GetFirstWeightMeasurementDateOutcomeMapper.outcome(resultSet))
                .optional();
    }

    private static BigDecimal persistedWeight(BigDecimal weightInKilograms) {
        if (weightInKilograms.compareTo(BigDecimal.ZERO) < 0) {
            throw new UnsupportedWeightSqliteException();
        }

        return weightInKilograms.multiply(BigDecimal.valueOf(1000));
    }

    private static String persistedDate(LocalDate date) {
        int year = date.getYear();

        if (year < 0 || year > 9_999) {
            throw new UnsupportedDateSqliteException();
        }

        return date.toString();
    }
}
