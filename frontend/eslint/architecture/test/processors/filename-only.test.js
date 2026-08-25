// @ts-check

'use strict';

const assert = require('node:assert/strict');
const { describe, it } = require('node:test');

const processor = require('../../processors/filename-only');

describe('filename-only processor', () => {
  it('replaces file contents with one empty JavaScript block', () => {
    assert.deepEqual(processor.preprocess('not JavaScript', 'example.scss'), ['']);
  });

  it('flattens the messages returned for that block', () => {
    const messages = processor.postprocess(
      [
        [
          {
            line: 1,
            column: 1,
            message: 'first',
            ruleId: 'first',
            severity: 2,
          },
        ],
        [
          {
            line: 1,
            column: 1,
            message: 'second',
            ruleId: 'second',
            severity: 2,
          },
        ],
      ],
      'example.scss',
    );

    assert.deepEqual(
      messages.map((message) => message.ruleId),
      ['first', 'second'],
    );
  });
});
