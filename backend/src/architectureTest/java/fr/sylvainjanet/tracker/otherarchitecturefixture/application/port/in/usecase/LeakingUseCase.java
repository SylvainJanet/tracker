package fr.sylvainjanet.tracker.otherarchitecturefixture.application.port.in.usecase;

import fr.sylvainjanet.tracker.otherarchitecturefixture.application.port.in.dtos.command.LeakingCommand;

public interface LeakingUseCase {

    void execute(LeakingCommand command);
}
