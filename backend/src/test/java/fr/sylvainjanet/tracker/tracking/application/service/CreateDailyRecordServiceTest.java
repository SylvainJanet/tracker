package fr.sylvainjanet.tracker.tracking.application.service;

import static fr.sylvainjanet.tracker.tracking.domain.builders.DailyRecordTestBuilder.aDailyRecord;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.tracking.adapter.out.persistence.InMemoryDailyRecordStore;
import fr.sylvainjanet.tracker.tracking.application.port.in.exceptions.DailyRecordAlreadyExistsException;
import fr.sylvainjanet.tracker.tracking.domain.DailyRecord;
import fr.sylvainjanet.tracker.tracking.domain.Weight;
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
        LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
        float weight = 123.0f;

        DailyRecord command = aDailyRecord().withDate(date).withWeightInKg(weight).build();

        DailyRecord result = service.execute(command);

        assertThat(result.date()).isEqualTo(date);
        assertThat(result.weight()).isEqualTo(Weight.of(weight));
    }

    @Test
    void rejectsDuplicateDailyRecord() {
        LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
        float weight = 123.0f;
        DailyRecord command = aDailyRecord().withDate(date).withWeightInKg(weight).build();
        repository.create(command);

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(DailyRecordAlreadyExistsException.class)
                .hasMessageContaining(command.date().toString());
    }

    @Test
    void rejectsNullCommand() {
        assertThatThrownBy(() -> service.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("command must not be null");
    }

    @Test
    void rejectsNullStore() {
        assertThatThrownBy(() -> new CreateDailyRecordService(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("store must not be null");
    }
}
