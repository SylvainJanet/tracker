package fr.sylvainjanet.tracker.architecture.contract.tests;

import static fr.sylvainjanet.tracker.architecture.contract.ArchitectureRuleContract.assertAccepts;
import static fr.sylvainjanet.tracker.architecture.contract.ArchitectureRuleContract.assertRejects;
import static fr.sylvainjanet.tracker.architecture.tests.DomainContractArchitectureTest.domainEnumsAreEnums;
import static fr.sylvainjanet.tracker.architecture.tests.DomainContractArchitectureTest.domainObjectImplementationsBelongToDomainPackages;
import static fr.sylvainjanet.tracker.architecture.tests.DomainContractArchitectureTest.domainObjectsUseSemanticContracts;
import static fr.sylvainjanet.tracker.architecture.tests.DomainContractArchitectureTest.equalityExplicitTypesDeclareEqualsAndHashCode;
import static fr.sylvainjanet.tracker.architecture.tests.DomainContractArchitectureTest.selfTypedDomainContractsUseImplementingType;
import static fr.sylvainjanet.tracker.architecture.tests.DomainContractArchitectureTest.stringRepresentationExplicitTypesDeclareToString;

import fr.sylvainjanet.tracker.architecturefixture.application.service.MisplacedDomainContractTypes.MisplacedDomainValueObject;
import fr.sylvainjanet.tracker.architecturefixture.domain.contract.InvalidDomainContractTypes.MissingEqualsValueObject;
import fr.sylvainjanet.tracker.architecturefixture.domain.contract.InvalidDomainContractTypes.MissingHashCodeValueObject;
import fr.sylvainjanet.tracker.architecturefixture.domain.contract.InvalidDomainContractTypes.MissingToStringValueObject;
import fr.sylvainjanet.tracker.architecturefixture.domain.contract.InvalidDomainContractTypes.NonEnumDomainEnum;
import fr.sylvainjanet.tracker.architecturefixture.domain.contract.InvalidDomainContractTypes.RawDomainObject;
import fr.sylvainjanet.tracker.architecturefixture.domain.contract.InvalidDomainContractTypes.WrongSelfDomainEnum;
import fr.sylvainjanet.tracker.architecturefixture.domain.contract.InvalidDomainContractTypes.WrongSelfIdentifier;
import fr.sylvainjanet.tracker.architecturefixture.domain.contract.InvalidDomainContractTypes.WrongSelfValueObject;
import fr.sylvainjanet.tracker.architecturefixture.domain.contract.ValidDomainContractTypes.ValidAggregateObject;
import fr.sylvainjanet.tracker.architecturefixture.domain.contract.ValidDomainContractTypes.ValidDomainEnum;
import fr.sylvainjanet.tracker.architecturefixture.domain.contract.ValidDomainContractTypes.ValidEntityObject;
import fr.sylvainjanet.tracker.architecturefixture.domain.contract.ValidDomainContractTypes.ValidGenericAggregateObject;
import fr.sylvainjanet.tracker.architecturefixture.domain.contract.ValidDomainContractTypes.ValidGenericEntityObject;
import fr.sylvainjanet.tracker.architecturefixture.domain.contract.ValidDomainContractTypes.ValidGenericValueObject;
import fr.sylvainjanet.tracker.architecturefixture.domain.contract.ValidDomainContractTypes.ValidIdentifier;
import fr.sylvainjanet.tracker.architecturefixture.domain.contract.ValidDomainContractTypes.ValidValueObject;
import org.junit.jupiter.api.Test;

public class DomainContractArchitectureContractTest {

    @Test
    void domainObjectImplementationsBelongToDomainPackages() {
        assertAccepts(
                domainObjectImplementationsBelongToDomainPackages,
                ValidValueObject.class,
                ValidGenericValueObject.class,
                ValidIdentifier.class,
                ValidEntityObject.class,
                ValidGenericEntityObject.class,
                ValidAggregateObject.class,
                ValidGenericAggregateObject.class);

        assertRejects(
                domainObjectImplementationsBelongToDomainPackages,
                "MisplacedDomainValueObject",
                MisplacedDomainValueObject.class);
    }

    @Test
    void domainEnumsAreEnums() {
        assertAccepts(domainEnumsAreEnums, ValidDomainEnum.class);

        assertRejects(domainEnumsAreEnums, "NonEnumDomainEnum", NonEnumDomainEnum.class);
    }

    @Test
    void equalityExplicitTypesDeclareEqualsAndHashCode() {
        assertAccepts(equalityExplicitTypesDeclareEqualsAndHashCode, ValidValueObject.class);

        assertRejects(
                equalityExplicitTypesDeclareEqualsAndHashCode,
                "MissingEqualsValueObject",
                MissingEqualsValueObject.class);
        assertRejects(
                equalityExplicitTypesDeclareEqualsAndHashCode,
                "MissingHashCodeValueObject",
                MissingHashCodeValueObject.class);
    }

    @Test
    void stringRepresentationExplicitTypesDeclareToString() {
        assertAccepts(stringRepresentationExplicitTypesDeclareToString, ValidValueObject.class);

        assertRejects(
                stringRepresentationExplicitTypesDeclareToString,
                "MissingToStringValueObject",
                MissingToStringValueObject.class);
    }

    @Test
    void selfTypedDomainContractsUseImplementingType() {
        assertAccepts(
                selfTypedDomainContractsUseImplementingType,
                ValidGenericValueObject.class,
                ValidIdentifier.class,
                ValidDomainEnum.class);

        assertRejects(
                selfTypedDomainContractsUseImplementingType,
                "WrongSelfValueObject",
                WrongSelfValueObject.class);
        assertRejects(
                selfTypedDomainContractsUseImplementingType,
                "WrongSelfDomainEnum",
                WrongSelfDomainEnum.class);
        assertRejects(
                selfTypedDomainContractsUseImplementingType,
                "WrongSelfIdentifier",
                WrongSelfIdentifier.class);
    }

    @Test
    void domainObjectsUseSemanticContracts() {
        assertAccepts(
                domainObjectsUseSemanticContracts,
                ValidValueObject.class,
                ValidGenericValueObject.class,
                ValidIdentifier.class,
                ValidEntityObject.class,
                ValidGenericEntityObject.class,
                ValidAggregateObject.class,
                ValidGenericAggregateObject.class);

        assertRejects(domainObjectsUseSemanticContracts, "RawDomainObject", RawDomainObject.class);
    }
}
