// @ts-check

'use strict';

const assert = require('node:assert/strict');
const { describe, it } = require('node:test');

const architecture = require('../architecture');
const { matchExpectedFile } = require('../tools/file-names');
const { analyzePath } = require('../tools/analyze-path');
const { matchArchitecture } = require('../tools/match-architecture');

/** @param {string} filePath */
function expectationFor(filePath) {
  const path = analyzePath(filePath);
  const architectureMatch = matchArchitecture(architecture, path);

  return (
    matchExpectedFile(architectureMatch.folder.files, path.fileName, architectureMatch.namingScope)
      ?.expectedFile ?? null
  );
}

describe('architecture declaration expectations', () => {
  it('requires a page to export exactly its PascalCase filename as a class', () => {
    const expectedFile = expectationFor(
      'src/app/contexts/nutrition/adapter/in/web/search/page/nutrition.search.page.ts',
    );

    assert.ok(expectedFile);
    assert.deepEqual(expectedFile.declarations, {
      requiredExports: [
        {
          name: '<PascalFileStem>',
          types: ['class'],
        },
      ],
    });
  });
  it('reuses the exact single-class expectation at other class boundaries', () => {
    const singleClassFiles = [
      'src/app/contexts/nutrition/adapter/in/web/search/presenter/mapper/nutrition.search.mapper.ts',
      'src/app/contexts/nutrition/adapter/out/http/contract/nutrition-http-contract.mapper.ts',
      'src/app/contexts/nutrition/adapter/out/http/errors/nutrition-unavailable.error.ts',
      'src/app/shell/administration/users/page/administration.users.page.ts',
      'src/app/shell/administration/users/presenter/mapper/administration.users.mapper.ts',
    ];

    for (const filePath of singleClassFiles) {
      assert.deepEqual(expectationFor(filePath)?.declarations, {
        requiredExports: [
          {
            name: '<PascalFileStem>',
            types: ['class'],
          },
        ],
      });
    }
  });
  it('describes a domain declaration and its optional helpers', () => {
    const expectedFile = expectationFor('src/app/contexts/nutrition/domain/food-entry.ts');

    assert.ok(expectedFile);
    assert.deepEqual(expectedFile.declarations, {
      requiredExports: [
        {
          name: '<PascalFileStem>',
          types: ['interface', 'type', 'class'],
        },
      ],
      optionalExports: [
        {
          name: '<LowerCamelFileStem>',
          types: ['function'],
        },
        {
          name: 'is<PascalFileStem>',
          types: ['function'],
        },
        {
          name: 'parse<PascalFileStem>',
          types: ['function'],
        },
      ],
    });
  });
  it('requires an application service to implement its matching use case', () => {
    const expectedFile = expectationFor(
      'src/app/contexts/nutrition/application/service/create-food-entry.service.ts',
    );

    assert.ok(expectedFile);
    assert.deepEqual(expectedFile.declarations, {
      requiredExports: [
        {
          name: '<PascalFileStem>',
          types: ['class'],
          implements: ['<PascalResponsibility>UseCase'],
        },
      ],
    });
  });
  it('describes inbound port declarations and data contracts', () => {
    assert.deepEqual(
      expectationFor(
        'src/app/contexts/nutrition/application/port/in/' + 'create-food-entry.use-case.ts',
      )?.declarations,
      {
        requiredExports: [
          {
            name: '<PascalResponsibility>Result',
            types: ['interface', 'type'],
            requiredProperties: ['resultData'],
          },
          {
            name: '<PascalImplementation><PascalResponsibility><PascalSegment>',
            types: ['interface'],
          },
        ],
        optionalExports: [
          {
            name: '<PascalResponsibility>Command',
            types: ['interface', 'type'],
          },
          {
            name: '<PascalResponsibility>Query',
            types: ['interface', 'type'],
          },
        ],
        maximumOptionalExports: 1,
      },
    );
  });

  it('requires each outbound port to export its matching interface', () => {
    const portFiles = [
      'src/app/contexts/nutrition/application/port/out/food-entry.gateway.ts',
      'src/app/contexts/nutrition/application/port/out/today.provider.ts',
    ];

    for (const filePath of portFiles) {
      assert.deepEqual(expectationFor(filePath)?.declarations, {
        requiredExports: [
          {
            name: '<PascalImplementation><PascalResponsibility><PascalSegment>',
            types: ['interface'],
          },
        ],
        optionalExports: [
          {
            name: '<PascalName>Criteria',
            types: ['interface', 'type'],
          },
          {
            name: '<PascalName>Instruction',
            types: ['interface', 'type'],
          },
          {
            name: '<PascalName>Outcome',
            types: ['interface', 'type'],
            requiredProperties: ['outcomeData'],
          },
        ],
      });
    }
  });

  it('requires each outbound adapter to export a matching class implementing its port', () => {
    const adapterFiles = [
      'src/app/contexts/nutrition/adapter/out/http/http-food-entry.gateway.ts',
      'src/app/contexts/nutrition/adapter/out/time/browser-today.provider.ts',
    ];

    for (const filePath of adapterFiles) {
      assert.deepEqual(expectationFor(filePath)?.declarations, {
        requiredExports: [
          {
            name: '<PascalImplementation><PascalResponsibility><PascalSegment>',
            types: ['class'],
            implements: ['<PascalResponsibility><PascalSegment>'],
          },
        ],
      });
    }
  });
  it('describes the exact application configuration exports', () => {
    assert.deepEqual(expectationFor('src/app/configuration/app.config.ts')?.declarations, {
      requiredExports: [
        {
          name: 'appConfig',
          types: ['const'],
        },
      ],
    });

    assert.deepEqual(expectationFor('src/app/configuration/contexts.config.ts')?.declarations, {
      requiredExports: [
        {
          name: 'APPLICATION_CONTEXTS',
          types: ['const'],
        },
        {
          name: 'DEFAULT_APPLICATION_CONTEXT',
          types: ['const'],
        },
      ],
    });
  });
  it('describes context navigation and route exports from their scope', () => {
    assert.deepEqual(
      expectationFor('src/app/contexts/nutrition/configuration/navigation/nutrition.navigation.ts')
        ?.declarations,
      {
        requiredExports: [
          {
            name: '<UpperSnakeScope>_PATHS',
            types: ['const'],
          },
          {
            name: '<UpperSnakeScope>_NAVIGATION',
            types: ['const'],
          },
        ],
      },
    );

    assert.deepEqual(
      expectationFor(
        'src/app/contexts/nutrition/reporting/configuration/routes/' +
          'nutrition.reporting.routes.ts',
      )?.declarations,
      {
        requiredExports: [
          {
            name: '<UpperSnakeScope>_ROUTES',
            types: ['const'],
          },
        ],
      },
    );
  });
  it('describes the shared navigation configuration exports', () => {
    assert.deepEqual(
      expectationFor('src/app/shared/configuration/navigation/shared.configuration.navigation.ts')
        ?.declarations,
      {
        requiredExports: [
          {
            name: 'SharedConfigurationNavigation',
            types: ['interface'],
          },
          {
            name: 'defineContextNavigation',
            types: ['function'],
          },
        ],
      },
    );
  });
  it('describes a shared HTTP DTO and its optional guard', () => {
    assert.deepEqual(
      expectationFor(
        'src/app/shared/adapter/out/http/contract/' + 'shared.problem-detail-response.dto.ts',
      )?.declarations,
      {
        requiredExports: [
          {
            name: '<PascalResponsibility><PascalSegment>',
            types: ['interface'],
          },
        ],
        optionalExports: [
          {
            name: 'is<PascalResponsibility><PascalSegment>',
            types: ['function'],
          },
        ],
      },
    );
  });
  it('describes shell route and section exports', () => {
    assert.deepEqual(
      expectationFor('src/app/shell/app/configuration/routes/app.routes.ts')?.declarations,
      {
        requiredExports: [
          {
            name: 'routes',
            types: ['const'],
          },
        ],
      },
    );

    assert.deepEqual(
      expectationFor(
        'src/app/shell/administration/users/configuration/sections/' +
          'administration.users.sections.ts',
      )?.declarations,
      {
        requiredExports: [
          {
            name: '<UpperSnakeScope>_SECTIONS',
            types: ['const'],
          },
        ],
      },
    );
  });
  it('describes presentation model exports', () => {
    const optionalExports = [
      {
        name: '<PascalName>View',
        types: ['interface'],
      },
      {
        name: '<PascalName>ReadModel',
        types: ['type'],
      },
      {
        name: '<PascalName>Section',
        types: ['interface'],
      },
      {
        name: '<PascalName>Destination',
        types: ['interface'],
      },
    ];

    const exactModelFiles = [
      'src/app/contexts/nutrition/adapter/in/web/logging/model/' + 'nutrition.logging.model.ts',
      'src/app/shell/navigation/model/navigation.model.ts',
    ];

    for (const filePath of exactModelFiles) {
      assert.deepEqual(expectationFor(filePath)?.declarations, {
        requiredExports: [
          {
            name: '<PascalFileStem>',
            types: ['class'],
          },
        ],
        optionalExports,
      });
    }

    assert.deepEqual(
      expectationFor(
        'src/app/shared/adapter/in/web/navigation/model/' + 'shared.navigation.model.ts',
      )?.declarations,
      {
        requiredExports: [
          {
            name: '<PascalName><PascalLocalScope>Model',
            types: ['class'],
          },
        ],
        optionalExports,
      },
    );
  });
  it('describes allowed context HTTP contract declarations', () => {
    assert.deepEqual(
      expectationFor(
        'src/app/contexts/daily-record/adapter/out/http/contract/' +
          'daily-record-http-contract.ts',
      )?.declarations,
      {
        minimumExports: 1,
        requiredExports: [],
        optionalExports: [
          {
            name: '<PascalScope>Url',
            types: ['class'],
          },
          {
            name: '<PascalResponsibility>Url',
            types: ['class'],
          },
          {
            name: '<OptionalPascalName><PascalScope>RequestParameters',
            types: ['interface'],
          },
          {
            name: '<OptionalPascalName><PascalScope>RequestBodyDto',
            types: ['interface'],
          },
          {
            name: '<OptionalPascalName><PascalScope>ResponseDto',
            types: ['interface'],
          },
          {
            name: '<OptionalPascalName><PascalResponsibility>RequestParameters',
            types: ['interface'],
          },
          {
            name: '<OptionalPascalName><PascalResponsibility>RequestBodyDto',
            types: ['interface'],
          },
          {
            name: '<OptionalPascalName><PascalResponsibility>ResponseDto',
            types: ['interface'],
          },
        ],
      },
    );
  });
  it('describes HTTP error translator functions', () => {
    assert.deepEqual(
      expectationFor(
        'src/app/contexts/daily-record/adapter/out/http/errors/translator/' +
          'daily-record-http-error.translator.ts',
      )?.declarations,
      {
        minimumExports: 1,
        requiredExports: [],
        optionalExports: [
          {
            name: 'translate<OptionalPascalName><PascalScope>HttpError',
            types: ['function'],
          },
          {
            name: 'translate<OptionalPascalName><PascalResponsibility>HttpError',
            types: ['function'],
          },
        ],
      },
    );
  });
  it('describes allowed shell provider exports', () => {
    const expectedDeclarations = {
      minimumExports: 1,
      requiredExports: [],
      optionalExports: [
        {
          name: '<LowerCamelScope>Providers',
          types: ['const'],
        },
        {
          name: 'provideApplication<PascalScope>',
          types: ['function'],
        },
      ],
    };

    assert.deepEqual(
      expectationFor('src/app/shell/app/configuration/providers/app.providers.ts')?.declarations,
      expectedDeclarations,
    );

    assert.deepEqual(
      expectationFor(
        'src/app/shell/administration/users/configuration/providers/' +
          'administration.users.providers.ts',
      )?.declarations,
      expectedDeclarations,
    );
  });
  it('describes allowed context provider exports', () => {
    assert.deepEqual(
      expectationFor(
        'src/app/contexts/daily-record/configuration/providers/' + 'daily-record.providers.ts',
      )?.declarations,
      {
        minimumExports: 1,
        requiredExports: [],
        optionalExports: [
          {
            name: '<UpperSnakeNameContainingScope>_USE_CASE',
            types: ['const'],
          },
          {
            name: 'provide<PascalScope><OptionalPascalName>Context',
            types: ['function'],
          },
          {
            name: 'provide<PascalScope><OptionalPascalName>Presenter',
            types: ['function'],
          },
        ],
      },
    );
  });
  it('describes an exact context public API re-export', () => {
    assert.deepEqual(
      expectationFor('src/app/contexts/daily-record/reporting/api/routes.ts')?.reExports,
      {
        statements: 'one',
        source: {
          exact: '../configuration/routes/<scope>.routes',
        },
        names: ['<UpperSnakeScope>_ROUTES'],
      },
    );
  });
  it('describes shell public API re-exports', () => {
    assert.deepEqual(expectationFor('src/app/shell/administration/users/api/page.ts')?.reExports, {
      statements: 'one',
      source: {
        exact: '../page/<scope>.page',
      },
      names: ['<PascalScope>Page'],
    });

    assert.deepEqual(
      expectationFor('src/app/shell/administration/users/api/providers.ts')?.reExports,
      {
        statements: 'one',
        source: {
          exact: '../configuration/providers/<scope>.providers',
        },
        names: 'any',
      },
    );
  });

  it('describes shared public API re-exports', () => {
    assert.deepEqual(expectationFor('src/app/shared/api/shared.context-navigation.ts')?.reExports, {
      statements: 'one-or-more',
      source: {
        prefix: '../',
      },
      names: 'any',
    });
  });
  it('describes presenter, factory, and token exports', () => {
    const presenterFiles = [
      'src/app/contexts/nutrition/adapter/in/web/logging/presenter/nutrition.logging.presenter.ts',
      'src/app/shared/adapter/in/web/navigation/presenter/shared.navigation.presenter.ts',
      'src/app/shell/administration/users/presenter/administration.users.presenter.ts',
    ];

    for (const filePath of presenterFiles) {
      assert.deepEqual(expectationFor(filePath)?.declarations, {
        requiredExports: [
          {
            name: '<PascalFileStem>',
            types: ['class'],
          },
          {
            name: '<OptionalPascalName><PascalFileStem>Factory',
            types: ['type'],
            functionType: {
              parameterCount: 0,
              returnType: '<PascalFileStem>',
            },
          },
        ],
        derivedExports: [
          {
            nameFrom: '<OptionalPascalName><PascalFileStem>Factory',
            transform: 'upper-snake',
            types: ['const'],
          },
        ],
      });
    }
  });
});
