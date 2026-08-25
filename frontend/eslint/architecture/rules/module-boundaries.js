// @ts-check

'use strict';

const architecture = require('../architecture');
const { matchDependencies } = require('../tools/match-dependencies');

/** @type {import('../types').RuleDefinition} */
module.exports = {
  messages: {
    internalModuleDependency:
      '{{filePath}} cannot depend directly on {{dependencyPath}}: ' +
      'module {{targetModule}} must be accessed through its api folder.',
    forbiddenContextDependency:
      '{{filePath}} cannot depend on {{dependencyPath}}: ' +
      '{{sourceLayer}} cannot depend on another context; ' +
      "only adapter/out may use another context's public API.",
  },

  check(file, expected) {
    if (file.path.relativePath === null || file.path.isTest || expected.violation !== null) {
      return [];
    }

    const sourceModule = isPublishedModule(expected.module) ? expected.module : null;
    /** @type {import('../types').RuleViolation[]} */
    const violations = [];

    for (const dependency of matchDependencies(architecture, file.path, file.dependencies)) {
      const targetModule = dependency.target.module;

      if (
        targetModule === null ||
        !isPublishedModule(targetModule) ||
        targetModule.root === sourceModule?.root
      ) {
        continue;
      }

      if (dependency.target.metadata.visibility !== 'public-api') {
        violations.push({
          node: file.node,
          messageId: 'internalModuleDependency',
          data: {
            filePath: `src/app/${file.path.relativePath}`,
            dependencyPath: dependency.path.value,
            targetModule: targetModule.root,
          },
        });

        continue;
      }

      if (
        sourceModule?.kind === 'context' &&
        targetModule.kind === 'context' &&
        expected.metadata.allowsCrossContextDependencies !== true
      ) {
        violations.push({
          node: file.node,
          messageId: 'forbiddenContextDependency',
          data: {
            filePath: `src/app/${file.path.relativePath}`,
            dependencyPath: dependency.path.value,
            sourceLayer: expected.layer ?? 'unclassified source',
          },
        });
      }
    }

    return violations;
  },
};

/**
 * @param {import('../types').ArchitectureModule | null} module
 * @returns {boolean}
 */
function isPublishedModule(module) {
  return module !== null && ['context', 'shared', 'shell'].includes(module.kind);
}
