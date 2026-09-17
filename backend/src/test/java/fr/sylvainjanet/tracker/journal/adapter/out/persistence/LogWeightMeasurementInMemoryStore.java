package fr.sylvainjanet.tracker.journal.adapter.out.persistence;

import static fr.sylvainjanet.tracker.journal.application.port.out.dtos.builders.outcome.LogWeightMeasurementOutcomeTestBuilder.aLogWeightMeasurementOutcome;
import static fr.sylvainjanet.tracker.journal.domain.builders.WeightMeasurementTestBuilder.aWeightMeasurement;

import fr.sylvainjanet.tracker.journal.application.port.out.dtos.instruction.LogWeightMeasurementInstruction;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.LogWeightMeasurementOutcome;
import fr.sylvainjanet.tracker.journal.application.port.out.gateway.store.LogWeightMeasurementStore;
import fr.sylvainjanet.tracker.journal.domain.WeightMeasurement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public final class LogWeightMeasurementInMemoryStore implements LogWeightMeasurementStore {

    private final Map<LocalDate, WeightMeasurement> records = new HashMap<>();

    @Override
    public LogWeightMeasurementOutcome log(LogWeightMeasurementInstruction instruction) {
        WeightMeasurement record =
                aWeightMeasurement()
                        .withDate(instruction.date())
                        .withWeightInKg(instruction.weightInKg())
                        .build();
        records.put(instruction.date(), record);
        return aLogWeightMeasurementOutcome()
                .withDate(instruction.date())
                .withWeightInKg(instruction.weightInKg())
                .build();
    }

    public void clear() {
        records.clear();
    }
}
