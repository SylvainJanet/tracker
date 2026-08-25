// @ts-check

'use strict';

const { createRule } = require('./create-rule');
const filenameOnly = require('./processors/filename-only');
const fileStructure = require('./rules/file-structure');
const fileNaming = require('./rules/file-naming');
const declarations = require('./rules/declarations');
const reservedVocabulary = require('./rules/reserved-vocabulary');
const hexagonalArchitecture = require('./rules/hexagonal-architecture');
const moduleBoundaries = require('./rules/module-boundaries');

module.exports = /** @satisfies {import('eslint').ESLint.Plugin} */ ({
  processors: {
    'filename-only': filenameOnly,
  },

  rules: {
    'file-structure': createRule(fileStructure),
    'file-naming': createRule(fileNaming),
    declarations: createRule(declarations),
    'reserved-vocabulary': createRule(reservedVocabulary),
    'hexagonal-architecture': createRule(hexagonalArchitecture),
    'module-boundaries': createRule(moduleBoundaries),
  },
});
