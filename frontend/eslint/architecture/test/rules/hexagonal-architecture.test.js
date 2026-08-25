// @ts-check

'use strict';

const assert = require('node:assert/strict');
const { describe, it } = require('node:test');
const parser = require('@typescript-eslint/parser');

const architecture = require('../../architecture');
const rule = require('../../rules/hexagonal-architecture');
const { analyzeFile } = require('../../tools/analyze-file');
const { matchArchitecture } = require('../../tools/match-architecture');

const path = require('node:path');
const { ESLint } = require('eslint');
const tseslint = require('typescript-eslint');

const architecturePlugin = require('../../index');

/**
 * @param {string} source
 * @param {string} filePath
 */
function check(source, filePath) {
  const program = parser.parse(source, { sourceType: 'module' });
  const file = analyzeFile(filePath, program);
  const expected = matchArchitecture(architecture, file.path);

  return rule.check(file, expected);
}

const eslint = new ESLint({
  cwd: path.resolve(__dirname, '../fixtures'),
  overrideConfigFile: true,
  overrideConfig: [
    {
      files: ['**/*.ts'],
      languageOptions: {
        parser: tseslint.parser,
      },
      plugins: {
        architecture: architecturePlugin,
      },
      rules: {
        'architecture/hexagonal-architecture': 'error',
      },
    },
  ],
});

/** @param {string} filePath */
async function lintFixture(filePath) {
  const [result] = await eslint.lintFiles([filePath]);

  assert.ok(result);

  return result.messages;
}

describe('hexagonal architecture', () => {
  const filePath = 'src/app/contexts/nutrition/domain/food-entry.ts';

  it('accepts a dependency on an allowed layer', () => {
    assert.deepEqual(check("import { Ingredient } from './ingredient';", filePath), []);
  });

  it('rejects an outward dependency in the same module', () => {
    const violations = check("import { Service } from '../application/service/service';", filePath);

    assert.equal(violations.length, 1);
    assert.deepEqual(
      {
        messageId: violations[0]?.messageId,
        data: violations[0]?.data,
      },
      {
        messageId: 'invalidDependency',
        data: {
          filePath,
          dependencyPath: 'src/app/contexts/nutrition/application/service/service',
          sourceLayer: 'domain',
          allowedLayers: 'domain',
        },
      },
    );
  });
  it('validates explicit physical fixtures', async () => {
    assert.deepEqual(
      await lintFixture(
        'src/app/contexts/nutrition/application/service/' +
          'valid-hexagonal-architecture.service.ts',
      ),
      [],
    );

    const filePath = 'src/app/contexts/nutrition/domain/' + 'invalid-hexagonal-architecture.ts';
    const [message] = await lintFixture(filePath);

    assert.equal(message?.ruleId, 'architecture/hexagonal-architecture');
    assert.equal(message.messageId, 'invalidDependency');
    assert.equal(
      message.message,
      `${filePath} cannot depend on ` +
        'src/app/contexts/nutrition/application/service/' +
        'valid-hexagonal-architecture.service: ' +
        'domain may only depend on [domain].',
    );
  });
  it('enforces shell and application-root dependency direction', () => {
    assert.deepEqual(
      check(
        "import '../../../../configuration/app.config';",
        'src/app/shell/navigation/configuration/providers/' + 'navigation.providers.ts',
      ),
      [],
    );
    const contextApiViolations = check(
      "import '../../../configuration/app.config';",
      'src/app/contexts/daily-record/api/navigation.ts',
    );

    assert.equal(contextApiViolations.length, 1);
    assert.equal(contextApiViolations[0]?.messageId, 'invalidDependency');

    const shellViolations = check(
      "import '../configuration/providers/navigation.providers';",
      'src/app/shell/navigation/page/navigation.page.ts',
    );

    assert.equal(shellViolations.length, 1);
    assert.equal(shellViolations[0]?.messageId, 'invalidDependency');

    const rootViolations = check(
      "import '../composition/app.composition';",
      'src/app/configuration/app.config.ts',
    );

    assert.equal(rootViolations.length, 1);
    assert.equal(rootViolations[0]?.messageId, 'invalidDependency');
  });
  it('leaves cross-module dependencies to module boundaries and skips specifications', () => {
    assert.deepEqual(
      check(
        "import '../../other/domain/value';",
        'src/app/contexts/nutrition/domain/food-entry.ts',
      ),
      [],
    );

    assert.deepEqual(
      check(
        "import '../application/service/service';",
        'src/app/contexts/nutrition/domain/food-entry.spec.ts',
      ),
      [],
    );

    assert.deepEqual(
      check("import '@angular/core';", 'src/app/contexts/nutrition/domain/food-entry.ts'),
      [],
    );
  });
});
