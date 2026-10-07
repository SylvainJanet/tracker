package fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response;

import java.time.LocalDate;

public record AnalysisDateRangeResponse(LocalDate startDate, LocalDate endDate) {}
