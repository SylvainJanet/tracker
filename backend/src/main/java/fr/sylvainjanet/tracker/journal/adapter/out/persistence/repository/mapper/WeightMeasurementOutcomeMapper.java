package fr.sylvainjanet.tracker.journal.adapter.out.persistence.repository.mapper;

import static fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.builder.WeightMeasurementOutcomeBuilder.aWeightMeasurementOutcome;

import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.WeightMeasurementOutcome;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public final class WeightMeasurementOutcomeMapper {

    private WeightMeasurementOutcomeMapper() {}

    public static WeightMeasurementOutcome outcome(ResultSet resultSet) throws SQLException {
        return aWeightMeasurementOutcome()
                .withDate(resultSet.getObject("date", LocalDate.class))
                .withWeightInKg(
                        resultSet
                                .getBigDecimal("weight_in_g")
                                .divide(BigDecimal.valueOf(1000), 2, RoundingMode.UNNECESSARY))
                .build();
    }
}
