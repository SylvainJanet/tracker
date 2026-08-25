package fr.sylvainjanet.tracker.tracking.application.service;

import fr.sylvainjanet.tracker.tracking.application.port.in.exceptions.DailyRecordNotFoundException;
import fr.sylvainjanet.tracker.tracking.application.port.in.usecase.GetDailyRecordUseCase;
import fr.sylvainjanet.tracker.tracking.application.port.out.gateway.store.DailyRecordStore;
import fr.sylvainjanet.tracker.tracking.domain.DailyRecord;
import java.time.LocalDate;
import java.util.Objects;

public final class GetDailyRecordService implements GetDailyRecordUseCase {

    private final DailyRecordStore store;

    public GetDailyRecordService(DailyRecordStore store) {
        this.store = Objects.requireNonNull(store, "store must not be null");
    }

    @Override
    public DailyRecord execute(LocalDate query) throws DailyRecordNotFoundException {
        Objects.requireNonNull(query, "date must not be null");

        return store.findByDate(query).orElseThrow(() -> new DailyRecordNotFoundException(query));
    }
}
