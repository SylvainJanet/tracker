package fr.sylvainjanet.tracker.journal.adapter.in.web;

import static fr.sylvainjanet.tracker.journal.application.port.in.dtos.builders.command.LogWeightMeasurementCommandTestBuilder.aLogWeightMeasurementCommand;
import static fr.sylvainjanet.tracker.journal.application.port.in.dtos.builders.result.LogWeightMeasurementResultTestBuilder.aLogWeightMeasurementResult;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.sylvainjanet.tracker.journal.adapter.in.web.controller.JournalController;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.command.LogWeightMeasurementCommand;
import fr.sylvainjanet.tracker.journal.application.port.in.dtos.result.LogWeightMeasurementResult;
import fr.sylvainjanet.tracker.journal.application.port.in.usecase.LogWeightMeasurementUseCase;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
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

    @MockitoBean private LogWeightMeasurementUseCase useCase;

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

            given(useCase.log(command)).willReturn(result);

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

            verifyNoInteractions(useCase);
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

            verifyNoInteractions(useCase);
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

            verifyNoInteractions(useCase);
        }
    }
}
