package fr.sylvainjanet.tracker.tracking.application.service;

import fr.sylvainjanet.tracker.tracking.application.port.in.exceptions.DailyRecordAlreadyExistsException;
import fr.sylvainjanet.tracker.tracking.application.port.in.usecase.CreateDailyRecordUseCase;
import fr.sylvainjanet.tracker.tracking.application.port.out.gateway.store.DailyRecordStore;
import fr.sylvainjanet.tracker.tracking.domain.DailyRecord;
import java.time.LocalDate;
import java.util.Objects;

public final class CreateDailyRecordService implements CreateDailyRecordUseCase {

    private final DailyRecordStore store;

    public CreateDailyRecordService(DailyRecordStore store) {
        this.store = Objects.requireNonNull(store, "store must not be null");
    }

    @Override
    public DailyRecord execute(LocalDate command) throws DailyRecordAlreadyExistsException {
        DailyRecord instruction = DailyRecord.create(command);

        return switch (store.create(instruction)) {
            case CREATED -> instruction;
            case ALREADY_EXISTS -> throw new DailyRecordAlreadyExistsException(command);
        };
    }
}
