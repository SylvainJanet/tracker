// @ts-check

'use strict';

const assert = require('node:assert/strict');
const { describe, it } = require('node:test');
const parser = require('@typescript-eslint/parser');

const { analyzeFile } = require('../../tools/analyze-file');

describe('analyzeFile', () => {
  it('assembles one analyzed file from its path and program', () => {
    const program = parser.parse(
      `
      import { Ingredient } from './ingredient';
      export interface Food {}
    `,
      { sourceType: 'module' },
    );

    const result = analyzeFile('src/app/contexts/nutrition/domain/food.ts', program);

    assert.deepEqual(result, {
      node: program,
      path: {
        value: 'src/app/contexts/nutrition/domain/food.ts',
        relativePath: 'contexts/nutrition/domain/food.ts',
        folders: ['contexts', 'nutrition', 'domain'],
        fileName: 'food.ts',
        stem: 'food',
        extension: '.ts',
        nameParts: ['food'],
        isTest: false,
        isAngularInlineTemplate: false,
      },
      declarations: {
        exported: [{ name: 'Food', type: 'interface' }],
        reExports: [],
        unsupportedExports: false,
      },
      declaredNames: ['Food'],
      dependencies: [{ source: './ingredient' }],
    });
  });
});
