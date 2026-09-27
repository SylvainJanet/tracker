package fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result;

import java.time.LocalDate;

public record AnalysisDateRangeResult(LocalDate startDate, LocalDate endDate) {}
