package fr.sylvainjanet.tracker.journal.adapter.out.persistence.repository.mapper;

import static fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.builder.GetFirstWeightMeasurementDateOutcomeBuilder.aGetFirstWeightMeasurementDateOutcome;

import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.GetFirstWeightMeasurementDateOutcome;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public final class GetFirstWeightMeasurementDateOutcomeMapper {

    private GetFirstWeightMeasurementDateOutcomeMapper() {}

    public static GetFirstWeightMeasurementDateOutcome outcome(ResultSet resultSet)
            throws SQLException {
        return aGetFirstWeightMeasurementDateOutcome()
                .withDate(LocalDate.parse(resultSet.getString("date")))
                .build();
    }
}
