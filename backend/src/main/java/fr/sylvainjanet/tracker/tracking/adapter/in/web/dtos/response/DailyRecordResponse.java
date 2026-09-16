package fr.sylvainjanet.tracker.tracking.adapter.in.web.dtos.response;

import java.time.LocalDate;

public record DailyRecordResponse(LocalDate date, Float weight) {}
