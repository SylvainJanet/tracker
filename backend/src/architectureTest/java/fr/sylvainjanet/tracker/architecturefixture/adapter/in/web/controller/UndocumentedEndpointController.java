package fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class UndocumentedEndpointController {

    @GetMapping("/undocumented")
    void undocumented() {
        throw new UnsupportedOperationException(
                "This endpoint is only for architecture contract enforcement testing.");
    }
}
