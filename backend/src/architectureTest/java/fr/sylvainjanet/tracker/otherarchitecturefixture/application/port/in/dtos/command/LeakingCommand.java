package fr.sylvainjanet.tracker.otherarchitecturefixture.application.port.in.dtos.command;

import fr.sylvainjanet.tracker.otherarchitecturefixture.domain.InternalDomain;

public record LeakingCommand(InternalDomain internalDomain) {}
