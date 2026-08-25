package fr.sylvainjanet.tracker.tracking.adapter.in.web.dtos.response;

import fr.sylvainjanet.tracker.tracking.adapter.in.web.dtos.response.enums.CompletionStatusResponse;
import java.time.LocalDate;

public record DailyRecordResponse(LocalDate date, CompletionStatusResponse status) {}
