// @ts-check

'use strict';

const architecture = require('../architecture');
const { matchDependencies } = require('../tools/match-dependencies');

/** @type {import('../types').RuleDefinition} */
module.exports = {
  messages: {
    invalidDependency:
      '{{filePath}} cannot depend on {{dependencyPath}}: ' +
      '{{sourceLayer}} may only depend on [{{allowedLayers}}].',
  },

  check(file, expected) {
    const allowedLayers = expected.metadata.allowedDependencyLayers;

    const allowedApplicationRootLayers =
      expected.metadata.allowedApplicationRootDependencyLayers ?? [];

    if (
      file.path.relativePath === null ||
      file.path.isTest ||
      expected.violation !== null ||
      expected.module === null ||
      allowedLayers === undefined
    ) {
      return [];
    }

    /** @type {import('../types').RuleViolation[]} */
    const violations = [];
    const dependencies = matchDependencies(architecture, file.path, file.dependencies);

    for (const dependency of dependencies) {
      const targetModule = dependency.target.module;
      const targetLayer = dependency.target.layer;

      if (targetModule === null || targetLayer === null) {
        continue;
      }

      const sameModule = targetModule.root === expected.module.root;
      const targetsApplicationRoot = targetModule.kind === 'application';

      if (!sameModule && !targetsApplicationRoot) {
        continue;
      }

      const permittedLayers = sameModule ? allowedLayers : allowedApplicationRootLayers;

      if (permittedLayers.includes(targetLayer)) {
        continue;
      }

      violations.push({
        node: file.node,
        messageId: 'invalidDependency',
        data: {
          filePath: `src/app/${file.path.relativePath}`,
          dependencyPath: dependency.path.value,
          sourceLayer: expected.layer ?? 'unclassified source',
          allowedLayers: permittedLayers.join(', '),
        },
      });
    }

    return violations;
  },
};
