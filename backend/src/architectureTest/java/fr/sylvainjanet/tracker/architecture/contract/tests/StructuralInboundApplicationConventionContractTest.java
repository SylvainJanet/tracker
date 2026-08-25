package fr.sylvainjanet.tracker.architecture.contract.tests;

import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.InboundPortRules.DtosRules.CommandRules.classesNamedCommandStayInCommandPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.InboundPortRules.DtosRules.CommandRules.commandDtosAreRecordsOrEnums;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.InboundPortRules.DtosRules.CommandRules.commandDtosHaveCommandSuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.InboundPortRules.DtosRules.QueryRules.classesNamedQueryStayInQueryPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.InboundPortRules.DtosRules.QueryRules.queryDtosAreRecordsOrEnums;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.InboundPortRules.DtosRules.QueryRules.queryDtosHaveQuerySuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.InboundPortRules.DtosRules.ResultRules.classesNamedResultStayInResultPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.InboundPortRules.DtosRules.ResultRules.resultDtosAreRecordsOrEnums;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.InboundPortRules.DtosRules.ResultRules.resultDtosHaveResultSuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.InboundPortRules.DtosRules.inboundPortDtosUseAllowedPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.InboundPortRules.ExceptionsRules.inboundPortExceptionsAreChecked;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.InboundPortRules.ExceptionsRules.inboundPortExceptionsHaveExceptionSuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.InboundPortRules.UsecaseRules.classesNamedUseCaseStayInUseCasePackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.InboundPortRules.UsecaseRules.inboundPortUseCasesAreInterfaces;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.InboundPortRules.UsecaseRules.inboundPortUseCasesHaveUseCaseSuffix;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.InboundPortRules.inboundPortsUseAllowedPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.PortRules.applicationPortsUseAllowedPackages;
import static fr.sylvainjanet.tracker.architecture.tests.StructuralConventionTest.ContextRules.ApplicationRules.applicationUsesAllowedPackages;

import fr.sylvainjanet.tracker.architecture.contract.ArchitectureRuleContract;
import fr.sylvainjanet.tracker.architecturefixture.application.port.in.dtos.command.CommandKindCommand;
import fr.sylvainjanet.tracker.architecturefixture.application.port.in.dtos.command.CorrectlyNamedCommand;
import fr.sylvainjanet.tracker.architecturefixture.application.port.in.dtos.command.MisconfiguredCommandPayload;
import fr.sylvainjanet.tracker.architecturefixture.application.port.in.dtos.query.CorrectlyNamedQuery;
import fr.sylvainjanet.tracker.architecturefixture.application.port.in.dtos.query.MisconfiguredQueryPayload;
import fr.sylvainjanet.tracker.architecturefixture.application.port.in.dtos.query.QueryKindQuery;
import fr.sylvainjanet.tracker.architecturefixture.application.port.in.dtos.result.CorrectlyNamedResult;
import fr.sylvainjanet.tracker.architecturefixture.application.port.in.dtos.result.MisconfiguredResultPayload;
import fr.sylvainjanet.tracker.architecturefixture.application.port.in.dtos.result.ResultKindResult;
import fr.sylvainjanet.tracker.architecturefixture.application.port.in.dtos.unsupported.UnsupportedInboundDto;
import fr.sylvainjanet.tracker.architecturefixture.application.port.in.exceptions.CorrectlyNamedException;
import fr.sylvainjanet.tracker.architecturefixture.application.port.in.exceptions.MisconfiguredInboundFailure;
import fr.sylvainjanet.tracker.architecturefixture.application.port.in.unsupported.UnsupportedInboundPortType;
import fr.sylvainjanet.tracker.architecturefixture.application.port.in.usecase.CorrectlyNamedUseCase;
import fr.sylvainjanet.tracker.architecturefixture.application.port.in.usecase.MisconfiguredInboundOperation;
import fr.sylvainjanet.tracker.architecturefixture.application.port.unsupported.UnsupportedApplicationPortType;
import fr.sylvainjanet.tracker.architecturefixture.application.service.CorrectlyNamedService;
import fr.sylvainjanet.tracker.architecturefixture.application.unsupported.UnsupportedApplicationType;
import fr.sylvainjanet.tracker.architecturefixture.domain.MisplacedCommand;
import fr.sylvainjanet.tracker.architecturefixture.domain.MisplacedQuery;
import fr.sylvainjanet.tracker.architecturefixture.domain.MisplacedResult;
import fr.sylvainjanet.tracker.architecturefixture.domain.MisplacedUseCase;
import org.junit.jupiter.api.Test;

public class StructuralInboundApplicationConventionContractTest {

    @Test
    void applicationUsesAllowedPackages() {
        ArchitectureRuleContract.assertAccepts(
                applicationUsesAllowedPackages, CorrectlyNamedService.class);
        ArchitectureRuleContract.assertRejects(
                applicationUsesAllowedPackages,
                "UnsupportedApplicationType",
                UnsupportedApplicationType.class);
    }

    @Test
    void applicationPortsUseAllowedPackages() {
        ArchitectureRuleContract.assertAccepts(
                applicationPortsUseAllowedPackages, CorrectlyNamedUseCase.class);
        ArchitectureRuleContract.assertRejects(
                applicationPortsUseAllowedPackages,
                "UnsupportedApplicationPortType",
                UnsupportedApplicationPortType.class);
    }

    @Test
    void inboundPortsUseAllowedPackages() {
        ArchitectureRuleContract.assertAccepts(
                inboundPortsUseAllowedPackages, CorrectlyNamedUseCase.class);
        ArchitectureRuleContract.assertRejects(
                inboundPortsUseAllowedPackages,
                "UnsupportedInboundPortType",
                UnsupportedInboundPortType.class);
    }

    @Test
    void inboundPortDtosUseAllowedPackages() {
        ArchitectureRuleContract.assertAccepts(
                inboundPortDtosUseAllowedPackages, CorrectlyNamedCommand.class);
        ArchitectureRuleContract.assertRejects(
                inboundPortDtosUseAllowedPackages,
                "UnsupportedInboundDto",
                UnsupportedInboundDto.class);
    }

    @Test
    void commandDtosHaveCommandSuffix() {
        ArchitectureRuleContract.assertAccepts(
                commandDtosHaveCommandSuffix,
                CorrectlyNamedCommand.class,
                CommandKindCommand.class);
        ArchitectureRuleContract.assertRejects(
                commandDtosHaveCommandSuffix,
                "MisconfiguredCommandPayload",
                MisconfiguredCommandPayload.class);
    }

    @Test
    void classesNamedCommandStayInCommandPackages() {
        ArchitectureRuleContract.assertAccepts(
                classesNamedCommandStayInCommandPackages, CorrectlyNamedCommand.class);
        ArchitectureRuleContract.assertRejects(
                classesNamedCommandStayInCommandPackages,
                "MisplacedCommand",
                MisplacedCommand.class);
    }

    @Test
    void commandDtosAreRecordsOrEnums() {
        ArchitectureRuleContract.assertAccepts(
                commandDtosAreRecordsOrEnums,
                CorrectlyNamedCommand.class,
                CommandKindCommand.class);
        ArchitectureRuleContract.assertRejects(
                commandDtosAreRecordsOrEnums,
                "MisconfiguredCommandPayload",
                MisconfiguredCommandPayload.class);
    }

    @Test
    void queryDtosHaveQuerySuffix() {
        ArchitectureRuleContract.assertAccepts(
                queryDtosHaveQuerySuffix, CorrectlyNamedQuery.class, QueryKindQuery.class);
        ArchitectureRuleContract.assertRejects(
                queryDtosHaveQuerySuffix,
                "MisconfiguredQueryPayload",
                MisconfiguredQueryPayload.class);
    }

    @Test
    void classesNamedQueryStayInQueryPackages() {
        ArchitectureRuleContract.assertAccepts(
                classesNamedQueryStayInQueryPackages, CorrectlyNamedQuery.class);
        ArchitectureRuleContract.assertRejects(
                classesNamedQueryStayInQueryPackages, "MisplacedQuery", MisplacedQuery.class);
    }

    @Test
    void queryDtosAreRecordsOrEnums() {
        ArchitectureRuleContract.assertAccepts(
                queryDtosAreRecordsOrEnums, CorrectlyNamedQuery.class, QueryKindQuery.class);
        ArchitectureRuleContract.assertRejects(
                queryDtosAreRecordsOrEnums,
                "MisconfiguredQueryPayload",
                MisconfiguredQueryPayload.class);
    }

    @Test
    void resultDtosHaveResultSuffix() {
        ArchitectureRuleContract.assertAccepts(
                resultDtosHaveResultSuffix, CorrectlyNamedResult.class, ResultKindResult.class);
        ArchitectureRuleContract.assertRejects(
                resultDtosHaveResultSuffix,
                "MisconfiguredResultPayload",
                MisconfiguredResultPayload.class);
    }

    @Test
    void classesNamedResultStayInResultPackages() {
        ArchitectureRuleContract.assertAccepts(
                classesNamedResultStayInResultPackages, CorrectlyNamedResult.class);
        ArchitectureRuleContract.assertRejects(
                classesNamedResultStayInResultPackages, "MisplacedResult", MisplacedResult.class);
    }

    @Test
    void resultDtosAreRecordsOrEnums() {
        ArchitectureRuleContract.assertAccepts(
                resultDtosAreRecordsOrEnums, CorrectlyNamedResult.class, ResultKindResult.class);
        ArchitectureRuleContract.assertRejects(
                resultDtosAreRecordsOrEnums,
                "MisconfiguredResultPayload",
                MisconfiguredResultPayload.class);
    }

    @Test
    void inboundPortExceptionsHaveExceptionSuffix() {
        ArchitectureRuleContract.assertAccepts(
                inboundPortExceptionsHaveExceptionSuffix, CorrectlyNamedException.class);
        ArchitectureRuleContract.assertRejects(
                inboundPortExceptionsHaveExceptionSuffix,
                "MisconfiguredInboundFailure",
                MisconfiguredInboundFailure.class);
    }

    @Test
    void inboundPortExceptionsAreChecked() {
        ArchitectureRuleContract.assertAccepts(
                inboundPortExceptionsAreChecked, CorrectlyNamedException.class);
        ArchitectureRuleContract.assertRejects(
                inboundPortExceptionsAreChecked,
                "MisconfiguredInboundFailure",
                MisconfiguredInboundFailure.class);
    }

    @Test
    void inboundPortUseCasesHaveUseCaseSuffix() {
        ArchitectureRuleContract.assertAccepts(
                inboundPortUseCasesHaveUseCaseSuffix, CorrectlyNamedUseCase.class);
        ArchitectureRuleContract.assertRejects(
                inboundPortUseCasesHaveUseCaseSuffix,
                "MisconfiguredInboundOperation",
                MisconfiguredInboundOperation.class);
    }

    @Test
    void classesNamedUseCaseStayInUseCasePackages() {
        ArchitectureRuleContract.assertAccepts(
                classesNamedUseCaseStayInUseCasePackages, CorrectlyNamedUseCase.class);
        ArchitectureRuleContract.assertRejects(
                classesNamedUseCaseStayInUseCasePackages,
                "MisplacedUseCase",
                MisplacedUseCase.class);
    }

    @Test
    void inboundPortUseCasesAreInterfaces() {
        ArchitectureRuleContract.assertAccepts(
                inboundPortUseCasesAreInterfaces, CorrectlyNamedUseCase.class);
        ArchitectureRuleContract.assertRejects(
                inboundPortUseCasesAreInterfaces,
                "MisconfiguredInboundOperation",
                MisconfiguredInboundOperation.class);
    }
}
