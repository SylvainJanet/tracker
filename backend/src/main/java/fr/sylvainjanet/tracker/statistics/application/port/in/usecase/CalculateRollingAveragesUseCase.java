package fr.sylvainjanet.tracker.statistics.application.port.in.usecase;

import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.CalculateRollingAveragesCommand;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculateRollingAveragesResult;

public interface CalculateRollingAveragesUseCase {

    /**
     * Calculates trailing averages within the inclusive output-index bounds. An output index is
     * omitted when its window contains no input value.
     */
    CalculateRollingAveragesResult calculate(CalculateRollingAveragesCommand command);
}
