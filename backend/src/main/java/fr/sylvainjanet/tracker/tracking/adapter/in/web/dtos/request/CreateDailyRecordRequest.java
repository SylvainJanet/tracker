package fr.sylvainjanet.tracker.tracking.adapter.in.web.dtos.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateDailyRecordRequest(@NotNull LocalDate date) {}
