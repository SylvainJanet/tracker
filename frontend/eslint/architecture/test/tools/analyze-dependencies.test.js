// @ts-check

'use strict';

const assert = require('node:assert/strict');
const { describe, it } = require('node:test');
const parser = require('@typescript-eslint/parser');

const { analyzeDependencies } = require('../../tools/analyze-dependencies');

/** @param {string} source */
function parseModule(source) {
  return parser.parse(source, { sourceType: 'module' });
}

describe('analyzeDependencies', () => {
  it('collects static import sources', () => {
    const program = parseModule(`
        import { Food } from './food';
        import { Injectable } from '@angular/core';
      `);

    assert.deepEqual(analyzeDependencies(program), [
      { source: './food' },
      { source: '@angular/core' },
    ]);
  });
  it('treats type-only and mixed imports as dependencies', () => {
    const program = parseModule(`
      import type { Food } from './food';
      import {
        type FoodId,
        createFood
      } from './food-service';
    `);

    assert.deepEqual(analyzeDependencies(program), [
      { source: './food' },
      { source: './food-service' },
    ]);
  });
  it('collects re-exports and dynamic imports', () => {
    const program = parseModule(`
      export { Food } from './food';
      export * from './nutrition';

      export async function loadReport() {
        return import('./monthly-report');
      }
    `);

    assert.deepEqual(analyzeDependencies(program), [
      { source: './food' },
      { source: './nutrition' },
      { source: './monthly-report' },
    ]);
  });
  it('collects no-substitution template dynamic imports', () => {
    const program = parseModule('const service = import(`../application/service/example`);');

    assert.deepEqual(analyzeDependencies(program), [{ source: '../application/service/example' }]);
  });
});
