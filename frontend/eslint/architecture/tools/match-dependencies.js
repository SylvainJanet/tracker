// @ts-check

'use strict';

const { matchArchitecture } = require('./match-architecture');
const { resolveDependency } = require('./resolve-dependency');

/**
 * @param {import('../types').ArchitectureFolder} architecture
 * @param {import('../types').FilePath} source
 * @param {import('../types').AnalyzedDependency[]} dependencies
 * @returns {import('../types').MatchedDependency[]}
 */
function matchDependencies(architecture, source, dependencies) {
  return dependencies.flatMap((dependency) => {
    const path = resolveDependency(source, dependency);

    if (path === null || path.relativePath === null) {
      return [];
    }

    return [
      {
        source: dependency.source,
        path,
        target: matchArchitecture(architecture, path),
      },
    ];
  });
}

module.exports = { matchDependencies };
