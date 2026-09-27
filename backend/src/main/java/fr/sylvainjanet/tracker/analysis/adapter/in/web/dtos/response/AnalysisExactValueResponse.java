package fr.sylvainjanet.tracker.analysis.adapter.in.web.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(oneOf = AnalysisFractionResponse.class)
public sealed interface AnalysisExactValueResponse permits AnalysisFractionResponse {}
