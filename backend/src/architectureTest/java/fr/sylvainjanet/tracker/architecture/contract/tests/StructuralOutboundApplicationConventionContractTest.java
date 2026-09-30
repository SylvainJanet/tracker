package fr.sylvainjanet.tracker.architecture.contract.tests;

import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.OutboundPortRules.DtosRules.CriteriaRules.CriteriaBuilderRules.criteriaBuildersHaveBuilderSuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.OutboundPortRules.DtosRules.CriteriaRules.CriteriaBuilderRules.criteriaBuildersShouldBeFinalClasses;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.OutboundPortRules.DtosRules.CriteriaRules.classesNamedCriteriaStayInCriteriaPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.OutboundPortRules.DtosRules.CriteriaRules.criteriaDtosAreRecordsOrEnums;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.OutboundPortRules.DtosRules.CriteriaRules.criteriaDtosHaveCriteriaSuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.OutboundPortRules.DtosRules.CriteriaRules.criteriaDtosUseAllowedPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.OutboundPortRules.DtosRules.InstructionRules.InstructionBuilderRules.instructionBuildersHaveBuilderSuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.OutboundPortRules.DtosRules.InstructionRules.InstructionBuilderRules.instructionBuildersShouldBeFinalClasses;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.OutboundPortRules.DtosRules.InstructionRules.classesNamedInstructionStayInInstructionPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.OutboundPortRules.DtosRules.InstructionRules.instructionDtosAreRecordsOrEnums;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.OutboundPortRules.DtosRules.InstructionRules.instructionDtosHaveInstructionSuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.OutboundPortRules.DtosRules.InstructionRules.instructionDtosUseAllowedPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.OutboundPortRules.DtosRules.OutcomeRules.OutcomeBuilderRules.outcomeBuildersHaveBuilderSuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.OutboundPortRules.DtosRules.OutcomeRules.OutcomeBuilderRules.outcomeBuildersShouldBeFinalClasses;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.OutboundPortRules.DtosRules.OutcomeRules.classesNamedOutcomeStayInOutcomePackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.OutboundPortRules.DtosRules.OutcomeRules.outcomeDtosAreRecordsOrEnums;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.OutboundPortRules.DtosRules.OutcomeRules.outcomeDtosHaveOutcomeSuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.OutboundPortRules.DtosRules.OutcomeRules.outcomeDtosUseAllowedPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.OutboundPortRules.DtosRules.outboundPortDtosUseAllowedPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.OutboundPortRules.GatewayRules.StoreRules.classesNamedStoreStayInStorePackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.OutboundPortRules.GatewayRules.StoreRules.outboundStoresHaveStoreSuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.OutboundPortRules.GatewayRules.StoreRules.outboundStoresShouldBeInterfaces;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.OutboundPortRules.GatewayRules.outboundGatewaysUseAllowedPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.OutboundPortRules.outboundPortsUseAllowedPackages;

import fr.sylvainjanet.tracker.architecture.contract.ArchitectureRuleContract;
import fr.sylvainjanet.tracker.architecturefixture.application.port.out.dtos.criteria.CorrectlyNamedCriteria;
import fr.sylvainjanet.tracker.architecturefixture.application.port.out.dtos.criteria.CriteriaKindCriteria;
import fr.sylvainjanet.tracker.architecturefixture.application.port.out.dtos.criteria.MisconfiguredCriteriaPayload;
import fr.sylvainjanet.tracker.architecturefixture.application.port.out.dtos.criteria.builder.CorrectDtoCriteriaBuilder;
import fr.sylvainjanet.tracker.architecturefixture.application.port.out.dtos.criteria.builder.InvalidCriteriaBuilderFunction;
import fr.sylvainjanet.tracker.architecturefixture.application.port.out.dtos.criteria.unsupported.UnsupportedCriteriaDtoPackage;
import fr.sylvainjanet.tracker.architecturefixture.application.port.out.dtos.instruction.CorrectlyNamedInstruction;
import fr.sylvainjanet.tracker.architecturefixture.application.port.out.dtos.instruction.InstructionKindInstruction;
import fr.sylvainjanet.tracker.architecturefixture.application.port.out.dtos.instruction.MisconfiguredInstructionPayload;
import fr.sylvainjanet.tracker.architecturefixture.application.port.out.dtos.instruction.builder.CorrectDtoInstructionBuilder;
import fr.sylvainjanet.tracker.architecturefixture.application.port.out.dtos.instruction.builder.InvalidInstructionBuilderFunction;
import fr.sylvainjanet.tracker.architecturefixture.application.port.out.dtos.instruction.unsupported.UnsupportedInstructionDtoPackage;
import fr.sylvainjanet.tracker.architecturefixture.application.port.out.dtos.outcome.CorrectlyNamedOutcome;
import fr.sylvainjanet.tracker.architecturefixture.application.port.out.dtos.outcome.MisconfiguredOutcomePayload;
import fr.sylvainjanet.tracker.architecturefixture.application.port.out.dtos.outcome.OutcomeKindOutcome;
import fr.sylvainjanet.tracker.architecturefixture.application.port.out.dtos.outcome.builder.CorrectDtoOutcomeBuilder;
import fr.sylvainjanet.tracker.architecturefixture.application.port.out.dtos.outcome.builder.InvalidOutcomeBuilderFunction;
import fr.sylvainjanet.tracker.architecturefixture.application.port.out.dtos.outcome.unsupported.UnsupportedOutcomeDtoPackage;
import fr.sylvainjanet.tracker.architecturefixture.application.port.out.dtos.unsupported.UnsupportedOutboundDto;
import fr.sylvainjanet.tracker.architecturefixture.application.port.out.gateway.store.CorrectlyNamedStore;
import fr.sylvainjanet.tracker.architecturefixture.application.port.out.gateway.store.MisconfiguredGatewayContract;
import fr.sylvainjanet.tracker.architecturefixture.application.port.out.gateway.unsupported.UnsupportedGatewayType;
import fr.sylvainjanet.tracker.architecturefixture.application.port.out.unsupported.UnsupportedOutboundPortType;
import fr.sylvainjanet.tracker.architecturefixture.domain.MisplacedCriteria;
import fr.sylvainjanet.tracker.architecturefixture.domain.MisplacedInstruction;
import fr.sylvainjanet.tracker.architecturefixture.domain.MisplacedOutcome;
import fr.sylvainjanet.tracker.architecturefixture.domain.MisplacedStore;
import org.junit.jupiter.api.Test;

public class StructuralOutboundApplicationConventionContractTest {

    @Test
    void outboundPortsUseAllowedPackages() {
        ArchitectureRuleContract.assertAccepts(
                outboundPortsUseAllowedPackages, CorrectlyNamedStore.class);
        ArchitectureRuleContract.assertRejects(
                outboundPortsUseAllowedPackages,
                "UnsupportedOutboundPortType",
                UnsupportedOutboundPortType.class);
    }

    @Test
    void outboundPortDtosUseAllowedPackages() {
        ArchitectureRuleContract.assertAccepts(
                outboundPortDtosUseAllowedPackages, CorrectlyNamedCriteria.class);
        ArchitectureRuleContract.assertRejects(
                outboundPortDtosUseAllowedPackages,
                "UnsupportedOutboundDto",
                UnsupportedOutboundDto.class);
    }

    @Test
    void criteriaDtosUseAllowedPackages() {
        ArchitectureRuleContract.assertAccepts(
                criteriaDtosUseAllowedPackages, CorrectDtoCriteriaBuilder.class);
        ArchitectureRuleContract.assertRejects(
                criteriaDtosUseAllowedPackages,
                "UnsupportedCriteriaDtoPackage",
                UnsupportedCriteriaDtoPackage.class);
    }

    @Test
    void criteriaBuildersHaveBuilderSuffix() {
        ArchitectureRuleContract.assertAccepts(
                criteriaBuildersHaveBuilderSuffix, CorrectDtoCriteriaBuilder.class);
        ArchitectureRuleContract.assertRejects(
                criteriaBuildersHaveBuilderSuffix,
                "InvalidCriteriaBuilderFunction",
                InvalidCriteriaBuilderFunction.class);
    }

    @Test
    void criteriaBuildersShouldBeFinalClasses() {
        ArchitectureRuleContract.assertAccepts(
                criteriaBuildersShouldBeFinalClasses, CorrectDtoCriteriaBuilder.class);
        ArchitectureRuleContract.assertRejects(
                criteriaBuildersShouldBeFinalClasses,
                "InvalidCriteriaBuilderFunction",
                InvalidCriteriaBuilderFunction.class);
    }

    @Test
    void criteriaDtosHaveCriteriaSuffix() {
        ArchitectureRuleContract.assertAccepts(
                criteriaDtosHaveCriteriaSuffix,
                CorrectlyNamedCriteria.class,
                CriteriaKindCriteria.class);
        ArchitectureRuleContract.assertRejects(
                criteriaDtosHaveCriteriaSuffix,
                "MisconfiguredCriteriaPayload",
                MisconfiguredCriteriaPayload.class);
    }

    @Test
    void classesNamedCriteriaStayInCriteriaPackages() {
        ArchitectureRuleContract.assertAccepts(
                classesNamedCriteriaStayInCriteriaPackages, CorrectlyNamedCriteria.class);
        ArchitectureRuleContract.assertRejects(
                classesNamedCriteriaStayInCriteriaPackages,
                "MisplacedCriteria",
                MisplacedCriteria.class);
    }

    @Test
    void criteriaDtosAreRecordsOrEnums() {
        ArchitectureRuleContract.assertAccepts(
                criteriaDtosAreRecordsOrEnums,
                CorrectlyNamedCriteria.class,
                CriteriaKindCriteria.class);
        ArchitectureRuleContract.assertRejects(
                criteriaDtosAreRecordsOrEnums,
                "MisconfiguredCriteriaPayload",
                MisconfiguredCriteriaPayload.class);
    }

    @Test
    void instructionDtosUseAllowedPackages() {
        ArchitectureRuleContract.assertAccepts(
                instructionDtosUseAllowedPackages, CorrectDtoInstructionBuilder.class);
        ArchitectureRuleContract.assertRejects(
                instructionDtosUseAllowedPackages,
                "UnsupportedInstructionDtoPackage",
                UnsupportedInstructionDtoPackage.class);
    }

    @Test
    void instructionBuildersHaveBuilderSuffix() {
        ArchitectureRuleContract.assertAccepts(
                instructionBuildersHaveBuilderSuffix, CorrectDtoInstructionBuilder.class);
        ArchitectureRuleContract.assertRejects(
                instructionBuildersHaveBuilderSuffix,
                "InvalidInstructionBuilderFunction",
                InvalidInstructionBuilderFunction.class);
    }

    @Test
    void instructionBuildersShouldBeFinalClasses() {
        ArchitectureRuleContract.assertAccepts(
                instructionBuildersShouldBeFinalClasses, CorrectDtoInstructionBuilder.class);
        ArchitectureRuleContract.assertRejects(
                instructionBuildersShouldBeFinalClasses,
                "InvalidInstructionBuilderFunction",
                InvalidInstructionBuilderFunction.class);
    }

    @Test
    void instructionDtosHaveInstructionSuffix() {
        ArchitectureRuleContract.assertAccepts(
                instructionDtosHaveInstructionSuffix,
                CorrectlyNamedInstruction.class,
                InstructionKindInstruction.class);
        ArchitectureRuleContract.assertRejects(
                instructionDtosHaveInstructionSuffix,
                "MisconfiguredInstructionPayload",
                MisconfiguredInstructionPayload.class);
    }

    @Test
    void classesNamedInstructionStayInInstructionPackages() {
        ArchitectureRuleContract.assertAccepts(
                classesNamedInstructionStayInInstructionPackages, CorrectlyNamedInstruction.class);
        ArchitectureRuleContract.assertRejects(
                classesNamedInstructionStayInInstructionPackages,
                "MisplacedInstruction",
                MisplacedInstruction.class);
    }

    @Test
    void instructionDtosAreRecordsOrEnums() {
        ArchitectureRuleContract.assertAccepts(
                instructionDtosAreRecordsOrEnums,
                CorrectlyNamedInstruction.class,
                InstructionKindInstruction.class);
        ArchitectureRuleContract.assertRejects(
                instructionDtosAreRecordsOrEnums,
                "MisconfiguredInstructionPayload",
                MisconfiguredInstructionPayload.class);
    }

    @Test
    void outcomeDtosUseAllowedPackages() {
        ArchitectureRuleContract.assertAccepts(
                outcomeDtosUseAllowedPackages, CorrectDtoOutcomeBuilder.class);
        ArchitectureRuleContract.assertRejects(
                outcomeDtosUseAllowedPackages,
                "UnsupportedOutcomeDtoPackage",
                UnsupportedOutcomeDtoPackage.class);
    }

    @Test
    void outcomeBuildersHaveBuilderSuffix() {
        ArchitectureRuleContract.assertAccepts(
                outcomeBuildersHaveBuilderSuffix, CorrectDtoOutcomeBuilder.class);
        ArchitectureRuleContract.assertRejects(
                outcomeBuildersHaveBuilderSuffix,
                "InvalidOutcomeBuilderFunction",
                InvalidOutcomeBuilderFunction.class);
    }

    @Test
    void outcomeBuildersShouldBeFinalClasses() {
        ArchitectureRuleContract.assertAccepts(
                outcomeBuildersShouldBeFinalClasses, CorrectDtoOutcomeBuilder.class);
        ArchitectureRuleContract.assertRejects(
                outcomeBuildersShouldBeFinalClasses,
                "InvalidOutcomeBuilderFunction",
                InvalidOutcomeBuilderFunction.class);
    }

    @Test
    void outcomeDtosHaveOutcomeSuffix() {
        ArchitectureRuleContract.assertAccepts(
                outcomeDtosHaveOutcomeSuffix,
                CorrectlyNamedOutcome.class,
                OutcomeKindOutcome.class);
        ArchitectureRuleContract.assertRejects(
                outcomeDtosHaveOutcomeSuffix,
                "MisconfiguredOutcomePayload",
                MisconfiguredOutcomePayload.class);
    }

    @Test
    void classesNamedOutcomeStayInOutcomePackages() {
        ArchitectureRuleContract.assertAccepts(
                classesNamedOutcomeStayInOutcomePackages, CorrectlyNamedOutcome.class);
        ArchitectureRuleContract.assertRejects(
                classesNamedOutcomeStayInOutcomePackages,
                "MisplacedOutcome",
                MisplacedOutcome.class);
    }

    @Test
    void outcomeDtosAreRecordsOrEnums() {
        ArchitectureRuleContract.assertAccepts(
                outcomeDtosAreRecordsOrEnums,
                CorrectlyNamedOutcome.class,
                OutcomeKindOutcome.class);
        ArchitectureRuleContract.assertRejects(
                outcomeDtosAreRecordsOrEnums,
                "MisconfiguredOutcomePayload",
                MisconfiguredOutcomePayload.class);
    }

    @Test
    void outboundGatewaysUseAllowedPackages() {
        ArchitectureRuleContract.assertAccepts(
                outboundGatewaysUseAllowedPackages, CorrectlyNamedStore.class);
        ArchitectureRuleContract.assertRejects(
                outboundGatewaysUseAllowedPackages,
                "UnsupportedGatewayType",
                UnsupportedGatewayType.class);
    }

    @Test
    void outboundStoresHaveStoreSuffix() {
        ArchitectureRuleContract.assertAccepts(
                outboundStoresHaveStoreSuffix, CorrectlyNamedStore.class);
        ArchitectureRuleContract.assertRejects(
                outboundStoresHaveStoreSuffix,
                "MisconfiguredGatewayContract",
                MisconfiguredGatewayContract.class);
    }

    @Test
    void classesNamedStoreStayInStorePackages() {
        ArchitectureRuleContract.assertAccepts(
                classesNamedStoreStayInStorePackages, CorrectlyNamedStore.class);
        ArchitectureRuleContract.assertRejects(
                classesNamedStoreStayInStorePackages, "MisplacedStore", MisplacedStore.class);
    }

    @Test
    void outboundStoresShouldBeInterfaces() {
        ArchitectureRuleContract.assertAccepts(
                outboundStoresShouldBeInterfaces, CorrectlyNamedStore.class);
        ArchitectureRuleContract.assertRejects(
                outboundStoresShouldBeInterfaces,
                "MisconfiguredGatewayContract",
                MisconfiguredGatewayContract.class);
    }
}
