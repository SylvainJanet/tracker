// @ts-check

'use strict';

const assert = require('node:assert/strict');
const { describe, it } = require('node:test');
const parser = require('@typescript-eslint/parser');

const architecture = require('../../architecture');
const rule = require('../../rules/reserved-vocabulary');
const { analyzeFile } = require('../../tools/analyze-file');
const { matchArchitecture } = require('../../tools/match-architecture');

const { ESLint } = require('eslint');
const tseslint = require('typescript-eslint');

const architecturePlugin = require('../../index');
const path = require('node:path');

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
        'architecture/reserved-vocabulary': 'error',
      },
    },
  ],
});

/**
 * @param {string} source
 * @param {string} filePath
 */
async function lint(source, filePath) {
  const [result] = await eslint.lintText(source, { filePath });

  assert.ok(result);

  return result.messages;
}

/** @param {string} filePath */
async function lintFixture(filePath) {
  const [result] = await eslint.lintFiles([filePath]);

  assert.ok(result);

  return result.messages;
}

describe('reserved vocabulary', () => {
  it('accepts reserved vocabulary in an allowed location', () => {
    assert.deepEqual(
      check(
        'interface CreateDailyRecordCommand {}',
        'src/app/contexts/daily-record/application/port/in/' + 'create-daily-record.use-case.ts',
      ),
      [],
    );
  });

  it('rejects reserved vocabulary outside its allowed locations', () => {
    const violations = check(
      'interface DailyRecordView {}',
      'src/app/contexts/daily-record/domain/daily-record.ts',
    );

    assert.equal(violations.length, 1);
    assert.deepEqual(
      {
        messageId: violations[0]?.messageId,
        data: violations[0]?.data,
      },
      {
        messageId: 'reservedVocabulary',
        data: {
          filePath: 'src/app/contexts/daily-record/domain/daily-record.ts',
          declarationName: 'DailyRecordView',
          reservedSuffix: 'View',
        },
      },
    );
  });
  it('reports the public rule with a clear message', async () => {
    const filePath = 'src/app/contexts/daily-record/domain/daily-record.ts';
    const [message] = await lint('interface DailyRecordView {}', filePath);

    assert.equal(message?.ruleId, 'architecture/reserved-vocabulary');
    assert.equal(message.messageId, 'reservedVocabulary');
    assert.equal(
      message.message,
      `${filePath} declares DailyRecordView using the reserved View suffix ` +
        'outside an architectural location that allows it.',
    );
  });
  it('checks nested declarations as well as exported declarations', () => {
    const violations = check(
      `
        function createFixture() {
          class HiddenDailyRecordOutcome {}
        }
      `,
      'src/app/contexts/daily-record/domain/daily-record.ts',
    );

    assert.equal(violations.length, 1);
    assert.equal(violations[0]?.data?.['declarationName'], 'HiddenDailyRecordOutcome');
  });

  it('ignores reserved vocabulary in specification files', () => {
    assert.deepEqual(
      check(
        'interface DailyRecordView {}',
        'src/app/contexts/daily-record/domain/daily-record.spec.ts',
      ),
      [],
    );
  });
  it('validates explicit physical fixtures', async () => {
    assert.deepEqual(
      await lintFixture(
        'src/app/contexts/nutrition/application/port/in/' + 'valid-reserved-vocabulary.use-case.ts',
      ),
      [],
    );

    const filePath = 'src/app/contexts/nutrition/domain/' + 'invalid-reserved-vocabulary.ts';
    const [message] = await lintFixture(filePath);

    assert.equal(message?.ruleId, 'architecture/reserved-vocabulary');
    assert.equal(message.messageId, 'reservedVocabulary');
    assert.equal(
      message.message,
      `${filePath} declares MisplacedResult using the reserved Result suffix ` +
        'outside an architectural location that allows it.',
    );
  });
});
