// @ts-check

'use strict';

const { analyzePath } = require('./analyze-path');
const { analyzeDeclarations, analyzeDeclaredNames } = require('./analyze-declarations');
const { analyzeDependencies } = require('./analyze-dependencies');

/**
 * @param {string} filePath
 * @param {import('@typescript-eslint/utils').TSESTree.Program} program
 * @returns {import('../types').AnalyzedFile}
 */
function analyzeFile(filePath, program) {
  return {
    node: program,
    path: analyzePath(filePath),
    declarations: analyzeDeclarations(program),
    declaredNames: analyzeDeclaredNames(program),
    dependencies: analyzeDependencies(program),
  };
}

module.exports = { analyzeFile };
