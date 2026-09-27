package fr.sylvainjanet.tracker.analysis.adapter.in.web.controller;

import static fr.sylvainjanet.tracker.analysis.fixture.AnalysisHttpFixtures.EMPTY_WEIGHT_ANALYSIS_RESPONSE_JSON;
import static fr.sylvainjanet.tracker.analysis.fixture.AnalysisHttpFixtures.WEIGHT_ANALYSIS_PATH;
import static fr.sylvainjanet.tracker.analysis.fixture.AnalysisHttpFixtures.WEIGHT_ANALYSIS_RESPONSE_JSON;
import static fr.sylvainjanet.tracker.analysis.fixture.WeightAnalysisFixtures.ANALYSIS_RESULT;
import static fr.sylvainjanet.tracker.analysis.fixture.WeightAnalysisFixtures.NO_MEASUREMENTS_RESULT;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.sylvainjanet.tracker.analysis.application.port.in.usecase.GetWeightAnalysisUseCase;
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
    void returnsWeightAnalysis() throws Exception {
        given(getWeightAnalysisUseCase.get()).willReturn(ANALYSIS_RESULT);

        mockMvc.perform(get(WEIGHT_ANALYSIS_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(WEIGHT_ANALYSIS_RESPONSE_JSON));

        verify(getWeightAnalysisUseCase).get();
    }

    @Test
    void returnsEmptyWeightAnalysis() throws Exception {
        given(getWeightAnalysisUseCase.get()).willReturn(NO_MEASUREMENTS_RESULT);

        mockMvc.perform(get(WEIGHT_ANALYSIS_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(EMPTY_WEIGHT_ANALYSIS_RESPONSE_JSON));

        verify(getWeightAnalysisUseCase).get();
    }

    @Test
    void hidesUnexpectedFailureDetails() throws Exception {
        given(getWeightAnalysisUseCase.get())
                .willThrow(new IllegalStateException("internal failure details"));

        mockMvc.perform(get(WEIGHT_ANALYSIS_PATH))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Internal server error"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred"))
                .andExpect(jsonPath("$.errorId").isString());

        verify(getWeightAnalysisUseCase).get();
    }
}
