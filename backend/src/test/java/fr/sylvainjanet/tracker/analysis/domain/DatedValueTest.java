package fr.sylvainjanet.tracker.analysis.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class DatedValueTest {

    @Test
    void rejectsANullDate() {
        BigDecimal value = new BigDecimal("12.34");
        assertThatThrownBy(() -> new DatedValue(null, value))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("date must not be null");
    }

    @Test
    void rejectsANullValue() {
        LocalDate date = LocalDate.of(2026, 9, 23);
        assertThatThrownBy(() -> new DatedValue(date, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null");
    }
}
