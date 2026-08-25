// @ts-check

'use strict';

const architecture = require('./architecture');
const { analyzeFile } = require('./tools/analyze-file');
const { matchArchitecture } = require('./tools/match-architecture');

/**
 * @param {import('./types').RuleDefinition} definition
 * @returns {import('eslint').Rule.RuleModule}
 */
function createRule(definition) {
  return {
    meta: {
      type: 'problem',
      schema: [],
      messages: definition.messages,
    },

    create(context) {
      return {
        Program(program) {
          const typedProgram = /** @type {import('@typescript-eslint/utils').TSESTree.Program} */ (
            program
          );
          const file = analyzeFile(context.filename, typedProgram);
          const expected = matchArchitecture(architecture, file.path);
          const violations = definition.check(file, expected);

          for (const violation of violations) {
            context.report(violation);
          }
        },
      };
    },
  };
}

module.exports = { createRule };
