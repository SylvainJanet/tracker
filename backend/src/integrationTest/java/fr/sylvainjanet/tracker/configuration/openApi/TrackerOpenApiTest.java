package fr.sylvainjanet.tracker.configuration.openApi;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.sylvainjanet.tracker.TrackerApplication;
import fr.sylvainjanet.tracker.configuration.sqlite.environment.SqliteTestDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest(classes = TrackerApplication.class)
@SqliteTestDatabase("tracker-open-api")
class TrackerOpenApiTest {

    @Autowired MockMvc mockMvc;

    @Test
    void openApiMatchesContract() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.paths['/api/journal/weight-measurement'].post.responses['200']")
                                .exists())
                .andExpect(
                        jsonPath("$.paths['/api/journal/weight-measurement'].post.responses['400']")
                                .exists())
                .andExpect(
                        jsonPath(
                                        "$.paths['/api/journal/weight-measurement'].post.responses.length()")
                                .value(2))
                .andExpect(
                        jsonPath("$.paths['/api/journal/weight-measurement'].keys()")
                                .value(containsInAnyOrder("post")))
                .andExpect(
                        jsonPath(
                                        "$.paths['/api/journal/weight-measurement/{date}'].get.responses['200']")
                                .exists())
                .andExpect(
                        jsonPath(
                                        "$.paths['/api/journal/weight-measurement/{date}'].get.responses['400']")
                                .exists())
                .andExpect(
                        jsonPath(
                                        "$.paths['/api/journal/weight-measurement/{date}'].get.responses['404']")
                                .exists())
                .andExpect(
                        jsonPath(
                                        "$.paths['/api/journal/weight-measurement/{date}'].get.responses.length()")
                                .value(3))
                .andExpect(
                        jsonPath("$.paths['/api/journal/weight-measurement/{date}'].keys()")
                                .value(containsInAnyOrder("get")))
                .andExpect(jsonPath("$.paths.length()").value(2));
    }
}
