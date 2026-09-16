package fr.sylvainjanet.tracker.tracking.adapter.in.web;

import static fr.sylvainjanet.tracker.tracking.domain.builders.DailyRecordTestBuilder.aDailyRecord;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.sylvainjanet.tracker.tracking.adapter.in.web.controller.DailyRecordController;
import fr.sylvainjanet.tracker.tracking.application.port.in.exceptions.DailyRecordAlreadyExistsException;
import fr.sylvainjanet.tracker.tracking.application.port.in.usecase.CreateDailyRecordUseCase;
import fr.sylvainjanet.tracker.tracking.domain.DailyRecord;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DailyRecordController.class)
class DailyRecordControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private CreateDailyRecordUseCase createDailyRecordUseCase;

    @Nested
    class CreateDailyRecord {

        @Test
        void createsDailyRecord() throws Exception {
            LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
            float weight = 123.0f;
            DailyRecord command = aDailyRecord().withDate(date).withWeightInKg(weight).build();

            given(createDailyRecordUseCase.execute(command)).willReturn(command);

            mockMvc.perform(
                            post("/api/daily-records")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(
                                            """
                                        {
                                          "date": "2026-08-25",
                                          "weight": 123.0
                                        }
                                        """))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.date").value("2026-08-25"))
                    .andExpect(jsonPath("$.weight").value("123.0"));
        }

        @Test
        void rejectsMissingDate() throws Exception {
            mockMvc.perform(
                            post("/api/daily-records")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(createDailyRecordUseCase);
        }

        @Test
        void rejectsInvalidDate() throws Exception {
            mockMvc.perform(
                            post("/api/daily-records")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(
                                            """
                                        {
                                          "date": "2026-02-31"
                                          "weight": 123.0
                                        }
                                        """))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(createDailyRecordUseCase);
        }

        @Test
        void rejectsInvalidWeight() throws Exception {
            mockMvc.perform(
                            post("/api/daily-records")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(
                                            """
                                        {
                                          "date": "2026-02-31"
                                          "weight": -123.0
                                        }
                                        """))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(createDailyRecordUseCase);
        }

        @Test
        void rejectsDuplicateDailyRecord() throws Exception {
            LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);
            float weight = 123.0f;
            DailyRecord command = aDailyRecord().withDate(date).withWeightInKg(weight).build();

            given(createDailyRecordUseCase.execute(command))
                    .willThrow(new DailyRecordAlreadyExistsException(date));

            mockMvc.perform(
                            post("/api/daily-records")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(
                                            """
                                        {
                                          "date": "2026-08-25",
                                          "weight": 123.0
                                        }
                                        """))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.title").value("Daily record already exists"))
                    .andExpect(jsonPath("$.date").value("2026-08-25"));
        }
    }
}
