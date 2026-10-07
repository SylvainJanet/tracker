package fr.sylvainjanet.tracker.journal.application.service;

import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.DATE_RANGE_CRITERIA;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.DATE_RANGE_OUTCOME;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.DATE_RANGE_QUERY;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.DATE_RANGE_RESULT;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.EMPTY_DATE_RANGE_OUTCOME;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.EMPTY_DATE_RANGE_RESULT;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.END_DATE;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.INVALID_OUTCOME;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.START_DATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementInDateRangeQuery;
import fr.sylvainjanet.tracker.journal.application.port.out.dtos.outcome.GetWeightMeasurementInDateRangeOutcome;
import fr.sylvainjanet.tracker.journal.application.port.out.gateway.store.WeightMeasurementStore;
import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetWeightMeasurementInDateRangeServiceTest {

    @Mock private WeightMeasurementStore store;

    private GetWeightMeasurementInDateRangeService service;

    @BeforeEach
    void setUp() {
        service = new GetWeightMeasurementInDateRangeService(store);
    }

    @Test
    void returnsWeightMeasurementsInRequestedDateRange() {
        when(store.getInDateRange(DATE_RANGE_CRITERIA)).thenReturn(DATE_RANGE_OUTCOME);

        assertThat(service.get(DATE_RANGE_QUERY)).isEqualTo(DATE_RANGE_RESULT);

        verify(store).getInDateRange(DATE_RANGE_CRITERIA);
    }

    @Test
    void returnsEmptyResultWhenNoWeightMeasurementExistsInRequestedDateRange() {
        when(store.getInDateRange(DATE_RANGE_CRITERIA)).thenReturn(EMPTY_DATE_RANGE_OUTCOME);

        assertThat(service.get(DATE_RANGE_QUERY)).isEqualTo(EMPTY_DATE_RANGE_RESULT);

        verify(store).getInDateRange(DATE_RANGE_CRITERIA);
    }

    @Test
    void rejectsStartDateAfterEndDate() {
        GetWeightMeasurementInDateRangeQuery query =
                new GetWeightMeasurementInDateRangeQuery(END_DATE, START_DATE);

        assertThatThrownBy(() -> service.get(query))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("start date must not be after end date");

        verifyNoInteractions(store);
    }

    @Test
    void rejectsMissingDateRangeBoundaries() {
        GetWeightMeasurementInDateRangeQuery query =
                new GetWeightMeasurementInDateRangeQuery(null, null);

        assertThatThrownBy(() -> service.get(query))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("start date must not be null")
                .hasMessageContaining("end date must not be null");

        verifyNoInteractions(store);
    }

    @Test
    void rejectsInvalidStoreOutcome() {
        when(store.getInDateRange(DATE_RANGE_CRITERIA))
                .thenReturn(new GetWeightMeasurementInDateRangeOutcome(List.of(INVALID_OUTCOME)));

        assertThatThrownBy(() -> service.get(DATE_RANGE_QUERY))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Weight must be positive");

        verify(store).getInDateRange(DATE_RANGE_CRITERIA);
    }

    @Test
    void rejectsNullQuery() {
        assertThatThrownBy(() -> service.get(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("query must not be null");

        verifyNoInteractions(store);
    }

    @Test
    void rejectsNullStore() {
        assertThatThrownBy(() -> new GetWeightMeasurementInDateRangeService(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("store must not be null");
    }
}
