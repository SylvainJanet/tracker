// @ts-check

'use strict';

const PASCAL_FILE_STEM = '<PascalFileStem>';
const LOWER_CAMEL_FILE_STEM = '<LowerCamelFileStem>';
const PASCAL_RESPONSIBILITY = '<PascalResponsibility>';
const PASCAL_IMPLEMENTATION = '<PascalImplementation>';
const PASCAL_SEGMENT = '<PascalSegment>';
const PASCAL_SCOPE = '<PascalScope>';
const LOWER_CAMEL_SCOPE = '<LowerCamelScope>';
const UPPER_SNAKE_SCOPE = '<UpperSnakeScope>';
const PASCAL_NAME = '<PascalName>';
const PASCAL_LOCAL_SCOPE = '<PascalLocalScope>';
const OPTIONAL_PASCAL_NAME = '<OptionalPascalName>';
const UPPER_SNAKE_NAME_CONTAINING_SCOPE = '<UpperSnakeNameContainingScope>';

/** @param {string} value */
function toLowerCamelCase(value) {
  return value.charAt(0).toLowerCase() + value.slice(1);
}

/** @param {string} value */
function toPascalCase(value) {
  return value
    .split(/[.-]/)
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join('');
}

/** @param {string} value */
function toUpperSnakeCase(value) {
  return value
    .replace(/([a-z0-9])([A-Z])/g, '$1_$2')
    .replace(/([A-Z])([A-Z][a-z])/g, '$1_$2')
    .toUpperCase();
}

/**
 * @param {string} expectedName
 * @param {string} fileStem
 * @param {import('../types').FileNameMatch} matchedName
 * @param {string[]} [namingScope=[]]
 */
function resolveDeclarationName(expectedName, fileStem, matchedName, namingScope = []) {
  const pascalFileStem = toPascalCase(fileStem);
  const pascalScope = toPascalCase(namingScope.join('.'));
  const pascalLocalScope = toPascalCase(namingScope.at(-1) ?? '');

  return expectedName
    .replaceAll(PASCAL_FILE_STEM, pascalFileStem)
    .replaceAll(LOWER_CAMEL_FILE_STEM, toLowerCamelCase(pascalFileStem))
    .replaceAll(PASCAL_SCOPE, pascalScope)
    .replaceAll(LOWER_CAMEL_SCOPE, toLowerCamelCase(pascalScope))
    .replaceAll(UPPER_SNAKE_SCOPE, toUpperSnakeCase(pascalScope))
    .replaceAll(PASCAL_IMPLEMENTATION, toPascalCase(matchedName.implementation ?? ''))
    .replaceAll(PASCAL_RESPONSIBILITY, toPascalCase(matchedName.responsibility ?? ''))
    .replaceAll(PASCAL_SEGMENT, toPascalCase(matchedName.segment ?? ''))
    .replaceAll(PASCAL_LOCAL_SCOPE, pascalLocalScope);
}

/**
 * @param {string} actualName
 * @param {string} expectedName
 * @param {string} fileStem
 * @param {import('../types').FileNameMatch} matchedName
 * @param {string[]} [namingScope=[]]
 */
function matchesDeclarationName(actualName, expectedName, fileStem, matchedName, namingScope = []) {
  const resolvedName = resolveDeclarationName(expectedName, fileStem, matchedName, namingScope);
  const upperSnakeScope = toUpperSnakeCase(toPascalCase(namingScope.join('.')));

  const pattern = resolvedName
    .split(/(<UpperSnakeNameContainingScope>|<OptionalPascalName>|<PascalName>)/)
    .map((part) => {
      if (part === PASCAL_NAME) {
        return '[A-Z][A-Za-z0-9]*';
      }

      if (part === OPTIONAL_PASCAL_NAME) {
        return '(?:[A-Z][A-Za-z0-9]*)?';
      }

      if (part === UPPER_SNAKE_NAME_CONTAINING_SCOPE) {
        const scope = escapeRegex(upperSnakeScope);

        return `(?=(?:[A-Z0-9]+_)*${scope}(?:_|$))` + '[A-Z][A-Z0-9]*(?:_[A-Z0-9]+)*';
      }

      return escapeRegex(part);
    })
    .join('');

  return new RegExp(`^${pattern}$`).test(actualName);
}

/** @param {string} value */
function escapeRegex(value) {
  return value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

module.exports = {
  toLowerCamelCase,
  toPascalCase,
  toUpperSnakeCase,
  resolveDeclarationName,
  matchesDeclarationName,
};
