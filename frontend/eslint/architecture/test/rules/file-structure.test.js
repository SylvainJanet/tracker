// @ts-check

'use strict';

const assert = require('node:assert/strict');
const path = require('node:path');
const { describe, it } = require('node:test');

const { ESLint } = require('eslint');
const tseslint = require('typescript-eslint');

const architecture = require('../../index');

const fixtures = path.resolve(__dirname, '../fixtures');

const eslint = new ESLint({
  cwd: fixtures,
  overrideConfigFile: true,
  overrideConfig: [
    {
      plugins: {
        architecture,
      },
      rules: {
        'architecture/file-structure': 'error',
      },
    },
    {
      files: ['**/*.ts'],
      languageOptions: {
        parser: tseslint.parser,
      },
    },
    {
      files: ['**/*.html', '**/*.scss'],
      processor: 'architecture/filename-only',
    },
  ],
});

/** @param {string} filePath */
async function lintFixture(filePath) {
  const [result] = await eslint.lintFiles([filePath]);

  assert.ok(result);

  return result.messages;
}

describe('file structure', () => {
  it('accepts a physical fixture in an allowed folder', async () => {
    const acceptedFixtures = [
      'src/app/contexts/nutrition/domain/valid-file-structure.ts',
      'src/app/contexts/nutrition/adapter/in/web/search/page/nutrition.search.page.html',
      'src/app/contexts/nutrition/adapter/in/web/search/page/nutrition.search.page.scss',
    ];

    for (const filePath of acceptedFixtures) {
      await it(`accepts ${filePath}`, async () => {
        assert.deepEqual(await lintFixture(filePath), []);
      });
    }
  });

  it('explains why a physical fixture is rejected', async () => {
    const [message] = await lintFixture('src/app/components/invalid-file-structure.ts');

    assert.ok(message);
    assert.equal(message.ruleId, 'architecture/file-structure');
    assert.equal(message.messageId, 'invalidStructure');
    assert.equal(
      message.message,
      'src/app/components/invalid-file-structure.ts does not follow the allowed frontend folder structure: ' +
        'the folder src/app does not allow direct files; ' +
        'allowed folders are [configuration, contexts, shared, shell, composition].',
    );
  });
  it('ignores Angular inline-template virtual files', async () => {
    const [result] = await eslint.lintText('', {
      filePath:
        'src/app/shell/app/page/app.page.ts/' + '1_inline-template-app.page.ts-1.component.html',
    });

    assert.ok(result);
    assert.deepEqual(result.messages, []);
  });
});
