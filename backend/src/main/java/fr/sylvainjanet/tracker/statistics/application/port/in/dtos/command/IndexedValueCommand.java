package fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command;

import java.math.BigDecimal;

public record IndexedValueCommand(long index, BigDecimal value) {}
