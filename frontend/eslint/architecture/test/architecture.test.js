// @ts-check

'use strict';

const assert = require('node:assert/strict');
const { describe, it } = require('node:test');

const architecture = require('../architecture');
const { analyzePath } = require('../tools/analyze-path');
const { matchArchitecture } = require('../tools/match-architecture');

/** @param {string} filePath */
function match(filePath) {
  return matchArchitecture(architecture, analyzePath(filePath));
}

describe('project architecture', () => {
  const acceptedPaths = [
    // Context modules
    'src/app/contexts/daily-record/api/navigation.ts',
    'src/app/contexts/daily-record/domain/calendar-date.ts',
    'src/app/contexts/daily-record/reporting/history/domain/calendar-date.ts',
    'src/app/contexts/daily-record/application/port/in/create-daily-record.use-case.ts',
    'src/app/contexts/daily-record/application/port/out/daily-record.gateway.ts',
    'src/app/contexts/daily-record/application/service/create-daily-record.service.ts',
    'src/app/contexts/daily-record/adapter/in/web/logging/model/daily-record.logging.model.ts',
    'src/app/contexts/daily-record/adapter/in/web/logging/page/daily-record.logging.page.ts',
    'src/app/contexts/daily-record/adapter/in/web/logging/presenter/daily-record.logging.presenter.ts',
    'src/app/contexts/daily-record/adapter/in/web/logging/presenter/mapper/daily-record.logging.mapper.ts',
    'src/app/contexts/daily-record/adapter/in/web/reports/monthly/page/daily-record.reports.monthly.page.ts',
    'src/app/contexts/daily-record/adapter/out/http/http-daily-record.gateway.ts',
    'src/app/contexts/daily-record/adapter/out/http/contract/daily-record-http-contract.ts',
    'src/app/contexts/daily-record/adapter/out/http/errors/daily-record-unexpected-gateway.error.ts',
    'src/app/contexts/daily-record/adapter/out/http/errors/translator/daily-record-http-error.translator.ts',
    'src/app/contexts/daily-record/adapter/out/time/browser-today.provider.ts',
    'src/app/contexts/daily-record/configuration/daily-record.navigation.ts',
    'src/app/contexts/daily-record/configuration/daily-record.providers.ts',
    'src/app/contexts/daily-record/configuration/daily-record.routes.ts',

    // Shared modules
    'src/app/shared/api/shared.context-navigation.ts',
    'src/app/shared/domain/shared-value.ts',
    'src/app/shared/reporting/history/domain/shared-value.ts',
    'src/app/shared/application/port/in/get-shared-value.use-case.ts',
    'src/app/shared/application/port/out/shared-value.gateway.ts',
    'src/app/shared/application/service/get-shared-value.service.ts',
    'src/app/shared/adapter/in/web/navigation/model/shared.navigation.model.ts',
    'src/app/shared/adapter/in/web/navigation/page/shared.navigation.page.ts',
    'src/app/shared/adapter/in/web/navigation/presenter/shared.navigation.presenter.ts',
    'src/app/shared/adapter/in/web/navigation/presenter/mapper/shared.navigation.mapper.ts',
    'src/app/shared/adapter/out/http/shared-http.gateway.ts',
    'src/app/shared/adapter/out/http/contract/shared.problem-detail-response.dto.ts',
    'src/app/shared/adapter/out/http/errors/shared-http.error.ts',
    'src/app/shared/adapter/out/http/errors/translator/shared-http-error.translator.ts',
    'src/app/shared/adapter/out/time/browser-today.provider.ts',
    'src/app/shared/configuration/shared.configuration.navigation.ts',
    'src/app/shared/configuration/shared.providers.ts',
    'src/app/shared/configuration/shared.routes.ts',

    // Shell modules
    'src/app/shell/app/api/page.ts',
    'src/app/shell/app/api/providers.ts',
    'src/app/shell/app/configuration/app.providers.ts',
    'src/app/shell/app/configuration/app.routes.ts',
    'src/app/shell/app/page/app.page.ts',
    'src/app/shell/navigation/api/page.ts',
    'src/app/shell/navigation/api/providers.ts',
    'src/app/shell/navigation/configuration/navigation.providers.ts',
    'src/app/shell/navigation/configuration/navigation.sections.ts',
    'src/app/shell/navigation/model/navigation.model.ts',
    'src/app/shell/navigation/page/navigation.page.ts',
    'src/app/shell/navigation/presenter/navigation.presenter.ts',
    'src/app/shell/navigation/presenter/mapper/navigation.mapper.ts',
    'src/app/shell/administration/users/page/users.page.ts',

    // Application root
    'src/app/configuration/app.config.ts',
    'src/app/configuration/contexts.config.ts',
    'src/app/composition/app.composition.ts',
  ];

  for (const filePath of acceptedPaths) {
    it(`accepts ${filePath}`, () => {
      assert.equal(match(filePath).violation, null);
    });
  }

  const rejectedPaths = [
    // Context modules
    'src/app/contexts/daily-record/daily-record.ts',
    'src/app/contexts/daily-record/reporting/report.ts',
    'src/app/contexts/daily-record/domain/calendar/calendar-date.ts',
    'src/app/contexts/daily-record/domain/application/calendar-date.ts',
    'src/app/contexts/daily-record/api/public/navigation.ts',
    'src/app/contexts/daily-record/application/create-daily-record.service.ts',
    'src/app/contexts/daily-record/application/use-case/create-daily-record.use-case.ts',
    'src/app/contexts/daily-record/application/port/create-daily-record.use-case.ts',
    'src/app/contexts/daily-record/application/port/in/dtos/create-daily-record.command.ts',
    'src/app/contexts/daily-record/adapter/mobile/example.ts',
    'src/app/contexts/daily-record/adapter/in/mobile/example.ts',
    'src/app/contexts/daily-record/adapter/in/web/logging/example.ts',
    'src/app/contexts/daily-record/adapter/in/web/logging/controller/example.ts',
    'src/app/contexts/daily-record/adapter/in/web/page/page/example.page.ts',
    'src/app/contexts/daily-record/adapter/in/web/logging/presenter/translator/example.ts',
    'src/app/contexts/daily-record/adapter/out/persistence/example.ts',
    'src/app/contexts/daily-record/adapter/out/http/repository/example.ts',
    'src/app/contexts/daily-record/adapter/out/http/errors/handler/example.ts',
    'src/app/contexts/daily-record/adapter/out/time/clock/example.ts',
    'src/app/contexts/daily-record/configuration/components/example.ts',

    // Shared modules
    'src/app/shared/shared.ts',
    'src/app/shared/reporting/report.ts',
    'src/app/shared/domain/value/shared-value.ts',
    'src/app/shared/api/public/shared-value.ts',
    'src/app/shared/application/shared-value.service.ts',
    'src/app/shared/application/port/in/dtos/shared-value.query.ts',
    'src/app/shared/adapter/mobile/example.ts',
    'src/app/shared/adapter/in/mobile/example.ts',
    'src/app/shared/adapter/in/web/navigation/example.ts',
    'src/app/shared/adapter/in/web/navigation/controller/example.ts',
    'src/app/shared/adapter/in/web/navigation/presenter/translator/example.ts',
    'src/app/shared/adapter/out/persistence/example.ts',
    'src/app/shared/adapter/out/http/repository/example.ts',
    'src/app/shared/adapter/out/http/errors/handler/example.ts',
    'src/app/shared/adapter/out/time/clock/example.ts',
    'src/app/shared/configuration/components/example.ts',

    // Shell modules
    'src/app/shell/shell.ts',
    'src/app/shell/app/app.ts',
    'src/app/shell/page/app.page.ts',
    'src/app/shell/page/page/app.page.ts',
    'src/app/shell/app/api/public/page.ts',
    'src/app/shell/app/configuration/navigation/app.navigation.ts',
    'src/app/shell/app/configuration/components/example.ts',
    'src/app/shell/app/model/details/app.model.ts',
    'src/app/shell/app/page/components/app.page.ts',
    'src/app/shell/app/presenter/translator/example.ts',
    'src/app/shell/app/domain/example.ts',
    'src/app/shell/app/application/service/example.ts',
    'src/app/shell/app/adapter/in/web/example/page/example.page.ts',

    // Application root
    'src/app/app.ts',
    'src/app/contexts.ts',
    'src/app/shared.ts',
    'src/app/shell.ts',
    'src/app/configuration.ts',
    'src/app/composition.ts',
    'src/app/components/example.ts',
    'src/app/domain/example.ts',
    'src/app/adapter/in/web/example/page/example.page.ts',
    'src/app/daily-record/domain/daily-record.ts',
    'src/app/configuration/providers/app.providers.ts',
    'src/app/configuration/routes/app.routes.ts',
    'src/app/composition/providers/app.providers.ts',
    'src/app/composition/contexts/example.ts',
  ];

  for (const filePath of rejectedPaths) {
    it(`rejects ${filePath}`, () => {
      assert.notEqual(match(filePath).violation, null);
    });
  }

  const violationScenarios = [
    {
      filePath: 'src/app/components/example.ts',
      directoryPath: 'src/app',
      allowsFiles: false,
      allowedFolders: ['configuration', 'contexts', 'shared', 'shell', 'composition'],
    },
    {
      filePath: 'src/app/configuration/providers/example.ts',
      directoryPath: 'src/app/configuration',
      allowsFiles: true,
      allowedFolders: [],
    },
    {
      filePath: 'src/app/contexts/example.ts',
      directoryPath: 'src/app/contexts',
      allowsFiles: false,
      allowedFolders: ['<context>'],
    },
    {
      filePath: 'src/app/contexts/daily-record/reporting/report.ts',
      directoryPath: 'src/app/contexts/daily-record/reporting',
      allowsFiles: false,
      allowedFolders: ['api', 'domain', 'application', 'adapter', 'configuration', '<subcontext>'],
    },
    {
      filePath: 'src/app/contexts/daily-record/domain/calendar/calendar-date.ts',
      directoryPath: 'src/app/contexts/daily-record/domain',
      allowsFiles: true,
      allowedFolders: [],
    },
    {
      filePath: 'src/app/contexts/daily-record/application/use-case/example.ts',
      directoryPath: 'src/app/contexts/daily-record/application',
      allowsFiles: false,
      allowedFolders: ['port', 'service'],
    },
    {
      filePath: 'src/app/contexts/daily-record/application/port/example.ts',
      directoryPath: 'src/app/contexts/daily-record/application/port',
      allowsFiles: false,
      allowedFolders: ['in', 'out'],
    },
    {
      filePath: 'src/app/contexts/daily-record/adapter/in/mobile/example.ts',
      directoryPath: 'src/app/contexts/daily-record/adapter/in',
      allowsFiles: false,
      allowedFolders: ['web'],
    },
    {
      filePath: 'src/app/contexts/daily-record/adapter/in/web/logging/example.ts',
      directoryPath: 'src/app/contexts/daily-record/adapter/in/web/logging',
      allowsFiles: false,
      allowedFolders: ['model', 'page', 'presenter', '<subcontext>'],
    },
    {
      filePath:
        'src/app/contexts/daily-record/adapter/in/web/logging/presenter/translator/example.ts',
      directoryPath: 'src/app/contexts/daily-record/adapter/in/web/logging/presenter',
      allowsFiles: true,
      allowedFolders: ['mapper'],
    },
    {
      filePath: 'src/app/contexts/daily-record/adapter/out/persistence/example.ts',
      directoryPath: 'src/app/contexts/daily-record/adapter/out',
      allowsFiles: false,
      allowedFolders: ['http', 'time'],
    },
    {
      filePath: 'src/app/contexts/daily-record/adapter/out/http/repository/example.ts',
      directoryPath: 'src/app/contexts/daily-record/adapter/out/http',
      allowsFiles: true,
      allowedFolders: ['contract', 'errors'],
    },
    {
      filePath: 'src/app/contexts/daily-record/adapter/out/http/errors/handler/example.ts',
      directoryPath: 'src/app/contexts/daily-record/adapter/out/http/errors',
      allowsFiles: true,
      allowedFolders: ['translator'],
    },
    {
      filePath: 'src/app/contexts/daily-record/configuration/components/example.ts',
      directoryPath: 'src/app/contexts/daily-record/configuration',
      allowsFiles: true,
      allowedFolders: [],
    },
    {
      filePath: 'src/app/shell/example.ts',
      directoryPath: 'src/app/shell',
      allowsFiles: false,
      allowedFolders: ['<module>'],
    },
    {
      filePath: 'src/app/shell/app/domain/example.ts',
      directoryPath: 'src/app/shell/app',
      allowsFiles: false,
      allowedFolders: ['api', 'configuration', 'model', 'page', 'presenter', '<module>'],
    },
    {
      filePath: 'src/app/shell/app/configuration/navigation/example.ts',
      directoryPath: 'src/app/shell/app/configuration',
      allowsFiles: true,
      allowedFolders: [],
    },
    {
      filePath: 'src/app/shell/app/model/details/example.ts',
      directoryPath: 'src/app/shell/app/model',
      allowsFiles: true,
      allowedFolders: [],
    },
  ];

  for (const { filePath, directoryPath, allowsFiles, allowedFolders } of violationScenarios) {
    it(`explains the violation in ${filePath}`, () => {
      assert.deepEqual(match(filePath).violation, {
        directoryPath,
        allowsFiles,
        allowedFolders,
      });
    });
  }

  it('describes a nested context module and layer', () => {
    const result = match(
      'src/app/contexts/nutrition/reporting/application/service/create-report.service.ts',
    );

    assert.deepEqual(result.module, {
      kind: 'context',
      name: 'reporting',
      root: 'src/app/contexts/nutrition/reporting',
      scope: ['nutrition', 'reporting'],
    });
    assert.equal(result.layer, 'application/service');
  });

  it('marks API folders as public', () => {
    const result = match('src/app/contexts/nutrition/api/routes.ts');

    assert.equal(result.metadata.visibility, 'public-api');
  });

  it('builds the filename scope from module and web subcontext folders', () => {
    const contextPage = match(
      'src/app/contexts/nutrition/reports/adapter/in/web/monthly/summary/page/nutrition.reports.monthly.summary.page.ts',
    );
    const shellPage = match('src/app/shell/administration/users/page/administration.users.page.ts');

    assert.deepEqual(contextPage.namingScope, ['nutrition', 'reports', 'monthly', 'summary']);
    assert.deepEqual(shellPage.namingScope, ['administration', 'users']);
  });
  it('describes where reserved declaration suffixes are allowed', () => {
    const reserved = [
      'Intent',
      'View',
      'Command',
      'Query',
      'Result',
      'Criteria',
      'Instruction',
      'Outcome',
    ];

    const domain = match('src/app/contexts/daily-record/domain/calendar-date.ts');
    assert.deepEqual(domain.metadata.reservedDeclarationSuffixes, reserved);
    assert.equal(domain.metadata.allowedDeclarationSuffixes, undefined);

    assert.deepEqual(
      match(
        'src/app/contexts/daily-record/application/port/in/' + 'create-daily-record.use-case.ts',
      ).metadata.allowedDeclarationSuffixes,
      ['Command', 'Query', 'Result'],
    );

    assert.deepEqual(
      match('src/app/contexts/daily-record/application/port/out/' + 'daily-record.gateway.ts')
        .metadata.allowedDeclarationSuffixes,
      ['Criteria', 'Instruction', 'Outcome'],
    );

    assert.deepEqual(
      match('src/app/shell/navigation/presenter/mapper/' + 'navigation.mapper.ts').metadata
        .allowedDeclarationSuffixes,
      ['Intent', 'View'],
    );
  });
  it('describes the allowed dependency layers at each boundary', () => {
    assert.deepEqual(
      match('src/app/contexts/daily-record/domain/daily-record.ts').metadata
        .allowedDependencyLayers,
      ['domain'],
    );

    assert.deepEqual(
      match('src/app/contexts/daily-record/application/service/' + 'create-daily-record.service.ts')
        .metadata.allowedDependencyLayers,
      ['domain', 'application/port/in', 'application/port/out'],
    );

    assert.deepEqual(
      match('src/app/contexts/daily-record/configuration/providers/' + 'daily-record.providers.ts')
        .metadata.allowedDependencyLayers,
      [
        'domain',
        'application/port/in',
        'application/port/out',
        'application/service',
        'adapter/in',
        'adapter/out',
        'configuration',
      ],
    );

    assert.deepEqual(
      match('src/app/shell/navigation/page/navigation.page.ts').metadata.allowedDependencyLayers,
      ['model', 'page', 'presenter'],
    );

    assert.deepEqual(
      match('src/app/composition/app.composition.ts').metadata.allowedDependencyLayers,
      ['configuration', 'composition'],
    );

    assert.deepEqual(
      match(
        'src/app/contexts/daily-record/application/port/in/' + 'create-daily-record.use-case.ts',
      ).metadata.allowedDependencyLayers,
      ['domain'],
    );

    assert.deepEqual(
      match('src/app/contexts/daily-record/application/port/out/' + 'daily-record.gateway.ts')
        .metadata.allowedDependencyLayers,
      ['domain'],
    );

    assert.deepEqual(
      match(
        'src/app/contexts/daily-record/adapter/in/web/logging/page/' +
          'daily-record.logging.page.ts',
      ).metadata.allowedDependencyLayers,
      ['domain', 'application/port/in', 'adapter/in'],
    );

    assert.deepEqual(
      match('src/app/contexts/daily-record/adapter/out/http/' + 'http-daily-record.gateway.ts')
        .metadata.allowedDependencyLayers,
      ['domain', 'application/port/out', 'adapter/out'],
    );

    assert.deepEqual(
      match('src/app/shell/navigation/configuration/providers/' + 'navigation.providers.ts')
        .metadata.allowedDependencyLayers,
      ['model', 'page', 'presenter', 'configuration'],
    );

    assert.deepEqual(
      match('src/app/shell/navigation/api/page.ts').metadata.allowedDependencyLayers,
      ['api', 'configuration', 'page'],
    );

    assert.deepEqual(
      match('src/app/configuration/app.config.ts').metadata.allowedDependencyLayers,
      ['configuration'],
    );
  });
  it('distinguishes context and shared API dependency permissions', () => {
    assert.deepEqual(
      match('src/app/contexts/daily-record/api/routes.ts').metadata.allowedDependencyLayers,
      ['api', 'configuration'],
    );

    assert.deepEqual(
      match('src/app/shared/api/shared.context-navigation.ts').metadata.allowedDependencyLayers,
      [
        'api',
        'domain',
        'application/port/in',
        'application/port/out',
        'application/service',
        'adapter/in',
        'adapter/out',
        'configuration',
      ],
    );
  });
});
