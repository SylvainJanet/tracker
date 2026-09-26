package fr.sylvainjanet.tracker.statistics.application.service;

import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.CalculateRollingAveragesCommand;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculateRollingAveragesResult;
import fr.sylvainjanet.tracker.statistics.application.port.in.usecase.CalculateRollingAveragesUseCase;

public final class CalculateRollingAveragesService implements CalculateRollingAveragesUseCase {

    @Override
    public CalculateRollingAveragesResult calculate(CalculateRollingAveragesCommand command) {
        java.util.Objects.requireNonNull(command, "command must not be null");

        return CalculateRollingAveragesResult.empty();
    }
}
