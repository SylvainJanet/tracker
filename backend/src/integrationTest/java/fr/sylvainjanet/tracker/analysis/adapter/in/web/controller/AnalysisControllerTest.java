package fr.sylvainjanet.tracker.analysis.adapter.in.web.controller;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult.DateRangeResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.dtos.result.GetWeightAnalysisResult.WeightMeasurementResult;
import fr.sylvainjanet.tracker.analysis.application.port.in.usecase.GetWeightAnalysisUseCase;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AnalysisController.class)
class AnalysisControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private GetWeightAnalysisUseCase getWeightAnalysisUseCase;

    @Test
    void getsWeightAnalysis() throws Exception {
        LocalDate firstDate = LocalDate.of(2026, Month.SEPTEMBER, 20);
        LocalDate secondDate = LocalDate.of(2026, Month.SEPTEMBER, 23);
        LocalDate endDate = LocalDate.of(2026, Month.SEPTEMBER, 25);

        given(getWeightAnalysisUseCase.get())
                .willReturn(
                        new GetWeightAnalysisResult(
                                firstDate,
                                new DateRangeResult(firstDate, endDate),
                                List.of(
                                        new WeightMeasurementResult(
                                                firstDate, 1L, new BigDecimal("82.10")),
                                        new WeightMeasurementResult(
                                                secondDate, 4L, new BigDecimal("81.90")))));

        mockMvc.perform(get("/api/analysis/weight"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$.timelineStartDate").value("2026-09-20"))
                .andExpect(jsonPath("$.range.length()").value(2))
                .andExpect(jsonPath("$.range.startDate").value("2026-09-20"))
                .andExpect(jsonPath("$.range.endDate").value("2026-09-25"))
                .andExpect(jsonPath("$.weightMeasurements.length()").value(2))
                .andExpect(jsonPath("$.weightMeasurements[0].length()").value(3))
                .andExpect(jsonPath("$.weightMeasurements[0].date").value("2026-09-20"))
                .andExpect(jsonPath("$.weightMeasurements[0].dayNumber").value(1))
                .andExpect(jsonPath("$.weightMeasurements[0].weightInKg").value(82.10))
                .andExpect(jsonPath("$.weightMeasurements[1].length()").value(3))
                .andExpect(jsonPath("$.weightMeasurements[1].date").value("2026-09-23"))
                .andExpect(jsonPath("$.weightMeasurements[1].dayNumber").value(4))
                .andExpect(jsonPath("$.weightMeasurements[1].weightInKg").value(81.90))
                .andExpect(jsonPath("$.rollingAverages").isArray())
                .andExpect(jsonPath("$.rollingAverages").isEmpty());

        verify(getWeightAnalysisUseCase).get();
    }

    @Test
    void getsAnEmptyWeightAnalysis() throws Exception {
        given(getWeightAnalysisUseCase.get()).willReturn(GetWeightAnalysisResult.empty());

        mockMvc.perform(get("/api/analysis/weight"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$.timelineStartDate").value(nullValue()))
                .andExpect(jsonPath("$.range").value(nullValue()))
                .andExpect(jsonPath("$.weightMeasurements").isArray())
                .andExpect(jsonPath("$.weightMeasurements").isEmpty())
                .andExpect(jsonPath("$.rollingAverages").isArray())
                .andExpect(jsonPath("$.rollingAverages").isEmpty());

        verify(getWeightAnalysisUseCase).get();
    }

    @Test
    void handlesAnUnexpectedAnalysisFailure() throws Exception {
        given(getWeightAnalysisUseCase.get())
                .willThrow(new IllegalStateException("internal failure details"));

        mockMvc.perform(get("/api/analysis/weight"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Internal server error"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred"))
                .andExpect(jsonPath("$.errorId").isString());

        verify(getWeightAnalysisUseCase).get();
    }
}
