// @ts-check

'use strict';

const IMPLEMENTATION = '[a-z0-9]+';
const RESPONSIBILITY = '[a-z0-9]+(?:-[a-z0-9]+)*';

const IMPLEMENTATION_PLACEHOLDER = '<implementation>';
const RESPONSIBILITY_PLACEHOLDER = '<responsibility>';
const SCOPE_PLACEHOLDER = '<scope>';

const IMPLEMENTATION_DESCRIPTION = '<kebab-case-implementation>';
const RESPONSIBILITY_DESCRIPTION = '<kebab-case-responsibility>';

/**
 * @param {import('../types').FileNaming} naming
 * @param {string[]} namingScope
 * @returns {import('../types').FileNameConvention}
 */
function createFileNameConvention(naming, namingScope) {
  const scope = namingScope.join('.');

  const allowedFileNames = naming.stems.flatMap((stem) =>
    naming.extensions.map((extension) => describeStem(stem, scope) + extension),
  );

  const patterns = naming.stems.flatMap((stem) =>
    naming.extensions.map((extension) => ({
      expression: new RegExp(`^${stemPattern(stem, scope)}${escapeRegex(extension)}$`),
      segment: findSegment(stem),
    })),
  );

  /**
   * @param {string} fileName
   * @returns {import('../types').FileNameMatch | null}
   */
  function match(fileName) {
    for (const pattern of patterns) {
      const result = pattern.expression.exec(fileName);

      if (result) {
        return {
          implementation: result.groups?.['implementation'] ?? null,
          responsibility: result.groups?.['responsibility'] ?? null,
          segment: pattern.segment,
        };
      }
    }

    return null;
  }

  return {
    allowedFileNames,
    match,

    matches(fileName) {
      return match(fileName) !== null;
    },
  };
}

/**
 * @param {string} stem
 * @param {string} scope
 */
function describeStem(stem, scope) {
  return stem
    .replaceAll(SCOPE_PLACEHOLDER, scope)
    .replaceAll(IMPLEMENTATION_PLACEHOLDER, IMPLEMENTATION_DESCRIPTION)
    .replaceAll(RESPONSIBILITY_PLACEHOLDER, RESPONSIBILITY_DESCRIPTION);
}

/**
 * @param {string} stem
 * @param {string} scope
 */
function stemPattern(stem, scope) {
  return stem
    .replaceAll(SCOPE_PLACEHOLDER, scope)
    .split(/(<implementation>|<responsibility>)/)
    .map((part) => {
      if (part === IMPLEMENTATION_PLACEHOLDER) {
        return `(?<implementation>${IMPLEMENTATION})`;
      }

      if (part === RESPONSIBILITY_PLACEHOLDER) {
        return `(?<responsibility>${RESPONSIBILITY})`;
      }

      return escapeRegex(part);
    })
    .join('');
}

/**
 * @param {string} stem
 * @returns {string | null}
 */
function findSegment(stem) {
  const segment = stem.split('.').at(-1) ?? '';
  return segment.includes('<') ? null : segment;
}

/**
 * @param {import('../types').ExpectedFile[]} expectedFiles
 * @param {string} fileName
 * @param {string[]} namingScope
 * @returns {import('../types').ExpectedFileMatch | null}
 */
function matchExpectedFile(expectedFiles, fileName, namingScope) {
  for (const expectedFile of expectedFiles) {
    const name = createFileNameConvention(expectedFile.naming, namingScope).match(fileName);

    if (name !== null) {
      return { expectedFile, name };
    }
  }

  return null;
}

/** @param {string} value */
function escapeRegex(value) {
  return value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

module.exports = {
  createFileNameConvention,
  matchExpectedFile,
};
