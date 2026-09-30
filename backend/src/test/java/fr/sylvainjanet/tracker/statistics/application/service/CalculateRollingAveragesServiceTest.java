package fr.sylvainjanet.tracker.statistics.application.service;

import static fr.sylvainjanet.tracker.statistics.fixture.RollingAverageFixtures.EMPTY_RESULT;
import static fr.sylvainjanet.tracker.statistics.fixture.RollingAverageFixtures.EMPTY_VALUES_COMMAND;
import static fr.sylvainjanet.tracker.statistics.fixture.RollingAverageFixtures.EXACT_AVERAGE_COMMAND;
import static fr.sylvainjanet.tracker.statistics.fixture.RollingAverageFixtures.EXACT_AVERAGE_RESULT;
import static fr.sylvainjanet.tracker.statistics.fixture.RollingAverageFixtures.INVALID_OUTPUT_RANGE_COMMAND;
import static fr.sylvainjanet.tracker.statistics.fixture.RollingAverageFixtures.INVALID_WINDOW_COMMAND;
import static fr.sylvainjanet.tracker.statistics.fixture.RollingAverageFixtures.NO_WINDOWS_COMMAND;
import static fr.sylvainjanet.tracker.statistics.fixture.RollingAverageFixtures.THROUGH_REQUESTED_END_COMMAND;
import static fr.sylvainjanet.tracker.statistics.fixture.RollingAverageFixtures.THROUGH_REQUESTED_END_RESULT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

class CalculateRollingAveragesServiceTest {

    private final CalculateRollingAveragesService service = new CalculateRollingAveragesService();

    @Test
    void returnsEmptyResultWhenNoValueIsProvided() {
        assertThat(service.calculate(EMPTY_VALUES_COMMAND)).isEqualTo(EMPTY_RESULT);
    }

    @Test
    void returnsEmptyResultWhenNoWindowIsRequested() {
        assertThat(service.calculate(NO_WINDOWS_COMMAND)).isEqualTo(EMPTY_RESULT);
    }

    @Test
    void calculatesExactFractionAndEveryRequestedApproximation() {
        assertThat(service.calculate(EXACT_AVERAGE_COMMAND)).isEqualTo(EXACT_AVERAGE_RESULT);
    }

    @Test
    void evaluatesEveryIndexThroughRequestedEndAndOmitsEmptyWindows() {
        assertThat(service.calculate(THROUGH_REQUESTED_END_COMMAND))
                .isEqualTo(THROUGH_REQUESTED_END_RESULT);
    }

    @Test
    void rejectsInvalidWindow() {
        assertThatThrownBy(() -> service.calculate(INVALID_WINDOW_COMMAND))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("rolling window size must be positive");
    }

    @Test
    void rejectsInvalidOutputRange() {
        assertThatThrownBy(() -> service.calculate(INVALID_OUTPUT_RANGE_COMMAND))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("first index must not be after last index");
    }

    @Test
    void rejectsNullCommand() {
        assertThatThrownBy(() -> service.calculate(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("command must not be null");
    }
}
