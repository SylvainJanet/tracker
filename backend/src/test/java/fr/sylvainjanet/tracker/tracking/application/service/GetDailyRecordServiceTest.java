package fr.sylvainjanet.tracker.tracking.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.tracking.adapter.out.persistence.InMemoryDailyRecordStore;
import fr.sylvainjanet.tracker.tracking.application.port.in.exceptions.DailyRecordNotFoundException;
import fr.sylvainjanet.tracker.tracking.domain.CompletionStatus;
import fr.sylvainjanet.tracker.tracking.domain.DailyRecord;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetDailyRecordServiceTest {

    private final InMemoryDailyRecordStore repository = new InMemoryDailyRecordStore();
    private final GetDailyRecordService service = new GetDailyRecordService(repository);

    @BeforeEach
    void setUp() {
        repository.clear();
    }

    @Test
    void returnsExistingDailyRecord() throws DailyRecordNotFoundException {
        LocalDate query = LocalDate.of(2026, Month.AUGUST, 25);
        repository.create(DailyRecord.create(query));

        DailyRecord result = service.execute(query);

        assertThat(result.date()).isEqualTo(query);
        assertThat(result.status()).isEqualTo(CompletionStatus.IN_PROGRESS);
    }

    @Test
    void rejectsMissingDailyRecord() {
        LocalDate query = LocalDate.of(2026, Month.AUGUST, 25);

        assertThatThrownBy(() -> service.execute(query))
                .isInstanceOf(DailyRecordNotFoundException.class)
                .hasMessageContaining(query.toString());
    }

    @Test
    void rejectsNullDate() {
        assertThatThrownBy(() -> service.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("date must not be null");
    }

    @Test
    void rejectsNullStore() {
        assertThatThrownBy(() -> new GetDailyRecordService(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("store must not be null");
    }
}
