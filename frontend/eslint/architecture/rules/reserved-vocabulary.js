// @ts-check

'use strict';

/** @type {import('../types').RuleDefinition} */
module.exports = {
  messages: {
    reservedVocabulary:
      '{{filePath}} declares {{declarationName}} using the reserved ' +
      '{{reservedSuffix}} suffix outside an architectural location that allows it.',
  },

  check(file, expected) {
    if (file.path.relativePath === null || file.path.isTest || expected.violation !== null) {
      return [];
    }

    const reserved = expected.metadata.reservedDeclarationSuffixes ?? [];
    const allowed = expected.metadata.allowedDeclarationSuffixes ?? [];
    const violations = [];

    for (const declarationName of file.declaredNames) {
      const reservedSuffix = reserved.find(
        (suffix) => declarationName.length > suffix.length && declarationName.endsWith(suffix),
      );

      if (reservedSuffix === undefined || allowed.includes(reservedSuffix)) {
        continue;
      }

      violations.push({
        node: file.node,
        messageId: 'reservedVocabulary',
        data: {
          filePath: `src/app/${file.path.relativePath}`,
          declarationName,
          reservedSuffix,
        },
      });
    }

    return violations;
  },
};
