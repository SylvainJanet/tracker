package fr.sylvainjanet.tracker.journal.adapter.in.web.controller.mapper;

import static fr.sylvainjanet.tracker.journal.application.port.in.dtos.command.builder.LogWeightMeasurementCommandBuilder.aLogWeightMeasurementCommand;

import fr.sylvainjanet.tracker.journal.adapter.in.web.dtos.request.LogWeightMeasurementRequest;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.command.LogWeightMeasurementCommand;

public final class LogWeightMeasurementCommandMapper {

    private LogWeightMeasurementCommandMapper() {}

    public static LogWeightMeasurementCommand command(LogWeightMeasurementRequest request) {
        return aLogWeightMeasurementCommand()
                .withDate(request.date())
                .withWeightInKg(request.weightInKg())
                .build();
    }
}
