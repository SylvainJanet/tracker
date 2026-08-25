// @ts-check

'use strict';

const assert = require('node:assert/strict');
const { describe, it } = require('node:test');
const parser = require('@typescript-eslint/parser');
const path = require('node:path');
const { ESLint } = require('eslint');
const tseslint = require('typescript-eslint');

const architecturePlugin = require('../../index');

const architecture = require('../../architecture');
const { analyzeFile } = require('../../tools/analyze-file');
const { matchArchitecture } = require('../../tools/match-architecture');
const rule = require('../../rules/module-boundaries');

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
        'architecture/module-boundaries': 'error',
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

describe('module boundaries', () => {
  const filePath = 'src/app/contexts/nutrition/domain/food-entry.ts';

  it('rejects another context public API from the domain', () => {
    const violations = check("import '../../daily-record/api/routes';", filePath);

    assert.equal(violations.length, 1);
    assert.deepEqual(
      {
        messageId: violations[0]?.messageId,
        data: violations[0]?.data,
      },
      {
        messageId: 'forbiddenContextDependency',
        data: {
          filePath,
          dependencyPath: 'src/app/contexts/daily-record/api/routes',
          sourceLayer: 'domain',
        },
      },
    );
  });

  it('accepts another context public API from an outbound adapter', () => {
    assert.deepEqual(
      check(
        "import '../../../../daily-record/api/routes';",
        'src/app/contexts/nutrition/adapter/out/http/daily-record.gateway.ts',
      ),
      [],
    );
  });

  it('rejects a deep import into another module', () => {
    const violations = check("import '../../daily-record/domain/daily-record';", filePath);

    assert.equal(violations.length, 1);
    assert.deepEqual(
      {
        messageId: violations[0]?.messageId,
        data: violations[0]?.data,
      },
      {
        messageId: 'internalModuleDependency',
        data: {
          filePath,
          dependencyPath: 'src/app/contexts/daily-record/domain/daily-record',
          targetModule: 'src/app/contexts/daily-record',
        },
      },
    );
  });
  it('validates explicit physical fixtures', async () => {
    assert.deepEqual(
      await lintFixture(
        'src/app/contexts/nutrition/adapter/out/http/' + 'valid-module-boundaries.gateway.ts',
      ),
      [],
    );

    const filePath = 'src/app/contexts/nutrition/domain/invalid-module-boundaries.ts';
    const [message] = await lintFixture(filePath);

    assert.equal(message?.ruleId, 'architecture/module-boundaries');
    assert.equal(message.messageId, 'internalModuleDependency');
    assert.equal(
      message.message,
      `${filePath} cannot depend directly on ` +
        'src/app/contexts/daily-record/domain/daily-record: ' +
        'module src/app/contexts/daily-record must be accessed through its api folder.',
    );
  });
  it('rejects a deep import from an outbound adapter into another context', () => {
    const adapterPath = 'src/app/contexts/nutrition/adapter/out/http/daily-record.gateway.ts';

    const violations = check("import '../../../../daily-record/domain/daily-record';", adapterPath);

    assert.equal(violations.length, 1);
    assert.deepEqual(
      {
        messageId: violations[0]?.messageId,
        data: violations[0]?.data,
      },
      {
        messageId: 'internalModuleDependency',
        data: {
          filePath: adapterPath,
          dependencyPath: 'src/app/contexts/daily-record/domain/daily-record',
          targetModule: 'src/app/contexts/daily-record',
        },
      },
    );
  });
  it('allows application assembly to consume context public APIs', () => {
    assert.deepEqual(
      check(
        "import '../contexts/daily-record/api/routes';",
        'src/app/configuration/contexts.config.ts',
      ),
      [],
    );

    assert.deepEqual(
      check(
        "import '../contexts/daily-record/api/routes';",
        'src/app/composition/routes.composition.ts',
      ),
      [],
    );
  });

  it('allows the shell to consume context public APIs', () => {
    assert.deepEqual(
      check(
        "import '../../../../contexts/daily-record/api/routes';",
        'src/app/shell/app/configuration/routes/app.routes.ts',
      ),
      [],
    );
  });
  it('ignores same-context, external, and specification dependencies', () => {
    assert.deepEqual(check("import './ingredient';", filePath), []);

    assert.deepEqual(check("import '@angular/core';", filePath), []);

    assert.deepEqual(
      check(
        "import '../../daily-record/domain/daily-record';",
        'src/app/contexts/nutrition/domain/food-entry.spec.ts',
      ),
      [],
    );
  });

  it('applies module boundaries to re-exports and dynamic imports', () => {
    const reExportViolations = check(
      "export { DailyRecord } from '../../daily-record/domain/daily-record';",
      filePath,
    );

    assert.equal(reExportViolations.length, 1);
    assert.equal(reExportViolations[0]?.messageId, 'internalModuleDependency');

    const dynamicImportViolations = check(
      "const routes = () => import('../../daily-record/api/routes');",
      filePath,
    );

    assert.equal(dynamicImportViolations.length, 1);
    assert.equal(dynamicImportViolations[0]?.messageId, 'forbiddenContextDependency');
  });
});
