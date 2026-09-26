package fr.sylvainjanet.tracker.statistics.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.CalculateRollingAveragesCommand;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.command.CalculateRollingAveragesCommand.IndexedValueCommand;
import fr.sylvainjanet.tracker.statistics.application.port.in.dtos.result.CalculateRollingAveragesResult;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class CalculateRollingAveragesServiceTest {

    @Test
    void returnsNoRollingAverageWhileNoWindowIsRequested() {
        CalculateRollingAveragesCommand command =
                new CalculateRollingAveragesCommand(
                        List.of(new IndexedValueCommand(1L, new BigDecimal("10"))), List.of());

        CalculateRollingAveragesResult result =
                new CalculateRollingAveragesService().calculate(command);

        assertThat(result.rollingAverages()).isEmpty();
    }
}
