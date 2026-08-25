package fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class DocumentedEndpointController {

    @ApiResponse(responseCode = "204", description = "Documented response")
    @GetMapping("/single-response")
    void singleResponse() {
        throw new UnsupportedOperationException(
                "This endpoint is only for architecture contract enforcement testing.");
    }

    @ApiResponse(responseCode = "200", description = "Successful response")
    @ApiResponse(responseCode = "404", description = "Missing response")
    @GetMapping("/multiple-responses")
    void multipleResponses() {
        throw new UnsupportedOperationException(
                "This endpoint is only for architecture contract enforcement testing.");
    }
}
