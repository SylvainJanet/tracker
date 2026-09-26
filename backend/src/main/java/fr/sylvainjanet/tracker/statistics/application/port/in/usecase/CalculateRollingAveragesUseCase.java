package fr.sylvainjanet.tracker.statistics.application.port.in.usecase;

import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.CalculateRollingAveragesCommand;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculateRollingAveragesResult;

public interface CalculateRollingAveragesUseCase {

    CalculateRollingAveragesResult calculate(CalculateRollingAveragesCommand command);
}
