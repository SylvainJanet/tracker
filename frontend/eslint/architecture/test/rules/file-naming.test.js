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
        'architecture/file-naming': 'error',
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

describe('file naming', () => {
  const acceptedFixtures = [
    'src/app/contexts/nutrition/application/service/valid-file-naming.service.ts',
    'src/app/contexts/nutrition/adapter/in/web/search/page/nutrition.search.page.html',
    'src/app/contexts/nutrition/adapter/in/web/search/page/nutrition.search.page.scss',
  ];

  for (const filePath of acceptedFixtures) {
    it(`accepts ${filePath}`, async () => {
      assert.deepEqual(await lintFixture(filePath), []);
    });
  }

  it('explains why a physical fixture is rejected', async () => {
    const [message] = await lintFixture(
      'src/app/contexts/nutrition/application/service/invalid-file-naming.ts',
    );

    assert.ok(message);
    assert.equal(message.ruleId, 'architecture/file-naming');
    assert.equal(message.messageId, 'invalidFileName');
    assert.equal(
      message.message,
      'src/app/contexts/nutrition/application/service/invalid-file-naming.ts ' +
        'does not follow the allowed frontend filename conventions: ' +
        'files in src/app/contexts/nutrition/application/service must be named one of ' +
        '[<kebab-case-responsibility>.service.ts, ' +
        '<kebab-case-responsibility>.service.spec.ts].',
    );
  });
});
