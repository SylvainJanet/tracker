package fr.sylvainjanet.tracker.journal.adapter.in.web;

import static fr.sylvainjanet.tracker.journal.application.port.in.dtos.builders.command.LogWeightMeasurementCommandTestBuilder.aLogWeightMeasurementCommand;
import static fr.sylvainjanet.tracker.journal.application.port.in.dtos.builders.query.GetWeightMeasurementByDateQueryTestBuilder.aGetWeightMeasurementByDateQuery;
import static fr.sylvainjanet.tracker.journal.application.port.in.dtos.builders.result.GetWeightMeasurementByDateResultTestBuilder.aGetWeightMeasurementByDateResult;
import static fr.sylvainjanet.tracker.journal.application.port.in.dtos.builders.result.LogWeightMeasurementResultTestBuilder.aLogWeightMeasurementResult;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.sylvainjanet.tracker.journal.adapter.in.web.controller.JournalController;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.command.LogWeightMeasurementCommand;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.query.GetWeightMeasurementByDateQuery;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.GetWeightMeasurementByDateResult;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.LogWeightMeasurementResult;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.GetWeightMeasurementByDateUseCase;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.LogWeightMeasurementUseCase;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
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
            LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
            BigDecimal weight = BigDecimal.valueOf(1234);
            LogWeightMeasurementCommand command =
                    aLogWeightMeasurementCommand().withDate(date).withWeightInKg(weight).build();
            LogWeightMeasurementResult result =
                    aLogWeightMeasurementResult().withDate(date).withWeightInKg(weight).build();

            given(logWeightMeasurementUseCase.log(command)).willReturn(result);

            mockMvc.perform(
                            post("/api/journal/weight-measurement")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(
                                            """
                                        {
                                          "date": "2026-08-25",
                                          "weightInKg": 1234
                                        }
                                        """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.date").value("2026-08-25"))
                    .andExpect(jsonPath("$.weightInKg").value("1234"));
        }

        @Test
        void rejectsMissingDate() throws Exception {
            mockMvc.perform(
                            post("/api/journal/weight-measurement")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(logWeightMeasurementUseCase);
        }

        @Test
        void rejectsInvalidDate() throws Exception {
            mockMvc.perform(
                            post("/api/journal/weight-measurement")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(
                                            """
                                        {
                                          "date": "2026-02-31"
                                          "weightInKg": 123.0
                                        }
                                        """))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(logWeightMeasurementUseCase);
        }

        @Test
        void rejectsInvalidWeight() throws Exception {
            mockMvc.perform(
                            post("/api/journal/weight-measurement")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(
                                            """
                                        {
                                          "date": "2026-02-31"
                                          "weightInKg": -123.0
                                        }
                                        """))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(logWeightMeasurementUseCase);
        }
    }

    @Nested
    class GetWeightMeasurementByDate {
        @Test
        void getWeightMeasurementByDate() throws Exception {
            LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
            BigDecimal weight = BigDecimal.valueOf(1234);
            GetWeightMeasurementByDateQuery query =
                    aGetWeightMeasurementByDateQuery().withDate(date).build();
            GetWeightMeasurementByDateResult result =
                    aGetWeightMeasurementByDateResult()
                            .withDate(date)
                            .withWeightInKg(weight)
                            .build();

            given(getWeightMeasurementByDateUseCase.get(query)).willReturn(Optional.of(result));

            mockMvc.perform(
                            get("/api/journal/weight-measurement/2026-08-25")
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.date").value("2026-08-25"))
                    .andExpect(jsonPath("$.weightInKg").value("1234"));
        }

        @Test
        void rejectsInvalidDate() throws Exception {
            mockMvc.perform(
                            get("/api/journal/weight-measurement/12345-678-9-0")
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(getWeightMeasurementByDateUseCase);
        }

        @Test
        void doesNotFindMissingWeightMeasurement() throws Exception {
            LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
            GetWeightMeasurementByDateQuery query =
                    aGetWeightMeasurementByDateQuery().withDate(date).build();

            given(getWeightMeasurementByDateUseCase.get(query)).willReturn(Optional.empty());

            mockMvc.perform(
                            get("/api/journal/weight-measurement/2026-08-25")
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.title").value("Not Found"))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(
                            jsonPath("$.detail")
                                    .value(
                                            "No weight measurement was found for date "
                                                    + "2026-08-25"));

            verify(getWeightMeasurementByDateUseCase).get(query);
        }
    }
}
