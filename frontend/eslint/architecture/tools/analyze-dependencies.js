// @ts-check

'use strict';

const { walkNodes } = require('./walk-nodes');

/**
 * @param {import('@typescript-eslint/utils').TSESTree.Program} program
 * @returns {import('../types').AnalyzedDependency[]}
 */
function analyzeDependencies(program) {
  /** @type {import('../types').AnalyzedDependency[]} */
  const dependencies = [];

  walkNodes(program, (node) => {
    switch (node.type) {
      case 'ImportDeclaration':
      case 'ExportNamedDeclaration':
      case 'ExportAllDeclaration':
      case 'ImportExpression': {
        const source = readStringValue(node.source);

        if (source !== null) {
          dependencies.push({ source });
        }

        break;
      }
    }
  });

  return dependencies;
}

/**
 * @param {unknown} value
 * @returns {string | null}
 */
function readStringValue(value) {
  if (value === null) {
    return null;
  }

  if (typeof value === 'object' && 'value' in value && typeof value.value === 'string') {
    return value.value;
  }

  return isNoSubstitutionTemplateLiteral(value) ? (value.quasis[0]?.value.cooked ?? null) : null;
}

/**
 * @param {unknown} value
 * @returns {value is import('@typescript-eslint/utils').TSESTree.TemplateLiteral}
 */
function isNoSubstitutionTemplateLiteral(value) {
  return (
    value !== null &&
    typeof value === 'object' &&
    'type' in value &&
    value.type === 'TemplateLiteral' &&
    'expressions' in value &&
    Array.isArray(value.expressions) &&
    value.expressions.length === 0 &&
    'quasis' in value &&
    Array.isArray(value.quasis) &&
    value.quasis.length === 1
  );
}

module.exports = { analyzeDependencies };
