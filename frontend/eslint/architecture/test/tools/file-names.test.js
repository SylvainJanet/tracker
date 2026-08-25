// @ts-check

'use strict';

const assert = require('node:assert/strict');
const { describe, it } = require('node:test');

const { createFileNameConvention, matchExpectedFile } = require('../../tools/file-names');

describe('createFileNameConvention', () => {
  it('expands an exact scope into every allowed extension', () => {
    const convention = createFileNameConvention(
      {
        stems: ['<scope>.page'],
        extensions: ['.ts', '.spec.ts', '.html', '.scss'],
      },
      ['daily-record', 'logging'],
    );

    assert.deepEqual(convention.allowedFileNames, [
      'daily-record.logging.page.ts',
      'daily-record.logging.page.spec.ts',
      'daily-record.logging.page.html',
      'daily-record.logging.page.scss',
    ]);
    assert.deepEqual(convention.match('daily-record.logging.page.ts'), {
      implementation: null,
      responsibility: null,
      segment: 'page',
    });

    assert.equal(convention.matches('daily-record.logging.page.html'), true);
    assert.equal(convention.matches('daily-record.summary.page.ts'), false);
  });

  it('matches a kebab-case responsibility placeholder', () => {
    const convention = createFileNameConvention(
      {
        stems: ['<scope>-<responsibility>.error'],
        extensions: ['.ts', '.spec.ts'],
      },
      ['daily-record'],
    );

    assert.deepEqual(convention.allowedFileNames, [
      'daily-record-<kebab-case-responsibility>.error.ts',
      'daily-record-<kebab-case-responsibility>.error.spec.ts',
    ]);
    assert.equal(convention.matches('daily-record-unexpected-gateway.error.ts'), true);
    assert.equal(convention.matches('unexpected-gateway.error.ts'), false);
    assert.equal(convention.matches('daily-record-UnexpectedGateway.error.ts'), false);
    assert.equal(convention.matches('daily-record-unexpected.gateway.error.ts'), false);
  });

  it('supports several exact stems', () => {
    const convention = createFileNameConvention(
      {
        stems: ['navigation', 'routes'],
        extensions: ['.ts'],
      },
      [],
    );

    assert.deepEqual(convention.allowedFileNames, ['navigation.ts', 'routes.ts']);
    assert.equal(convention.matches('routes.ts'), true);
    assert.equal(convention.matches('routes.spec.ts'), false);
  });
  it('returns the expected file together with its filename parts', () => {
    const adapter = {
      naming: {
        stems: ['<implementation>-<responsibility>.gateway'],
        extensions: ['.ts'],
      },
    };

    assert.deepEqual(matchExpectedFile([adapter], 'http-daily-record.gateway.ts', []), {
      expectedFile: adapter,
      name: {
        implementation: 'http',
        responsibility: 'daily-record',
        segment: 'gateway',
      },
    });
    assert.equal(matchExpectedFile([adapter], 'wrong.ts', []), null);
  });

  it('extracts reusable parts from a matching filename', () => {
    const service = createFileNameConvention(
      {
        stems: ['<responsibility>.service'],
        extensions: ['.ts', '.spec.ts'],
      },
      [],
    );
    const adapter = createFileNameConvention(
      {
        stems: ['<implementation>-<responsibility>.gateway'],
        extensions: ['.ts', '.spec.ts'],
      },
      [],
    );

    assert.deepEqual(service.match('create-daily-record.service.ts'), {
      implementation: null,
      responsibility: 'create-daily-record',
      segment: 'service',
    });
    assert.deepEqual(adapter.match('http-daily-record.gateway.ts'), {
      implementation: 'http',
      responsibility: 'daily-record',
      segment: 'gateway',
    });
    assert.equal(adapter.match('http.gateway.ts'), null);
  });
});
