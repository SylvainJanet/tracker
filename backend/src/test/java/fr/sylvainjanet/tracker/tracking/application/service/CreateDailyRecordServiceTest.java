package fr.sylvainjanet.tracker.tracking.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.tracking.adapter.out.persistence.InMemoryDailyRecordStore;
import fr.sylvainjanet.tracker.tracking.application.port.in.exceptions.DailyRecordAlreadyExistsException;
import fr.sylvainjanet.tracker.tracking.domain.CompletionStatus;
import fr.sylvainjanet.tracker.tracking.domain.DailyRecord;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreateDailyRecordServiceTest {

    private final InMemoryDailyRecordStore repository = new InMemoryDailyRecordStore();
    private final CreateDailyRecordService service = new CreateDailyRecordService(repository);

    @BeforeEach
    void setUp() {
        repository.clear();
    }

    @Test
    void createsAndPersistsDailyRecord() throws DailyRecordAlreadyExistsException {
        LocalDate command = LocalDate.of(2026, Month.AUGUST, 25);

        DailyRecord result = service.execute(command);

        assertThat(result.date()).isEqualTo(command);
        assertThat(result.status()).isEqualTo(CompletionStatus.IN_PROGRESS);

        assertThat(repository.findByDate(command)).isPresent();
    }

    @Test
    void rejectsDuplicateDailyRecord() {
        LocalDate command = LocalDate.of(2026, Month.AUGUST, 25);

        repository.create(DailyRecord.create(command));

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(DailyRecordAlreadyExistsException.class)
                .hasMessageContaining(command.toString());
    }

    @Test
    void rejectsNullDate() {
        assertThatThrownBy(() -> service.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("date must not be null");
    }

    @Test
    void rejectsNullStore() {
        assertThatThrownBy(() -> new CreateDailyRecordService(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("store must not be null");
    }
}
