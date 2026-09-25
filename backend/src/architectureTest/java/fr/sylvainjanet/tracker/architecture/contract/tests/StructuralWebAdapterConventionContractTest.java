package fr.sylvainjanet.tracker.architecture.contract.tests;

import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebControllerRules.classesNamedControllerStayInControllerPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebControllerRules.webControllerEndpointsShouldDeclareOpenApiResponses;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebControllerRules.webControllersShouldBeAnnotatedWithRestController;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebControllerRules.webControllersShouldBeFinalClasses;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebControllerRules.webControllersShouldHaveControllerSuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebDtoRules.RequestRules.RequestEnumRules.requestEnumsShouldBeEnums;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebDtoRules.RequestRules.classesNamedRequestStayInRequestPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebDtoRules.RequestRules.requestDtosUseAllowedPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebDtoRules.RequestRules.webRequestDtosHaveRequestSuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebDtoRules.RequestRules.webRequestDtosShouldBeRecords;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebDtoRules.ResponseRules.ResponseBuilderRules.responseBuildersHaveResponseBuilderSuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebDtoRules.ResponseRules.ResponseBuilderRules.responseBuildersShouldBeFinalClasses;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebDtoRules.ResponseRules.ResponseEnumRules.responseEnumsShouldBeEnums;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebDtoRules.ResponseRules.ResponseMapperRules.responseMappersHaveResponseMapperSuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebDtoRules.ResponseRules.ResponseMapperRules.responseMappersOnlyDependOnResponsesAndApplicationResults;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebDtoRules.ResponseRules.ResponseMapperRules.responseMappersShouldBeFinalClasses;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebDtoRules.ResponseRules.classesNamedResponseStayInResponsePackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebDtoRules.ResponseRules.responseDtosUseAllowedPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebDtoRules.ResponseRules.webResponseDtosHaveResponseSuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebDtoRules.ResponseRules.webResponseDtosShouldBeRecords;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebDtoRules.webDataTransferObjectsDoNotDependOnCore;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebDtoRules.webDataTransferObjectsUseAllowedPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebHandlerRules.classesNamedHandlerStayInHandlerPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebHandlerRules.exceptionHandlerMethodsShouldReturnProblemDetails;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebHandlerRules.webHandlerShouldHaveHandlerSuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebHandlerRules.webHandlersShouldBeAnnotatedWithRestControllerAdvice;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebHandlerRules.webHandlersShouldBeFinalClasses;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebValidatorRules.classesNamedValidatorStayInValidatorPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebValidatorRules.webValidatorAnnotationRules.classesNamedValidStayInValidPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebValidatorRules.webValidatorAnnotationRules.webValidatorAnnotationShouldHaveValidSuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebValidatorRules.webValidatorShouldHaveValidatorSuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.WebValidatorRules.webValidatorsUseAllowedPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.WebAdapterRules.webAdaptersUseAllowedPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.AdapterInboundRules.inboundAdaptersUseAllowedPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.AdapterRules.adaptersUseAllowedPackages;

import fr.sylvainjanet.tracker.architecture.contract.ArchitectureRuleContract;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.unsupported.UnsupportedInboundAdapterType;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.controller.CorrectlyNamedController;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.controller.DocumentedEndpointController;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.controller.MisconfiguredEndpoint;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.controller.UndocumentedEndpointController;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.dtos.UnsupportedWebDto;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.dtos.request.CoreDependentRequest;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.dtos.request.CorrectlyNamedRequest;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.dtos.request.MisconfiguredRequestPayload;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.dtos.request.enums.NonEnumRequest;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.dtos.request.enums.RequestKindRequest;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.dtos.request.unsupported.UnsupportedNestedRequest;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.dtos.response.CorrectlyNamedResponse;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.dtos.response.MisconfiguredResponsePayload;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.dtos.response.builder.CorrectlyNamedResponseBuilder;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.dtos.response.builder.InvalidResponseBuilderFunction;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.dtos.response.enums.NonEnumResponse;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.dtos.response.enums.ResponseKindResponse;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.dtos.response.mapper.CorrectlyNamedResponseMapper;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.dtos.response.mapper.DomainDependentResponseMapper;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.dtos.response.mapper.InvalidResponseMapperFunction;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.dtos.response.mapper.ResultDependentResponseMapper;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.dtos.response.unsupported.UnsupportedNestedResponse;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.handler.CorrectlyNamedHandler;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.handler.MisconfiguredAdvice;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.unsupported.UnsupportedWebAdapterType;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.validator.CorrectlyNamedValidator;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.validator.MisnamedValidationComponent;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.validator.annotation.CorrectlyNamedAnnotationValid;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.validator.annotation.MisnamedAnnotationService;
import fr.sylvainjanet.tracker.architecturefixture.adapter.in.web.validator.unsupported.UnsupportedValidatorType;
import fr.sylvainjanet.tracker.architecturefixture.adapter.unsupported.UnsupportedAdapterType;
import fr.sylvainjanet.tracker.architecturefixture.application.port.in.dtos.result.CorrectlyNamedResult;
import fr.sylvainjanet.tracker.architecturefixture.domain.CorrectlyLocatedDomainType;
import fr.sylvainjanet.tracker.architecturefixture.domain.MisplacedController;
import fr.sylvainjanet.tracker.architecturefixture.domain.MisplacedHandler;
import fr.sylvainjanet.tracker.architecturefixture.domain.MisplacedRequest;
import fr.sylvainjanet.tracker.architecturefixture.domain.MisplacedResponse;
import fr.sylvainjanet.tracker.architecturefixture.domain.MisplacedValidator;
import fr.sylvainjanet.tracker.architecturefixture.domain.MisplacedValidatorAnnotationValid;
import org.junit.jupiter.api.Test;

public class StructuralWebAdapterConventionContractTest {

    @Test
    void adaptersUseAllowedPackages() {
        ArchitectureRuleContract.assertAccepts(
                adaptersUseAllowedPackages, CorrectlyNamedController.class);
        ArchitectureRuleContract.assertRejects(
                adaptersUseAllowedPackages, "UnsupportedAdapterType", UnsupportedAdapterType.class);
    }

    @Test
    void inboundAdaptersUseAllowedPackages() {
        ArchitectureRuleContract.assertAccepts(
                inboundAdaptersUseAllowedPackages, CorrectlyNamedController.class);
        ArchitectureRuleContract.assertRejects(
                inboundAdaptersUseAllowedPackages,
                "UnsupportedInboundAdapterType",
                UnsupportedInboundAdapterType.class);
    }

    @Test
    void webAdaptersUseAllowedPackages() {
        ArchitectureRuleContract.assertAccepts(
                webAdaptersUseAllowedPackages, CorrectlyNamedController.class);
        ArchitectureRuleContract.assertRejects(
                webAdaptersUseAllowedPackages,
                "UnsupportedWebAdapterType",
                UnsupportedWebAdapterType.class);
    }

    @Test
    void webControllersShouldHaveControllerSuffix() {
        ArchitectureRuleContract.assertAccepts(
                webControllersShouldHaveControllerSuffix, CorrectlyNamedController.class);
        ArchitectureRuleContract.assertRejects(
                webControllersShouldHaveControllerSuffix,
                "MisconfiguredEndpoint",
                MisconfiguredEndpoint.class);
    }

    @Test
    void classesNamedControllerStayInControllerPackages() {
        ArchitectureRuleContract.assertAccepts(
                classesNamedControllerStayInControllerPackages, CorrectlyNamedController.class);
        ArchitectureRuleContract.assertRejects(
                classesNamedControllerStayInControllerPackages,
                "MisplacedController",
                MisplacedController.class);
    }

    @Test
    void webControllersShouldBeFinalClasses() {
        ArchitectureRuleContract.assertAccepts(
                webControllersShouldBeFinalClasses, CorrectlyNamedController.class);
        ArchitectureRuleContract.assertRejects(
                webControllersShouldBeFinalClasses,
                "MisconfiguredEndpoint",
                MisconfiguredEndpoint.class);
    }

    @Test
    void webControllersShouldBeAnnotatedWithRestController() {
        ArchitectureRuleContract.assertAccepts(
                webControllersShouldBeAnnotatedWithRestController, CorrectlyNamedController.class);
        ArchitectureRuleContract.assertRejects(
                webControllersShouldBeAnnotatedWithRestController,
                "MisconfiguredEndpoint",
                MisconfiguredEndpoint.class);
    }

    @Test
    void webControllerEndpointsShouldDeclareOpenApiResponses() {
        ArchitectureRuleContract.assertAccepts(
                webControllerEndpointsShouldDeclareOpenApiResponses,
                DocumentedEndpointController.class);
        ArchitectureRuleContract.assertRejects(
                webControllerEndpointsShouldDeclareOpenApiResponses,
                "undocumented",
                UndocumentedEndpointController.class);
    }

    @Test
    void webDataTransferObjectsUseAllowedPackages() {
        ArchitectureRuleContract.assertAccepts(
                webDataTransferObjectsUseAllowedPackages, CorrectlyNamedRequest.class);
        ArchitectureRuleContract.assertRejects(
                webDataTransferObjectsUseAllowedPackages,
                "UnsupportedWebDto",
                UnsupportedWebDto.class);
    }

    @Test
    void webDataTransferObjectsDoNotDependOnCore() {
        ArchitectureRuleContract.assertAccepts(
                webDataTransferObjectsDoNotDependOnCore,
                CorrectlyNamedRequest.class,
                ResultDependentResponseMapper.class,
                CorrectlyNamedResult.class);
        ArchitectureRuleContract.assertRejects(
                webDataTransferObjectsDoNotDependOnCore,
                "CoreDependentRequest",
                CoreDependentRequest.class,
                CorrectlyLocatedDomainType.class);
    }

    @Test
    void requestDtosUseAllowedPackages() {
        ArchitectureRuleContract.assertAccepts(
                requestDtosUseAllowedPackages, RequestKindRequest.class);
        ArchitectureRuleContract.assertRejects(
                requestDtosUseAllowedPackages,
                "UnsupportedNestedRequest",
                UnsupportedNestedRequest.class);
    }

    @Test
    void webRequestDtosHaveRequestSuffix() {
        ArchitectureRuleContract.assertAccepts(
                webRequestDtosHaveRequestSuffix,
                CorrectlyNamedRequest.class,
                RequestKindRequest.class);
        ArchitectureRuleContract.assertRejects(
                webRequestDtosHaveRequestSuffix,
                "MisconfiguredRequestPayload",
                MisconfiguredRequestPayload.class);
    }

    @Test
    void classesNamedRequestStayInRequestPackages() {
        ArchitectureRuleContract.assertAccepts(
                classesNamedRequestStayInRequestPackages, CorrectlyNamedRequest.class);
        ArchitectureRuleContract.assertRejects(
                classesNamedRequestStayInRequestPackages,
                "MisplacedRequest",
                MisplacedRequest.class);
    }

    @Test
    void webRequestDtosShouldBeRecords() {
        ArchitectureRuleContract.assertAccepts(
                webRequestDtosShouldBeRecords, CorrectlyNamedRequest.class);
        ArchitectureRuleContract.assertRejects(
                webRequestDtosShouldBeRecords,
                "MisconfiguredRequestPayload",
                MisconfiguredRequestPayload.class);
    }

    @Test
    void requestEnumsShouldBeEnums() {
        ArchitectureRuleContract.assertAccepts(requestEnumsShouldBeEnums, RequestKindRequest.class);
        ArchitectureRuleContract.assertRejects(
                requestEnumsShouldBeEnums, "NonEnumRequest", NonEnumRequest.class);
    }

    @Test
    void responseDtosUseAllowedPackages() {
        ArchitectureRuleContract.assertAccepts(
                responseDtosUseAllowedPackages, ResponseKindResponse.class);
        ArchitectureRuleContract.assertRejects(
                responseDtosUseAllowedPackages,
                "UnsupportedNestedResponse",
                UnsupportedNestedResponse.class);
    }

    @Test
    void webResponseDtosHaveResponseSuffix() {
        ArchitectureRuleContract.assertAccepts(
                webResponseDtosHaveResponseSuffix,
                CorrectlyNamedResponse.class,
                ResponseKindResponse.class);
        ArchitectureRuleContract.assertRejects(
                webResponseDtosHaveResponseSuffix,
                "MisconfiguredResponsePayload",
                MisconfiguredResponsePayload.class);
    }

    @Test
    void classesNamedResponseStayInResponsePackages() {
        ArchitectureRuleContract.assertAccepts(
                classesNamedResponseStayInResponsePackages, CorrectlyNamedResponse.class);
        ArchitectureRuleContract.assertRejects(
                classesNamedResponseStayInResponsePackages,
                "MisplacedResponse",
                MisplacedResponse.class);
    }

    @Test
    void webResponseDtosShouldBeRecords() {
        ArchitectureRuleContract.assertAccepts(
                webResponseDtosShouldBeRecords, CorrectlyNamedResponse.class);
        ArchitectureRuleContract.assertRejects(
                webResponseDtosShouldBeRecords,
                "MisconfiguredResponsePayload",
                MisconfiguredResponsePayload.class);
    }

    @Test
    void responseBuildersHaveResponseBuilderSuffix() {
        ArchitectureRuleContract.assertAccepts(
                responseBuildersHaveResponseBuilderSuffix, CorrectlyNamedResponseBuilder.class);
        ArchitectureRuleContract.assertRejects(
                responseBuildersHaveResponseBuilderSuffix,
                "InvalidResponseBuilderFunction",
                InvalidResponseBuilderFunction.class);
    }

    @Test
    void responseBuildersShouldBeFinalClasses() {
        ArchitectureRuleContract.assertAccepts(
                responseBuildersShouldBeFinalClasses, CorrectlyNamedResponseBuilder.class);
        ArchitectureRuleContract.assertRejects(
                responseBuildersShouldBeFinalClasses,
                "InvalidResponseBuilderFunction",
                InvalidResponseBuilderFunction.class);
    }

    @Test
    void responseMappersHaveResponseMapperSuffix() {
        ArchitectureRuleContract.assertAccepts(
                responseMappersHaveResponseMapperSuffix, CorrectlyNamedResponseMapper.class);
        ArchitectureRuleContract.assertRejects(
                responseMappersHaveResponseMapperSuffix,
                "InvalidResponseMapperFunction",
                InvalidResponseMapperFunction.class);
    }

    @Test
    void responseMappersShouldBeFinalClasses() {
        ArchitectureRuleContract.assertAccepts(
                responseMappersShouldBeFinalClasses, CorrectlyNamedResponseMapper.class);
        ArchitectureRuleContract.assertRejects(
                responseMappersShouldBeFinalClasses,
                "InvalidResponseMapperFunction",
                InvalidResponseMapperFunction.class);
    }

    @Test
    void responseMappersOnlyDependOnResponsesAndApplicationResults() {
        ArchitectureRuleContract.assertAccepts(
                responseMappersOnlyDependOnResponsesAndApplicationResults,
                ResultDependentResponseMapper.class,
                CorrectlyNamedResult.class);

        ArchitectureRuleContract.assertRejects(
                responseMappersOnlyDependOnResponsesAndApplicationResults,
                "CorrectlyLocatedDomainType",
                DomainDependentResponseMapper.class,
                CorrectlyLocatedDomainType.class);
    }

    @Test
    void responseEnumsShouldBeEnums() {
        ArchitectureRuleContract.assertAccepts(
                responseEnumsShouldBeEnums, ResponseKindResponse.class);
        ArchitectureRuleContract.assertRejects(
                responseEnumsShouldBeEnums, "NonEnumResponse", NonEnumResponse.class);
    }

    @Test
    void webHandlerShouldHaveHandlerSuffix() {
        ArchitectureRuleContract.assertAccepts(
                webHandlerShouldHaveHandlerSuffix, CorrectlyNamedHandler.class);
        ArchitectureRuleContract.assertRejects(
                webHandlerShouldHaveHandlerSuffix,
                "MisconfiguredAdvice",
                MisconfiguredAdvice.class);
    }

    @Test
    void classesNamedHandlerStayInHandlerPackages() {
        ArchitectureRuleContract.assertAccepts(
                classesNamedHandlerStayInHandlerPackages, CorrectlyNamedHandler.class);
        ArchitectureRuleContract.assertRejects(
                classesNamedHandlerStayInHandlerPackages,
                "MisplacedHandler",
                MisplacedHandler.class);
    }

    @Test
    void webHandlersShouldBeFinalClasses() {
        ArchitectureRuleContract.assertAccepts(
                webHandlersShouldBeFinalClasses, CorrectlyNamedHandler.class);
        ArchitectureRuleContract.assertRejects(
                webHandlersShouldBeFinalClasses, "MisconfiguredAdvice", MisconfiguredAdvice.class);
    }

    @Test
    void webHandlersShouldBeAnnotatedWithRestControllerAdvice() {
        ArchitectureRuleContract.assertAccepts(
                webHandlersShouldBeAnnotatedWithRestControllerAdvice, CorrectlyNamedHandler.class);
        ArchitectureRuleContract.assertRejects(
                webHandlersShouldBeAnnotatedWithRestControllerAdvice,
                "MisconfiguredAdvice",
                MisconfiguredAdvice.class);
    }

    @Test
    void exceptionHandlerMethodsShouldReturnProblemDetails() {
        ArchitectureRuleContract.assertAccepts(
                exceptionHandlerMethodsShouldReturnProblemDetails, CorrectlyNamedHandler.class);
        ArchitectureRuleContract.assertRejects(
                exceptionHandlerMethodsShouldReturnProblemDetails,
                "MisconfiguredAdvice",
                MisconfiguredAdvice.class);
    }

    @Test
    void webValidatorShouldHaveValidatorSuffix() {
        ArchitectureRuleContract.assertAccepts(
                webValidatorShouldHaveValidatorSuffix, CorrectlyNamedValidator.class);
        ArchitectureRuleContract.assertRejects(
                webValidatorShouldHaveValidatorSuffix,
                "MisnamedValidationComponent",
                MisnamedValidationComponent.class);
    }

    @Test
    void classesNamedValidatorStayInValidatorPackages() {
        ArchitectureRuleContract.assertAccepts(
                classesNamedValidatorStayInValidatorPackages, CorrectlyNamedValidator.class);
        ArchitectureRuleContract.assertRejects(
                classesNamedValidatorStayInValidatorPackages,
                "MisplacedValidator",
                MisplacedValidator.class);
    }

    @Test
    void webValidatorAnnotationShouldHaveValidSuffix() {
        ArchitectureRuleContract.assertAccepts(
                webValidatorAnnotationShouldHaveValidSuffix, CorrectlyNamedAnnotationValid.class);
        ArchitectureRuleContract.assertRejects(
                webValidatorAnnotationShouldHaveValidSuffix,
                "MisnamedAnnotationService",
                MisnamedAnnotationService.class);
    }

    @Test
    void classesNamedValidStayInValidPackages() {
        ArchitectureRuleContract.assertAccepts(
                classesNamedValidStayInValidPackages, CorrectlyNamedValidator.class);
        ArchitectureRuleContract.assertRejects(
                classesNamedValidStayInValidPackages,
                "MisplacedValidatorAnnotationValid",
                MisplacedValidatorAnnotationValid.class);
    }

    @Test
    void webValidatorsUseAllowedPackages() {
        ArchitectureRuleContract.assertAccepts(
                webValidatorsUseAllowedPackages, CorrectlyNamedValidator.class);
        ArchitectureRuleContract.assertRejects(
                webValidatorsUseAllowedPackages,
                "UnsupportedValidatorType",
                UnsupportedValidatorType.class);
    }
}
