package fr.sylvainjanet.tracker.analysis.fixture;

public final class AnalysisHttpFixtures {

    public static final String WEIGHT_ANALYSIS_PATH = "/api/analysis/weight";

    public static final String WEIGHT_ANALYSIS_RESPONSE_JSON =
            """
            {
              "timelineStartDate": "2026-09-20",
              "range": {
                "startDate": "2026-09-20",
                "endDate": "2026-09-25"
              },
              "weightMeasurements": [
                {
                  "date": "2026-09-20",
                  "dayNumber": 1,
                  "weightInKg": 82.10
                },
                {
                  "date": "2026-09-23",
                  "dayNumber": 4,
                  "weightInKg": 81.90
                }
              ],
              "rollingAverageSeries": [
                {
                  "windowSize": 7,
                  "rollingAverages": [
                    {
                      "date": "2026-09-25",
                      "dayNumber": 6,
                      "includedValues": [
                        {
                          "date": "2026-09-20",
                          "dayNumber": 1,
                          "weightInKg": 82.10
                        },
                        {
                          "date": "2026-09-23",
                          "dayNumber": 4,
                          "weightInKg": 81.90
                        }
                      ],
                      "rollingAverage": {
                        "exactValue": {
                          "numerator": 164.00,
                          "denominator": 2
                        },
                        "approximations": [
                          {
                            "value": 82.00,
                            "rounding": "PRETTY"
                          },
                          {
                            "value": 82.00000000000000000000,
                            "rounding": "PRECISE"
                          }
                        ]
                      }
                    }
                  ]
                }
              ]
            }
            """;

    public static final String EMPTY_WEIGHT_ANALYSIS_RESPONSE_JSON =
            """
            {
              "timelineStartDate": null,
              "range": null,
              "weightMeasurements": [],
              "rollingAverageSeries": []
            }
            """;

    private AnalysisHttpFixtures() {}
}
