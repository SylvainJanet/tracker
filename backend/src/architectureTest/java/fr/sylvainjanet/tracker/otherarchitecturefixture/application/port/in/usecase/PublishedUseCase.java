package fr.sylvainjanet.tracker.otherarchitecturefixture.application.port.in.usecase;

import fr.sylvainjanet.tracker.otherarchitecturefixture.application.port.in.dtos.command.PublishedCommand;

public interface PublishedUseCase {

    void execute(PublishedCommand command);
}
