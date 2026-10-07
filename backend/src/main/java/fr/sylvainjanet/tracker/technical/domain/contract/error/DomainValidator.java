package fr.sylvainjanet.tracker.technical.domain.contract.error;

import fr.sylvainjanet.tracker.technical.domain.MultiSet;
import fr.sylvainjanet.tracker.technical.domain.contract.base.DomainEnum;
import fr.sylvainjanet.tracker.technical.domain.contract.base.DomainValidated;
import fr.sylvainjanet.tracker.technical.domain.contract.error.exception.DomainValidationException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class DomainValidator {

    private DomainValidator() {}

    public static <
                    T extends DomainValidated<T, K, M>,
                    K extends Enum<K> & DomainEnum<K>,
                    M extends DomainValidationErrorMetadata>
            void validate(DomainValidated<T, K, M> domainValidated) {
        throwErrors(domainValidated.validate());
    }

    public static <
                    T extends DomainValidated<T, K, M>,
                    K extends Enum<K> & DomainEnum<K>,
                    M extends DomainValidationErrorMetadata>
            void throwErrors(Set<DomainValidationError<K, M>> validationErrors) {
        if (!validationErrors.isEmpty()) {
            throw new DomainValidationException(exceptionMessage(validationErrors));
        }
    }

    private static <K extends Enum<K> & DomainEnum<K>, M extends DomainValidationErrorMetadata>
            String exceptionMessage(Set<DomainValidationError<K, M>> validationErrors) {

        return validationErrors.stream()
                .collect(
                        Collectors.groupingBy(
                                DomainValidationError::kind,
                                Collectors.mapping(
                                        DomainValidationError::publicMessage,
                                        MultiSet.toMultiSet())))
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().name()))
                .map(
                        entry -> {
                            DomainEnum<K> kind = entry.getKey();
                            MultiSet<String> messages = entry.getValue();
                            List<String> formattedMessages =
                                    messages.distinctElements().stream()
                                            .sorted()
                                            .map(
                                                    message -> {
                                                        int count = messages.count(message);
                                                        return "PublicMessage"
                                                                + (count > 1
                                                                        ? " (x" + count + ")"
                                                                        : "")
                                                                + ": "
                                                                + message;
                                                    })
                                            .toList();
                            return "DomainValidationError[kind="
                                    + kind
                                    + ", messages="
                                    + formattedMessages
                                    + "]";
                        })
                .toList()
                .toString();
    }
}
