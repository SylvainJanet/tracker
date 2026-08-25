package fr.sylvainjanet.tracker.tracking.adapter.in.web;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.sylvainjanet.tracker.tracking.adapter.in.web.controller.DailyRecordController;
import fr.sylvainjanet.tracker.tracking.application.port.in.exceptions.DailyRecordAlreadyExistsException;
import fr.sylvainjanet.tracker.tracking.application.port.in.exceptions.DailyRecordNotFoundException;
import fr.sylvainjanet.tracker.tracking.application.port.in.usecase.CreateDailyRecordUseCase;
import fr.sylvainjanet.tracker.tracking.application.port.in.usecase.GetDailyRecordUseCase;
import fr.sylvainjanet.tracker.tracking.domain.CompletionStatus;
import fr.sylvainjanet.tracker.tracking.domain.DailyRecord;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DailyRecordController.class)
class DailyRecordControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private CreateDailyRecordUseCase createDailyRecordUseCase;
    @MockitoBean private GetDailyRecordUseCase getDailyRecordUseCase;

    @Nested
    class GetDailyRecord {

        @Test
        void retrievesDailyRecord() throws Exception {
            LocalDate request = LocalDate.of(2026, Month.AUGUST, 25);

            given(getDailyRecordUseCase.execute(request))
                    .willReturn(DailyRecord.reconstitute(request, CompletionStatus.COMPLETED));

            mockMvc.perform(get("/api/daily-records/{date}", request))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.date").value("2026-08-25"))
                    .andExpect(jsonPath("$.status").value("COMPLETED"));
        }

        @Test
        void reportsMissingDailyRecord() throws Exception {
            LocalDate request = LocalDate.of(2026, Month.AUGUST, 25);

            given(getDailyRecordUseCase.execute(request))
                    .willThrow(new DailyRecordNotFoundException(request));

            mockMvc.perform(get("/api/daily-records/{date}", request))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("Daily record not found"))
                    .andExpect(jsonPath("$.detail").value("No daily record exists for 2026-08-25"));
        }

        @Test
        void rejectsMissingDate() throws Exception {
            String request = "";
            mockMvc.perform(get("/api/daily-records/{date}", request))
                    .andExpect(status().isNotFound());

            verifyNoInteractions(createDailyRecordUseCase);
        }

        @Test
        void rejectsInvalidDate() throws Exception {
            String request = "invalid-date";
            mockMvc.perform(get("/api/daily-records/{date}", request))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(createDailyRecordUseCase);
        }
    }

    @Nested
    class CreateDailyRecord {

        @Test
        void createsDailyRecord() throws Exception {
            LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);

            given(createDailyRecordUseCase.execute(date))
                    .willReturn(DailyRecord.reconstitute(date, CompletionStatus.IN_PROGRESS));

            mockMvc.perform(
                            post("/api/daily-records")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(
                                            """
                                        {
                                          "date": "2026-08-25"
                                        }
                                        """))
                    .andExpect(status().isCreated())
                    .andExpect(
                            header().string(
                                            HttpHeaders.LOCATION,
                                            "http://localhost/api/daily-records/2026-08-25"))
                    .andExpect(jsonPath("$.date").value("2026-08-25"))
                    .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
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
                                        }
                                        """))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(createDailyRecordUseCase);
        }

        @Test
        void rejectsDuplicateDailyRecord() throws Exception {
            LocalDate date = LocalDate.of(2026, Month.AUGUST, 25);

            given(createDailyRecordUseCase.execute(date))
                    .willThrow(new DailyRecordAlreadyExistsException(date));

            mockMvc.perform(
                            post("/api/daily-records")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(
                                            """
                                        {
                                          "date": "2026-08-25"
                                        }
                                        """))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.title").value("Daily record already exists"))
                    .andExpect(jsonPath("$.date").value("2026-08-25"));
        }
    }
}
