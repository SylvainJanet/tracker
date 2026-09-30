package fr.sylvainjanet.tracker.analysis.adapter.in.web.controller;

import fr.sylvainjanet.tracker.analysis.adapter.in.web.controller.mapper.GetWeightAnalysisResponseMapper;
import fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response.GetWeightAnalysisResponse;
import fr.sylvainjanet.tracker.analysis.application.port.in.usecase.GetWeightAnalysisUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.Objects;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analysis")
public final class AnalysisController {

    private final GetWeightAnalysisUseCase getWeightAnalysis;

    public AnalysisController(GetWeightAnalysisUseCase getWeightAnalysis) {
        this.getWeightAnalysis =
                Objects.requireNonNull(getWeightAnalysis, "get weight analysis must not be null");
    }

    @Operation(
            summary = "Get weight analysis",
            description =
                    """
                    Returns measurements and 7, 14, 28, 60, 180 and 360-day rolling-average series
                    from the first logged weight through today. Day numbers are one-based calendar-day
                    offsets from the timeline start. Missing dates are omitted from weightMeasurements;
                    a rolling-average point is present while its trailing window contains at least one
                    measurement. When no measurement is available, the timeline and range are null and
                    both value collections are empty.
                    """)
    @ApiResponse(
            responseCode = "200",
            description = "Weight analysis retrieved",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GetWeightAnalysisResponse.class),
                            examples = {
                                @ExampleObject(
                                        name = "withMeasurements",
                                        summary = "Analysis containing measurements",
                                        value =
                                                """
                                            {
                                              "timelineStartDate": "2026-09-25",
                                              "range": {
                                                "startDate": "2026-09-25",
                                                "endDate": "2026-09-25"
                                              },
                                              "weightMeasurements": [
                                                {
                                                  "date": "2026-09-25",
                                                  "dayNumber": 1,
                                                  "weightInKg": 82.10
                                                }
                                              ],
                                              "rollingAverageSeries": [
                                                {
                                                  "windowSize": 7,
                                                  "rollingAverages": [
                                                    {
                                                      "date": "2026-09-25",
                                                      "dayNumber": 1,
                                                      "includedValues": [
                                                        {
                                                          "date": "2026-09-25",
                                                          "dayNumber": 1,
                                                          "weightInKg": 82.10
                                                        }
                                                      ],
                                                      "rollingAverage": {
                                                        "exactValue": {
                                                          "numerator": 82.10,
                                                          "denominator": 1
                                                        },
                                                        "approximations": [
                                                          {
                                                            "value": 82.10,
                                                            "rounding": "PRETTY"
                                                          },
                                                          {
                                                            "value": 82.10000000000000000000,
                                                            "rounding": "PRECISE"
                                                          }
                                                        ]
                                                      }
                                                    }
                                                  ]
                                                },
                                                {
                                                  "windowSize": 14,
                                                  "rollingAverages": [
                                                    {
                                                      "date": "2026-09-25",
                                                      "dayNumber": 1,
                                                      "includedValues": [
                                                        {
                                                          "date": "2026-09-25",
                                                          "dayNumber": 1,
                                                          "weightInKg": 82.10
                                                        }
                                                      ],
                                                      "rollingAverage": {
                                                        "exactValue": {
                                                          "numerator": 82.10,
                                                          "denominator": 1
                                                        },
                                                        "approximations": [
                                                          {
                                                            "value": 82.10,
                                                            "rounding": "PRETTY"
                                                          },
                                                          {
                                                            "value": 82.10000000000000000000,
                                                            "rounding": "PRECISE"
                                                          }
                                                        ]
                                                      }
                                                    }
                                                  ]
                                                },
                                                {
                                                  "windowSize": 28,
                                                  "rollingAverages": [
                                                    {
                                                      "date": "2026-09-25",
                                                      "dayNumber": 1,
                                                      "includedValues": [
                                                        {
                                                          "date": "2026-09-25",
                                                          "dayNumber": 1,
                                                          "weightInKg": 82.10
                                                        }
                                                      ],
                                                      "rollingAverage": {
                                                        "exactValue": {
                                                          "numerator": 82.10,
                                                          "denominator": 1
                                                        },
                                                        "approximations": [
                                                          {
                                                            "value": 82.10,
                                                            "rounding": "PRETTY"
                                                          },
                                                          {
                                                            "value": 82.10000000000000000000,
                                                            "rounding": "PRECISE"
                                                          }
                                                        ]
                                                      }
                                                    }
                                                  ]
                                                },
                                                {
                                                  "windowSize": 60,
                                                  "rollingAverages": [
                                                    {
                                                      "date": "2026-09-25",
                                                      "dayNumber": 1,
                                                      "includedValues": [
                                                        {
                                                          "date": "2026-09-25",
                                                          "dayNumber": 1,
                                                          "weightInKg": 82.10
                                                        }
                                                      ],
                                                      "rollingAverage": {
                                                        "exactValue": {
                                                          "numerator": 82.10,
                                                          "denominator": 1
                                                        },
                                                        "approximations": [
                                                          {
                                                            "value": 82.10,
                                                            "rounding": "PRETTY"
                                                          },
                                                          {
                                                            "value": 82.10000000000000000000,
                                                            "rounding": "PRECISE"
                                                          }
                                                        ]
                                                      }
                                                    }
                                                  ]
                                                },
                                                {
                                                  "windowSize": 180,
                                                  "rollingAverages": [
                                                    {
                                                      "date": "2026-09-25",
                                                      "dayNumber": 1,
                                                      "includedValues": [
                                                        {
                                                          "date": "2026-09-25",
                                                          "dayNumber": 1,
                                                          "weightInKg": 82.10
                                                        }
                                                      ],
                                                      "rollingAverage": {
                                                        "exactValue": {
                                                          "numerator": 82.10,
                                                          "denominator": 1
                                                        },
                                                        "approximations": [
                                                          {
                                                            "value": 82.10,
                                                            "rounding": "PRETTY"
                                                          },
                                                          {
                                                            "value": 82.10000000000000000000,
                                                            "rounding": "PRECISE"
                                                          }
                                                        ]
                                                      }
                                                    }
                                                  ]
                                                },
                                                {
                                                  "windowSize": 360,
                                                  "rollingAverages": [
                                                    {
                                                      "date": "2026-09-25",
                                                      "dayNumber": 1,
                                                      "includedValues": [
                                                        {
                                                          "date": "2026-09-25",
                                                          "dayNumber": 1,
                                                          "weightInKg": 82.10
                                                        }
                                                      ],
                                                      "rollingAverage": {
                                                        "exactValue": {
                                                          "numerator": 82.10,
                                                          "denominator": 1
                                                        },
                                                        "approximations": [
                                                          {
                                                            "value": 82.10,
                                                            "rounding": "PRETTY"
                                                          },
                                                          {
                                                            "value": 82.10000000000000000000,
                                                            "rounding": "PRECISE"
                                                          }
                                                        ]
                                                      }
                                                    }
                                                  ]
                                                }
                                              ]
                                            }
                                            """),
                                @ExampleObject(
                                        name = "withoutMeasurements",
                                        summary = "Globally empty analysis",
                                        value =
                                                """
                                            {
                                              "timelineStartDate": null,
                                              "range": null,
                                              "weightMeasurements": [],
                                              "rollingAverageSeries": []
                                            }
                                            """)
                            }))
    @GetMapping(path = "/weight")
    ResponseEntity<GetWeightAnalysisResponse> getWeightAnalysis() {
        return ResponseEntity.ok(
                GetWeightAnalysisResponseMapper.resultToResponse(getWeightAnalysis.get()));
    }
}
