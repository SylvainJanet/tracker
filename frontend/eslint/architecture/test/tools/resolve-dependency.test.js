// @ts-check

'use strict';

const assert = require('node:assert/strict');
const { describe, it } = require('node:test');

const { analyzePath } = require('../../tools/analyze-path');
const { resolveDependency } = require('../../tools/resolve-dependency');

describe('resolveDependency', () => {
  it('resolves a relative dependency from its source file', () => {
    const file = analyzePath(
      'src/app/contexts/nutrition/application/service/' + 'create-food-entry.service.ts',
    );

    const resolved = resolveDependency(file, {
      source: '../../domain/food-entry',
    });

    assert.equal(resolved?.value, 'src/app/contexts/nutrition/domain/food-entry');
    assert.equal(resolved?.relativePath, 'contexts/nutrition/domain/food-entry');
    assert.deepEqual(resolved?.folders, ['contexts', 'nutrition', 'domain']);
  });

  it('leaves external packages unclassified', () => {
    const file = analyzePath('src/app/contexts/nutrition/domain/food-entry.ts');

    assert.equal(resolveDependency(file, { source: '@angular/core' }), null);
  });
});
