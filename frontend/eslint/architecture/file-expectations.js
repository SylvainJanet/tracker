// @ts-check

'use strict';

const declarations = require('./declaration-expectations');

const TYPESCRIPT_EXTENSIONS = ['.ts', '.spec.ts'];
const PAGE_EXTENSIONS = [...TYPESCRIPT_EXTENSIONS, '.scss', '.html'];
const API_EXTENSIONS = ['.ts'];

/**
 * @param {string[]} stems
 * @param {string[]} [extensions]
 * @param {import('./types').DeclarationExpectation} [expectedDeclarations]
 * @returns {import('./types').ExpectedFile}
 */
function expectedFile(stems, extensions = TYPESCRIPT_EXTENSIONS, expectedDeclarations) {
  const file = {
    naming: {
      stems,
      extensions,
    },
  };

  return expectedDeclarations === undefined
    ? file
    : { ...file, declarations: expectedDeclarations };
}

/**
 * @param {string[]} stems
 * @param {import('./types').ReExportExpectation} reExports
 * @returns {import('./types').ExpectedFile}
 */
function reExportFile(stems, reExports) {
  return {
    ...expectedFile(stems, API_EXTENSIONS),
    reExports,
  };
}

const fileExpectations = {
  web: {
    exactModel: [expectedFile(['<scope>.model'], TYPESCRIPT_EXTENSIONS, declarations.exactModel)],
    sharedModel: [expectedFile(['<scope>.model'], TYPESCRIPT_EXTENSIONS, declarations.sharedModel)],
    page: [expectedFile(['<scope>.page'], PAGE_EXTENSIONS, declarations.singleClass)],
    presenter: [expectedFile(['<scope>.presenter'], TYPESCRIPT_EXTENSIONS, declarations.presenter)],
    mapper: [expectedFile(['<scope>.mapper'], TYPESCRIPT_EXTENSIONS, declarations.singleClass)],
  },

  boundedModule: {
    domain: [expectedFile(['<responsibility>'], TYPESCRIPT_EXTENSIONS, declarations.domain)],
    inboundPort: [
      expectedFile(['<responsibility>.use-case'], TYPESCRIPT_EXTENSIONS, declarations.inboundPort),
    ],
    outboundPort: [
      expectedFile(['<responsibility>.gateway'], TYPESCRIPT_EXTENSIONS, declarations.outboundPort),
      expectedFile(['<responsibility>.provider'], TYPESCRIPT_EXTENSIONS, declarations.outboundPort),
    ],
    service: [
      expectedFile(['<responsibility>.service'], TYPESCRIPT_EXTENSIONS, declarations.service),
    ],
    httpGateway: [
      expectedFile(
        ['<implementation>-<responsibility>.gateway'],
        TYPESCRIPT_EXTENSIONS,
        declarations.outboundAdapter,
      ),
    ],
    httpError: [
      expectedFile(
        ['<scope>-<responsibility>.error'],
        TYPESCRIPT_EXTENSIONS,
        declarations.singleClass,
      ),
    ],
    httpErrorTranslator: [
      expectedFile(
        ['<scope>-http-error.translator'],
        TYPESCRIPT_EXTENSIONS,
        declarations.httpErrorTranslator,
      ),
    ],
    timeProvider: [
      expectedFile(
        ['<implementation>-<responsibility>.provider'],
        TYPESCRIPT_EXTENSIONS,
        declarations.outboundAdapter,
      ),
    ],
  },

  context: {
    api: [
      reExportFile(['navigation'], {
        statements: 'one',
        source: {
          exact: '../configuration/navigation/<scope>.navigation',
        },
        names: ['<UpperSnakeScope>_NAVIGATION'],
      }),
      reExportFile(['routes'], {
        statements: 'one',
        source: {
          exact: '../configuration/routes/<scope>.routes',
        },
        names: ['<UpperSnakeScope>_ROUTES'],
      }),
    ],
    httpContracts: [
      expectedFile(
        ['<scope>-http-contract', '<scope>-<responsibility>-http-contract'],
        TYPESCRIPT_EXTENSIONS,
        declarations.contextHttpContract,
      ),
      expectedFile(
        ['<scope>-http-contract.mapper', '<scope>-<responsibility>-http-contract.mapper'],
        TYPESCRIPT_EXTENSIONS,
        declarations.singleClass,
      ),
    ],
    configuration: {
      navigation: [
        expectedFile(['<scope>.navigation'], TYPESCRIPT_EXTENSIONS, declarations.contextNavigation),
      ],
      providers: [
        expectedFile(['<scope>.providers'], TYPESCRIPT_EXTENSIONS, declarations.contextProvider),
      ],
      routes: [expectedFile(['<scope>.routes'], TYPESCRIPT_EXTENSIONS, declarations.contextRoutes)],
    },
  },

  shared: {
    api: [
      reExportFile(['<scope>.<responsibility>'], {
        statements: 'one-or-more',
        source: {
          prefix: '../',
        },
        names: 'any',
      }),
    ],
    httpContracts: [
      expectedFile(
        ['<scope>.<responsibility>.dto'],
        TYPESCRIPT_EXTENSIONS,
        declarations.sharedHttpDto,
      ),
    ],
    configuration: {
      navigation: [
        expectedFile(
          ['<scope>.configuration.navigation'],
          TYPESCRIPT_EXTENSIONS,
          declarations.sharedNavigation,
        ),
      ],
      providers: [expectedFile(['<scope>.configuration.providers'])],
      routes: [expectedFile(['<scope>.configuration.routes'])],
    },
  },

  shell: {
    api: [
      reExportFile(['page'], {
        statements: 'one',
        source: {
          exact: '../page/<scope>.page',
        },
        names: ['<PascalScope>Page'],
      }),
      reExportFile(['providers'], {
        statements: 'one',
        source: {
          exact: '../configuration/providers/<scope>.providers',
        },
        names: 'any',
      }),
    ],
    configuration: {
      providers: [
        expectedFile(['<scope>.providers'], TYPESCRIPT_EXTENSIONS, declarations.shellProvider),
      ],
      routes: [expectedFile(['<scope>.routes'], TYPESCRIPT_EXTENSIONS, declarations.shellRoutes)],
      sections: [
        expectedFile(['<scope>.sections'], TYPESCRIPT_EXTENSIONS, declarations.shellSections),
      ],
    },
  },

  application: {
    configuration: [
      expectedFile(['app.config'], TYPESCRIPT_EXTENSIONS, declarations.applicationConfig),
      expectedFile(
        ['contexts.config'],
        TYPESCRIPT_EXTENSIONS,
        declarations.applicationContextsConfig,
      ),
    ],
    composition: [expectedFile(['<responsibility>.composition'])],
  },
};

module.exports = fileExpectations;
