package fr.sylvainjanet.tracker.analysis.domain;

import static fr.sylvainjanet.tracker.analysis.domain.builder.WeightAnalysisBuilder.aWeightAnalysis;
import static fr.sylvainjanet.tracker.shared.domain.builder.WeightMeasurementBuilder.aWeightMeasurement;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.sylvainjanet.tracker.shared.domain.DateRange;
import fr.sylvainjanet.tracker.shared.domain.WeightMeasurement;
import fr.sylvainjanet.tracker.shared.domain.builder.DateRangeBuilder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class WeightAnalysisTest {

    private static final LocalDate TIMELINE_START = LocalDate.of(2026, 9, 20);
    private static final DateRange RANGE =
            DateRangeBuilder.aDateRange()
                    .withStartDate(LocalDate.of(2026, 9, 20))
                    .withEndDate(LocalDate.of(2026, 9, 25))
                    .build();

    @Test
    void createsAWeightAnalysis() {
        WeightMeasurement first =
                aWeightMeasurement()
                        .withDate(LocalDate.parse("2026-09-20"))
                        .withWeightInKg(new BigDecimal("82.10"))
                        .build();
        WeightMeasurement second =
                aWeightMeasurement()
                        .withDate(LocalDate.parse("2026-09-23"))
                        .withWeightInKg(new BigDecimal("81.90"))
                        .build();

        WeightAnalysis analysis =
                aWeightAnalysis()
                        .withTimelineStartDate(TIMELINE_START)
                        .withRange(RANGE)
                        .withWeightMeasurements(List.of(first, second))
                        .build();

        assertThat(analysis.timelineStartDate()).isEqualTo(TIMELINE_START);
        assertThat(analysis.range()).isEqualTo(RANGE);
        assertThat(analysis.weightMeasurements()).containsExactly(first, second);
    }

    @Test
    void comparesAnalysesUsingTheCompleteInputMeasurementState() {
        LocalDate measurementDate = LocalDate.parse("2026-09-20");

        WeightAnalysis analysis =
                aWeightAnalysis()
                        .withTimelineStartDate(TIMELINE_START)
                        .withRange(RANGE)
                        .withWeightMeasurements(
                                List.of(
                                        aWeightMeasurement()
                                                .withDate(measurementDate)
                                                .withWeightInKg(new BigDecimal("82.10"))
                                                .build()))
                        .build();

        WeightAnalysis sameInputs =
                aWeightAnalysis()
                        .withTimelineStartDate(TIMELINE_START)
                        .withRange(RANGE)
                        .withWeightMeasurements(
                                List.of(
                                        aWeightMeasurement()
                                                .withDate(measurementDate)
                                                .withWeightInKg(new BigDecimal("82.10"))
                                                .build()))
                        .build();

        WeightAnalysis correctedMeasurement =
                aWeightAnalysis()
                        .withTimelineStartDate(TIMELINE_START)
                        .withRange(RANGE)
                        .withWeightMeasurements(
                                List.of(
                                        aWeightMeasurement()
                                                .withDate(measurementDate)
                                                .withWeightInKg(new BigDecimal("81.90"))
                                                .build()))
                        .build();

        assertThat(analysis).isEqualTo(sameInputs);
        assertThat(analysis.hashCode()).isEqualTo(sameInputs.hashCode());
        assertThat(analysis).isNotEqualTo(correctedMeasurement);
    }

    @Test
    void acceptsARangeBeginningAfterTheTimelineStart() {
        DateRange selectedRange =
                DateRangeBuilder.aDateRange()
                        .withStartDate(LocalDate.of(2026, 9, 22))
                        .withEndDate(LocalDate.of(2026, 9, 25))
                        .build();

        WeightAnalysis analysis =
                aWeightAnalysis()
                        .withTimelineStartDate(TIMELINE_START)
                        .withRange(selectedRange)
                        .withWeightMeasurements(List.of())
                        .build();

        assertThat(analysis.timelineStartDate()).isEqualTo(TIMELINE_START);
        assertThat(analysis.range()).isEqualTo(selectedRange);
        assertThat(analysis.weightMeasurements()).isEmpty();
    }

    @Test
    void rejectsATimelineStartAfterTheRepresentedRangeStart() {
        assertThatThrownBy(
                        () ->
                                aWeightAnalysis()
                                        .withTimelineStartDate(LocalDate.of(2026, 9, 21))
                                        .withRange(RANGE)
                                        .withWeightMeasurements(List.of())
                                        .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("timeline start date must not be after represented range start date");
    }

    @Test
    void rejectsAMeasurementOutsideTheRepresentedRange() {
        WeightMeasurement measurement =
                aWeightMeasurement()
                        .withDate(LocalDate.parse("2026-09-26"))
                        .withWeightInKg(new BigDecimal("81.90"))
                        .build();

        assertThatThrownBy(
                        () ->
                                aWeightAnalysis()
                                        .withTimelineStartDate(TIMELINE_START)
                                        .withRange(RANGE)
                                        .withWeightMeasurements(List.of(measurement))
                                        .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("weight measurement must be inside the represented range");
    }

    @Test
    void rejectsDuplicateMeasurementDates() {
        List<WeightMeasurement> measurements =
                List.of(
                        aWeightMeasurement()
                                .withDate(LocalDate.parse("2026-09-20"))
                                .withWeightInKg(new BigDecimal("82.10"))
                                .build(),
                        aWeightMeasurement()
                                .withDate(LocalDate.parse("2026-09-20"))
                                .withWeightInKg(new BigDecimal("81.90"))
                                .build());

        assertThatThrownBy(
                        () ->
                                aWeightAnalysis()
                                        .withTimelineStartDate(TIMELINE_START)
                                        .withRange(RANGE)
                                        .withWeightMeasurements(measurements)
                                        .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("weight measurements must be ordered by unique ascending dates");
    }

    @Test
    void rejectsDescendingMeasurementDates() {
        List<WeightMeasurement> measurements =
                List.of(
                        aWeightMeasurement()
                                .withDate(LocalDate.parse("2026-09-23"))
                                .withWeightInKg(new BigDecimal("81.90"))
                                .build(),
                        aWeightMeasurement()
                                .withDate(LocalDate.parse("2026-09-20"))
                                .withWeightInKg(new BigDecimal("82.10"))
                                .build());

        assertThatThrownBy(
                        () ->
                                aWeightAnalysis()
                                        .withTimelineStartDate(TIMELINE_START)
                                        .withRange(RANGE)
                                        .withWeightMeasurements(measurements)
                                        .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("weight measurements must be ordered by unique ascending dates");
    }

    @Test
    void rejectsNullInputs() {
        assertThatThrownBy(
                        () ->
                                aWeightAnalysis()
                                        .withTimelineStartDate(null)
                                        .withRange(RANGE)
                                        .withWeightMeasurements(List.of())
                                        .build())
                .isInstanceOf(NullPointerException.class)
                .hasMessage("timeline start date must not be null");

        assertThatThrownBy(
                        () ->
                                aWeightAnalysis()
                                        .withTimelineStartDate(TIMELINE_START)
                                        .withRange(null)
                                        .withWeightMeasurements(List.of())
                                        .build())
                .isInstanceOf(NullPointerException.class)
                .hasMessage("represented range must not be null");

        assertThatThrownBy(() -> WeightAnalysis.create(TIMELINE_START, RANGE, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("weight measurements must not be null");
    }

    @Test
    void rejectsANullMeasurement() {
        List<WeightMeasurement> measurements = Collections.singletonList(null);

        assertThatThrownBy(
                        () ->
                                aWeightAnalysis()
                                        .withTimelineStartDate(TIMELINE_START)
                                        .withRange(RANGE)
                                        .withWeightMeasurements(measurements)
                                        .build())
                .isInstanceOf(NullPointerException.class)
                .hasMessage("weight measurement must not be null");
    }

    @Test
    void protectsItsMeasurementCollection() {
        WeightMeasurement measurement =
                aWeightMeasurement()
                        .withDate(LocalDate.parse("2026-09-20"))
                        .withWeightInKg(new BigDecimal("82.10"))
                        .build();
        List<WeightMeasurement> suppliedMeasurements = new ArrayList<>(List.of(measurement));

        WeightAnalysis analysis =
                aWeightAnalysis()
                        .withTimelineStartDate(TIMELINE_START)
                        .withRange(RANGE)
                        .withWeightMeasurements(suppliedMeasurements)
                        .build();
        suppliedMeasurements.clear();

        assertThat(analysis.weightMeasurements()).containsExactly(measurement);
        assertThatThrownBy(() -> analysis.weightMeasurements().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void numbersTheTimelineStartAsDayOne() {
        WeightAnalysis analysis =
                aWeightAnalysis()
                        .withTimelineStartDate(TIMELINE_START)
                        .withRange(RANGE)
                        .withWeightMeasurements(List.of())
                        .build();

        assertThat(analysis.dayNumberFor(TIMELINE_START)).isEqualTo(1L);
    }

    @Test
    void derivesDayNumbersFromElapsedCalendarDays() {
        WeightAnalysis analysis =
                aWeightAnalysis()
                        .withTimelineStartDate(TIMELINE_START)
                        .withRange(RANGE)
                        .withWeightMeasurements(List.of())
                        .build();

        assertThat(analysis.dayNumberFor(LocalDate.of(2026, 9, 23))).isEqualTo(4L);
    }

    @Test
    void preservesTheTimelineOriginWhenTheRepresentedRangeStartsLater() {
        DateRange selectedRange =
                DateRangeBuilder.aDateRange()
                        .withStartDate(LocalDate.of(2026, 9, 23))
                        .withEndDate(LocalDate.of(2026, 9, 25))
                        .build();
        WeightAnalysis analysis =
                aWeightAnalysis()
                        .withTimelineStartDate(TIMELINE_START)
                        .withRange(selectedRange)
                        .withWeightMeasurements(List.of())
                        .build();

        assertThat(analysis.dayNumberFor(selectedRange.getStartDate())).isEqualTo(4L);
    }

    @Test
    void rejectsADayBeforeTheTimelineStart() {
        WeightAnalysis analysis =
                aWeightAnalysis()
                        .withTimelineStartDate(TIMELINE_START)
                        .withRange(RANGE)
                        .withWeightMeasurements(List.of())
                        .build();

        assertThatThrownBy(() -> analysis.dayNumberFor(TIMELINE_START.minusDays(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("date must not be before the timeline start date");
    }

    @Test
    void rejectsANullTimelineDate() {
        WeightAnalysis analysis =
                aWeightAnalysis()
                        .withTimelineStartDate(TIMELINE_START)
                        .withRange(RANGE)
                        .withWeightMeasurements(List.of())
                        .build();

        assertThatThrownBy(() -> analysis.dayNumberFor(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("date must not be null");
    }
}
