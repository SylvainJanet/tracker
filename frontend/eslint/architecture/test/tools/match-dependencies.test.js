// @ts-check

'use strict';

const assert = require('node:assert/strict');
const { describe, it } = require('node:test');

const architecture = require('../../architecture');
const { analyzePath } = require('../../tools/analyze-path');
const { matchDependencies } = require('../../tools/match-dependencies');

describe('matchDependencies', () => {
  it('classifies resolved application dependencies', () => {
    const source = analyzePath(
      'src/app/contexts/nutrition/application/service/' + 'create-food-entry.service.ts',
    );

    const matches = matchDependencies(architecture, source, [
      { source: '../../domain/food-entry' },
      { source: '@angular/core' },
    ]);

    assert.equal(matches.length, 1);
    assert.equal(matches[0]?.path.value, 'src/app/contexts/nutrition/domain/food-entry');
    assert.equal(matches[0]?.target.layer, 'domain');
    assert.deepEqual(matches[0]?.target.module, {
      kind: 'context',
      name: 'nutrition',
      root: 'src/app/contexts/nutrition',
      scope: ['nutrition'],
    });
  });
});
