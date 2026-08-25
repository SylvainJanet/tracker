// @ts-check

'use strict';

const assert = require('node:assert/strict');
const { describe, it } = require('node:test');

const { analyzePath } = require('../../tools/analyze-path');
const { matchArchitecture } = require('../../tools/match-architecture');

/** @type {import('../../types').ArchitectureFolder} */
const domain = {
  files: [
    {
      naming: {
        stems: ['<responsibility>'],
        extensions: ['.ts'],
      },
    },
  ],
  metadata: { layer: 'domain' },
};

/** @type {import('../../types').ArchitectureFolder} */
const boundedModule = {
  files: [],
  folders: {
    domain,
  },
  anyFolder: {
    label: '<subcontext>',
    folder: 'same',
    metadata: { moduleSegment: true },
  },
};

/** @type {import('../../types').ArchitectureFolder} */
const architecture = {
  files: [],
  metadata: {
    moduleKind: 'application',
  },
  folders: {
    configuration: {
      files: [
        {
          naming: {
            stems: ['app.config'],
            extensions: ['.ts'],
          },
        },
      ],
      metadata: { layer: 'configuration' },
    },
    contexts: {
      files: [],
      anyFolder: {
        label: '<context>',
        folder: boundedModule,
        metadata: {
          moduleKind: 'context',
          moduleSegment: true,
        },
      },
    },
  },
};

describe('matchArchitecture', () => {
  it('matches a fixed folder', () => {
    const result = matchArchitecture(
      architecture,
      analyzePath('src/app/configuration/app.config.ts'),
    );

    assert.equal(result.folder, architecture.folders?.['configuration']);
    assert.deepEqual(result.module, {
      kind: 'application',
      name: 'application',
      root: 'src/app',
      scope: [],
    });
    assert.equal(result.layer, 'configuration');
    assert.equal(result.violation, null);
  });

  it('matches dynamic contexts and nested subcontexts', () => {
    const result = matchArchitecture(
      architecture,
      analyzePath('src/app/contexts/nutrition/reporting/domain/monthly-report.ts'),
    );

    assert.equal(result.folder, domain);
    assert.deepEqual(result.module, {
      kind: 'context',
      name: 'reporting',
      root: 'src/app/contexts/nutrition/reporting',
      scope: ['nutrition', 'reporting'],
    });
    assert.equal(result.layer, 'domain');
    assert.equal(result.violation, null);
  });

  it('rejects a file directly inside a folder that forbids files', () => {
    const result = matchArchitecture(architecture, analyzePath('src/app/contexts/nutrition.ts'));

    assert.equal(result.folder, architecture.folders?.['contexts']);
    assert.deepEqual(result.violation, {
      directoryPath: 'src/app/contexts',
      allowsFiles: false,
      allowedFolders: ['<context>'],
    });
  });

  it('rejects an unknown child while preserving the deepest match', () => {
    const configuration = architecture.folders?.['configuration'];
    const result = matchArchitecture(
      architecture,
      analyzePath('src/app/configuration/extra/app.config.ts'),
    );

    assert.equal(result.folder, configuration);
    assert.deepEqual(result.violation, {
      directoryPath: 'src/app/configuration',
      allowsFiles: true,
      allowedFolders: [],
    });
  });
});
