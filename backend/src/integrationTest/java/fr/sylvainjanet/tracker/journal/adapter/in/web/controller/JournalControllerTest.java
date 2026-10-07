package fr.sylvainjanet.tracker.journal.adapter.in.web.controller;

import static fr.sylvainjanet.tracker.journal.fixture.JournalHttpFixtures.INVALID_DATE_PATH;
import static fr.sylvainjanet.tracker.journal.fixture.JournalHttpFixtures.INVALID_DATE_REQUEST_JSON;
import static fr.sylvainjanet.tracker.journal.fixture.JournalHttpFixtures.INVALID_WEIGHT_REQUEST_JSON;
import static fr.sylvainjanet.tracker.journal.fixture.JournalHttpFixtures.LOG_REQUEST_JSON;
import static fr.sylvainjanet.tracker.journal.fixture.JournalHttpFixtures.MISSING_DATE_REQUEST_JSON;
import static fr.sylvainjanet.tracker.journal.fixture.JournalHttpFixtures.MISSING_WEIGHT_REQUEST_JSON;
import static fr.sylvainjanet.tracker.journal.fixture.JournalHttpFixtures.WEIGHT_MEASUREMENT_BY_DATE_PATH;
import static fr.sylvainjanet.tracker.journal.fixture.JournalHttpFixtures.WEIGHT_MEASUREMENT_PATH;
import static fr.sylvainjanet.tracker.journal.fixture.JournalHttpFixtures.WEIGHT_MEASUREMENT_RESPONSE_JSON;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.BY_DATE_QUERY;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.LOG_COMMAND;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.START_DATE;
import static fr.sylvainjanet.tracker.journal.fixture.WeightMeasurementFixtures.START_RESULT;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.sylvainjanet.tracker.journal.application.port.in.usecase.GetWeightMeasurementByDateUseCase;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.LogWeightMeasurementUseCase;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(JournalController.class)
class JournalControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private LogWeightMeasurementUseCase logWeightMeasurementUseCase;
    @MockitoBean private GetWeightMeasurementByDateUseCase getWeightMeasurementByDateUseCase;

    @Nested
    class LogWeightMeasurement {

        @Test
        void logsWeightMeasurement() throws Exception {
            given(logWeightMeasurementUseCase.log(LOG_COMMAND)).willReturn(START_RESULT);

            mockMvc.perform(
                            post(WEIGHT_MEASUREMENT_PATH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(LOG_REQUEST_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(WEIGHT_MEASUREMENT_RESPONSE_JSON));

            verify(logWeightMeasurementUseCase).log(LOG_COMMAND);
        }

        @Test
        void rejectsMissingDate() throws Exception {
            mockMvc.perform(
                            post(WEIGHT_MEASUREMENT_PATH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(MISSING_DATE_REQUEST_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.title").value("Invalid request"))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.detail").value("Request validation failed."))
                    .andExpect(jsonPath("$.errors.length()").value(1))
                    .andExpect(jsonPath("$.errors[0].field").value("date"));

            verifyNoInteractions(logWeightMeasurementUseCase);
        }

        @Test
        void rejectsMissingWeight() throws Exception {
            mockMvc.perform(
                            post(WEIGHT_MEASUREMENT_PATH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(MISSING_WEIGHT_REQUEST_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.errors.length()").value(1))
                    .andExpect(jsonPath("$.errors[0].field").value("weightInKg"));

            verifyNoInteractions(logWeightMeasurementUseCase);
        }

        @Test
        void rejectsInvalidDate() throws Exception {
            mockMvc.perform(
                            post(WEIGHT_MEASUREMENT_PATH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(INVALID_DATE_REQUEST_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

            verifyNoInteractions(logWeightMeasurementUseCase);
        }

        @Test
        void rejectsInvalidWeight() throws Exception {
            mockMvc.perform(
                            post(WEIGHT_MEASUREMENT_PATH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(INVALID_WEIGHT_REQUEST_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.errors.length()").value(1))
                    .andExpect(jsonPath("$.errors[0].field").value("weightInKg"))
                    .andExpect(jsonPath("$.errors[0].message").value("Weight must be positive."));

            verifyNoInteractions(logWeightMeasurementUseCase);
        }
    }

    @Nested
    class GetWeightMeasurementByDate {

        @Test
        void returnsWeightMeasurement() throws Exception {
            given(getWeightMeasurementByDateUseCase.get(BY_DATE_QUERY))
                    .willReturn(Optional.of(START_RESULT));

            mockMvc.perform(get(WEIGHT_MEASUREMENT_BY_DATE_PATH))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(WEIGHT_MEASUREMENT_RESPONSE_JSON));

            verify(getWeightMeasurementByDateUseCase).get(BY_DATE_QUERY);
        }

        @Test
        void rejectsInvalidDate() throws Exception {
            mockMvc.perform(get(INVALID_DATE_PATH))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

            verifyNoInteractions(getWeightMeasurementByDateUseCase);
        }

        @Test
        void returnsNotFoundWhenWeightMeasurementDoesNotExist() throws Exception {
            given(getWeightMeasurementByDateUseCase.get(BY_DATE_QUERY))
                    .willReturn(Optional.empty());

            mockMvc.perform(get(WEIGHT_MEASUREMENT_BY_DATE_PATH))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.title").value("Not Found"))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(
                            jsonPath("$.detail")
                                    .value(
                                            "No weight measurement was found for date "
                                                    + START_DATE));

            verify(getWeightMeasurementByDateUseCase).get(BY_DATE_QUERY);
        }
    }
}
