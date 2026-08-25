// @ts-check

'use strict';

const assert = require('node:assert/strict');
const { describe, it } = require('node:test');

const { ESLint } = require('eslint');
const tseslint = require('typescript-eslint');

const { createRule } = require('../create-rule');

/** @type {import('../types').RuleDefinition} */
const definition = {
  messages: {
    observed: '{{filePath}} belongs to {{moduleName}}.',
  },

  check(file, expected) {
    return [
      {
        node: file.node,
        messageId: 'observed',
        data: {
          filePath: file.path.relativePath ?? file.path.value,
          moduleName: expected.module?.name ?? 'none',
        },
      },
    ];
  },
};

describe('createRule', () => {
  it('analyzes, matches and checks a file before reporting violations', async () => {
    const eslint = new ESLint({
      overrideConfigFile: true,
      overrideConfig: [
        {
          files: ['**/*.ts'],
          languageOptions: {
            parser: tseslint.parser,
          },
          plugins: {
            architecture: {
              rules: {
                example: createRule(definition),
              },
            },
          },
          rules: {
            'architecture/example': 'error',
          },
        },
      ],
    });

    const [result] = await eslint.lintText('', {
      filePath: 'src/app/contexts/nutrition/domain/food.ts',
    });

    assert.ok(result);
    assert.equal(result.messages.length, 1);
    assert.deepEqual(
      {
        ruleId: result.messages[0]?.ruleId,
        messageId: result.messages[0]?.messageId,
        message: result.messages[0]?.message,
      },
      {
        ruleId: 'architecture/example',
        messageId: 'observed',
        message: 'contexts/nutrition/domain/food.ts belongs to nutrition.',
      },
    );
  });
});
