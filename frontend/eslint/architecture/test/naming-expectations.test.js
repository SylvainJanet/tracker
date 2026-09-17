// @ts-check

'use strict';

const assert = require('node:assert/strict');
const { describe, it } = require('node:test');

const architecture = require('../architecture');
const { createFileNameConvention } = require('../tools/file-names');
const { analyzePath } = require('../tools/analyze-path');
const { matchArchitecture } = require('../tools/match-architecture');

/** @param {string} filePath */
function conventionsFor(filePath) {
  const path = analyzePath(filePath);
  const expected = matchArchitecture(architecture, path);

  return expected.folder.files.map((file) =>
    createFileNameConvention(file.naming, expected.namingScope),
  );
}

/** @param {string} filePath */
function accepts(filePath) {
  const fileName = analyzePath(filePath).fileName;

  return conventionsFor(filePath).some((convention) => convention.matches(fileName));
}

describe('architecture naming expectations', () => {
  const acceptedFileNames = [
    'src/app/configuration/app.config.spec.ts',
    'src/app/composition/app.composition.ts',

    'src/app/contexts/daily-record/api/routes.ts',
    'src/app/shared/api/shared.problem-detail-response.ts',
    'src/app/shell/administration/users/api/providers.ts',

    'src/app/contexts/daily-record/domain/calendar-date.ts',
    'src/app/contexts/daily-record/application/port/in/create-daily-record.use-case.ts',
    'src/app/contexts/daily-record/application/port/out/daily-record.gateway.ts',
    'src/app/contexts/daily-record/application/port/out/today.provider.ts',
    'src/app/contexts/daily-record/application/service/create-daily-record.service.ts',

    'src/app/contexts/daily-record/reporting/configuration/routes/daily-record.reporting.routes.ts',
    'src/app/shared/configuration/providers/shared.configuration.providers.ts',
    'src/app/shell/administration/users/configuration/sections/administration.users.sections.ts',

    'src/app/contexts/daily-record/adapter/in/web/logging/page/daily-record.logging.page.html',
    'src/app/contexts/daily-record/adapter/in/web/logging/presenter/mapper/daily-record.logging.mapper.ts',
    'src/app/shared/adapter/in/web/navigation/model/shared.navigation.model.ts',
    'src/app/shell/administration/users/page/administration.users.page.ts',

    'src/app/contexts/daily-record/adapter/out/http/http-daily-record.gateway.ts',
    'src/app/contexts/daily-record/adapter/out/time/browser-today.provider.ts',

    'src/app/contexts/daily-record/adapter/out/http/contract/daily-record-http-contract.ts',
    'src/app/contexts/daily-record/adapter/out/http/contract/daily-record-http-contract.mapper.ts',
    'src/app/shared/adapter/out/http/contract/shared.problem-detail-response.dto.ts',

    'src/app/contexts/daily-record/adapter/out/http/errors/daily-record-unexpected-gateway.error.ts',
    'src/app/contexts/daily-record/adapter/out/http/errors/translator/daily-record-http-error.translator.ts',
    'src/app/shared/network/adapter/out/http/errors/shared.network-unavailable-service.error.ts',

    'src/app/contexts/daily-record/adapter/in/web/logging/model/daily-record.logging.model.ts',
    'src/app/contexts/daily-record/adapter/in/web/logging/model/daily-record.logging.model.spec.ts',
    'src/app/contexts/daily-record/adapter/in/web/logging/page/daily-record.logging.page.ts',
    'src/app/contexts/daily-record/adapter/in/web/logging/page/daily-record.logging.page.spec.ts',
    'src/app/contexts/daily-record/adapter/in/web/logging/presenter/daily-record.logging.presenter.ts',
    'src/app/contexts/daily-record/adapter/in/web/reports/monthly/page/daily-record.reports.monthly.page.ts',

    'src/app/shared/adapter/in/web/navigation/model/shared.navigation.model.spec.ts',
    'src/app/shared/adapter/in/web/navigation/presenter/shared.navigation.presenter.ts',
    'src/app/shared/adapter/in/web/navigation/presenter/mapper/shared.navigation.mapper.ts',

    'src/app/shell/app/page/app.page.ts',
    'src/app/shell/app/page/app.page.spec.ts',
    'src/app/shell/navigation/model/navigation.model.ts',
    'src/app/shell/navigation/presenter/navigation.presenter.ts',

    'src/app/contexts/daily-record/domain/calendar-date.spec.ts',
    'src/app/contexts/daily-record/reporting/history/domain/monthly-report.ts',

    'src/app/contexts/daily-record/application/port/in/create-daily-record.use-case.spec.ts',
    'src/app/contexts/daily-record/application/port/out/daily-record.gateway.spec.ts',
    'src/app/contexts/daily-record/application/service/create-daily-record.service.spec.ts',

    'src/app/shared/domain/problem-detail.ts',
    'src/app/shared/application/port/in/get-locale.use-case.ts',
    'src/app/shared/application/port/out/locale.provider.ts',
    'src/app/shared/application/service/get-locale.service.ts',

    'src/app/contexts/daily-record/configuration/navigation/daily-record.navigation.ts',
    'src/app/contexts/daily-record/configuration/navigation/daily-record.navigation.spec.ts',
    'src/app/contexts/daily-record/configuration/providers/daily-record.providers.ts',
    'src/app/contexts/daily-record/configuration/routes/daily-record.routes.ts',
    'src/app/contexts/daily-record/reporting/history/configuration/routes/daily-record.reporting.history.routes.ts',

    'src/app/shared/configuration/navigation/shared.configuration.navigation.ts',
    'src/app/shared/configuration/navigation/shared.configuration.navigation.spec.ts',

    'src/app/shell/app/configuration/providers/app.providers.ts',
    'src/app/shell/app/configuration/routes/app.routes.spec.ts',
    'src/app/shell/navigation/configuration/sections/navigation.sections.ts',
    'src/app/shell/administration/users/configuration/providers/administration.users.providers.ts',

    'src/app/configuration/app.config.ts',
    'src/app/configuration/contexts.config.ts',
    'src/app/composition/app.composition.spec.ts',

    'src/app/contexts/daily-record/api/navigation.ts',
    'src/app/contexts/daily-record/reporting/api/navigation.ts',
    'src/app/contexts/daily-record/reporting/api/routes.ts',

    'src/app/shared/api/shared.context-navigation.ts',

    'src/app/shell/app/api/page.ts',
    'src/app/shell/app/api/providers.ts',
    'src/app/shell/navigation/api/page.ts',
    'src/app/shell/navigation/api/providers.ts',
    'src/app/shell/administration/users/api/page.ts',

    'src/app/contexts/daily-record/adapter/out/http/http-daily-record.gateway.spec.ts',
    'src/app/contexts/daily-record/reporting/adapter/out/http/http-monthly-report.gateway.ts',
    'src/app/contexts/daily-record/adapter/out/time/browser-today.provider.spec.ts',

    'src/app/shared/adapter/out/http/http-problem-detail.gateway.ts',
    'src/app/shared/adapter/out/time/system-today.provider.ts',

    'src/app/contexts/daily-record/adapter/out/http/contract/daily-record-http-contract.spec.ts',
    'src/app/contexts/daily-record/adapter/out/http/contract/daily-record-http-contract.mapper.spec.ts',
    'src/app/contexts/daily-record/reporting/history/adapter/out/http/contract/daily-record.reporting.history-http-contract.ts',

    'src/app/shared/adapter/out/http/contract/shared.problem-detail-response.dto.spec.ts',
    'src/app/shared/adapter/out/http/contract/shared.validation-problem.dto.ts',
    'src/app/shared/reporting/adapter/out/http/contract/shared.reporting.problem-detail-response.dto.ts',

    'src/app/contexts/daily-record/adapter/out/http/errors/daily-record-unexpected-gateway.error.spec.ts',
    'src/app/contexts/daily-record/adapter/out/http/errors/translator/daily-record-http-error.translator.spec.ts',

    'src/app/contexts/daily-record/reporting/history/adapter/out/http/errors/daily-record.reporting.history-unexpected-response.error.ts',
    'src/app/contexts/daily-record/reporting/history/adapter/out/http/errors/translator/daily-record.reporting.history-http-error.translator.ts',

    'src/app/shared/adapter/out/http/errors/shared-unexpected-response.error.ts',
    'src/app/shared/adapter/out/http/errors/translator/shared-http-error.translator.ts',
    'src/app/shared/network/adapter/out/http/errors/translator/shared.network-http-error.translator.ts',

    'src/app/contexts/daily-record/adapter/in/web/logging/page/daily-record.logging.page.scss',
    'src/app/contexts/daily-record/adapter/in/web/reports/monthly/page/daily-record.reports.monthly.page.html',
    'src/app/contexts/daily-record/adapter/in/web/reports/monthly/page/daily-record.reports.monthly.page.scss',

    'src/app/shared/adapter/in/web/navigation/page/shared.navigation.page.html',
    'src/app/shared/adapter/in/web/navigation/page/shared.navigation.page.scss',

    'src/app/shell/app/page/app.page.html',
    'src/app/shell/app/page/app.page.scss',
    'src/app/shell/navigation/page/navigation.page.html',
    'src/app/shell/navigation/page/navigation.page.scss',
  ];

  for (const filePath of acceptedFileNames) {
    it(`accepts ${filePath}`, () => {
      assert.equal(accepts(filePath), true);
    });
  }

  const rejectedFileNames = [
    'src/app/contexts/daily-record/adapter/in/web/logging/page/logging.page.ts',
    'src/app/contexts/daily-record/adapter/in/web/logging/page/other-record.logging.page.ts',
    'src/app/contexts/daily-record/adapter/in/web/logging/page/daily-record.summary.page.ts',
    'src/app/contexts/daily-record/adapter/in/web/logging/page/daily-record-logging.page.ts',
    'src/app/contexts/daily-record/adapter/in/web/logging/page/daily-record.logging.component.ts',
    'src/app/contexts/daily-record/adapter/in/web/logging/page/daily-record.logging.page.page.ts',
    'src/app/contexts/daily-record/adapter/in/web/logging/page/daily-record.logging.page.test.ts',
    'src/app/contexts/daily-record/adapter/in/web/logging/presenter/daily-record.logging.model.ts',
    'src/app/contexts/daily-record/adapter/in/web/logging/presenter/mapper/daily-record.logging.presenter.mapper.ts',
    'src/app/shared/adapter/in/web/navigation/model/navigation.model.ts',
    'src/app/shared/adapter/in/web/navigation/page/shared.menu.page.ts',
    'src/app/shell/navigation/page/shell.navigation.page.ts',
    'src/app/shell/administration/users/page/users.page.ts',

    'src/app/contexts/daily-record/domain/CalendarDate.ts',
    'src/app/contexts/daily-record/domain/calendar_date.ts',
    'src/app/contexts/daily-record/domain/calendar-date.model.ts',

    'src/app/contexts/daily-record/application/port/in/createDailyRecord.use-case.ts',
    'src/app/contexts/daily-record/application/port/in/create-daily-record.command.ts',
    'src/app/contexts/daily-record/application/port/in/get-daily-record.query.ts',
    'src/app/contexts/daily-record/application/port/in/get-daily-record.result.ts',
    'src/app/contexts/daily-record/application/port/in/create-daily-record.usecase.ts',
    'src/app/contexts/daily-record/application/port/in/create-daily-record.use-case.test.ts',

    'src/app/contexts/daily-record/application/port/out/daily-record.repository.ts',
    'src/app/contexts/daily-record/application/port/out/find-daily-record.criteria.ts',
    'src/app/contexts/daily-record/application/port/out/create-daily-record.instruction.ts',
    'src/app/contexts/daily-record/application/port/out/find-daily-record.outcome.ts',
    'src/app/contexts/daily-record/application/port/out/today.ts',

    'src/app/contexts/daily-record/application/service/createDailyRecord.service.ts',
    'src/app/contexts/daily-record/application/service/create-daily-record.use-case.ts',
    'src/app/contexts/daily-record/application/service/create-daily-record.service.test.ts',

    'src/app/contexts/daily-record/configuration/navigation/navigation.ts',
    'src/app/contexts/daily-record/configuration/navigation/daily-record-navigation.ts',
    'src/app/contexts/daily-record/configuration/daily-record.routes.test.ts',
    'src/app/contexts/daily-record/reporting/history/configuration/daily-record.routes.ts',

    'src/app/shared/configuration/navigation/shared.navigation.ts',
    'src/app/shared/configuration/navigation/configuration.navigation.ts',
    'src/app/shared/configuration/navigation/shared-configuration.navigation.ts',

    'src/app/shell/app/configuration/providers/shell.app.providers.ts',
    'src/app/shell/navigation/configuration/sections/app.sections.ts',
    'src/app/shell/administration/users/configuration/providers/users.providers.ts',

    'src/app/configuration/app.ts',
    'src/app/configuration/App.config.ts',
    'src/app/configuration/contexts_config.ts',
    'src/app/configuration/smth/app.configuration.ts',

    'src/app/composition/app.ts',
    'src/app/composition/App.composition.ts',
    'src/app/composition/app.config.ts',

    'src/app/contexts/daily-record/api/page.ts',
    'src/app/contexts/daily-record/api/providers.ts',
    'src/app/contexts/daily-record/api/monthly-reports.ts',
    'src/app/contexts/daily-record/api/Navigation.ts',
    'src/app/contexts/daily-record/api/context_navigation.ts',
    'src/app/contexts/daily-record/api/daily-record.navigation.ts',
    'src/app/contexts/daily-record/api/navigation.spec.ts',
    'src/app/contexts/daily-record/reporting/api/monthly-reports.ts',

    'src/app/shared/api/context-navigation.ts',
    'src/app/shared/api/shared-context-navigation.ts',
    'src/app/shared/api/shared.ContextNavigation.ts',
    'src/app/shared/api/shared.context-navigation.spec.ts',
    'src/app/shared/api/shared.context.navigation.ts',

    'src/app/shell/app/api/routes.ts',
    'src/app/shell/app/api/navigation.ts',
    'src/app/shell/app/api/user-management.ts',
    'src/app/shell/app/api/app.page.ts',
    'src/app/shell/app/api/Page.ts',
    'src/app/shell/app/api/app_providers.ts',
    'src/app/shell/app/api/page.spec.ts',
    'src/app/shell/administration/users/api/user-management.ts',

    'src/app/contexts/daily-record/adapter/out/http/HttpDailyRecord.gateway.ts',
    'src/app/contexts/daily-record/adapter/out/http/http_daily_record.gateway.ts',
    'src/app/contexts/daily-record/adapter/out/http/http-daily-record.repository.ts',
    'src/app/contexts/daily-record/adapter/out/http/http-daily-record.provider.ts',
    'src/app/contexts/daily-record/adapter/out/http/http-daily-record.ts',
    'src/app/contexts/daily-record/adapter/out/http/http-daily-record.gateway.test.ts',

    'src/app/contexts/daily-record/adapter/out/time/browserToday.provider.ts',
    'src/app/contexts/daily-record/adapter/out/time/browser-today.gateway.ts',
    'src/app/contexts/daily-record/adapter/out/time/browser-today.clock.ts',
    'src/app/contexts/daily-record/adapter/out/time/browser-today.ts',
    'src/app/contexts/daily-record/adapter/out/time/browser-today.provider.test.ts',

    'src/app/shared/adapter/out/http/http-problem-detail.repository.ts',
    'src/app/shared/adapter/out/time/system-today.gateway.ts',

    'src/app/contexts/daily-record/adapter/out/http/contract/daily-record.http-contract.ts',
    'src/app/contexts/daily-record/adapter/out/http/contract/daily-record-contract.ts',
    'src/app/contexts/daily-record/adapter/out/http/contract/http-daily-record-contract.ts',
    'src/app/contexts/daily-record/adapter/out/http/contract/other-http-contract.ts',
    'src/app/contexts/daily-record/adapter/out/http/contract/daily-record-http-contract.dto.ts',
    'src/app/contexts/daily-record/adapter/out/http/contract/daily-record-http.mapper.ts',
    'src/app/contexts/daily-record/adapter/out/http/contract/daily-record-http-contract.test.ts',
    'src/app/contexts/daily-record/reporting/history/adapter/out/http/contract/daily-record-reporting-history-http-contract.ts',

    'src/app/shared/adapter/out/http/contract/problem-detail-response.dto.ts',
    'src/app/shared/adapter/out/http/contract/shared-problem-detail-response.dto.ts',
    'src/app/shared/adapter/out/http/contract/shared.ProblemDetailResponse.dto.ts',
    'src/app/shared/adapter/out/http/contract/shared.problem.detail.response.dto.ts',
    'src/app/shared/adapter/out/http/contract/shared.problem-detail-response.contract.ts',
    'src/app/shared/adapter/out/http/contract/shared.problem-detail-response.dto.test.ts',
    'src/app/shared/reporting/adapter/out/http/contract/shared.problem-detail-response.dto.ts',

    'src/app/contexts/daily-record/adapter/out/http/errors/unexpected-gateway.error.ts',
    'src/app/contexts/daily-record/adapter/out/http/errors/daily-record.unexpected-gateway.error.ts',
    'src/app/contexts/daily-record/adapter/out/http/errors/other-record-unexpected-gateway.error.ts',
    'src/app/contexts/daily-record/adapter/out/http/errors/daily-record-UnexpectedGateway.error.ts',
    'src/app/contexts/daily-record/adapter/out/http/errors/daily-record-unexpected-gateway.exception.ts',
    'src/app/contexts/daily-record/adapter/out/http/errors/daily-record.error.ts',
    'src/app/contexts/daily-record/adapter/out/http/errors/daily-record-unexpected-gateway.error.test.ts',

    'src/app/contexts/daily-record/adapter/out/http/errors/translator/http-error.translator.ts',
    'src/app/contexts/daily-record/adapter/out/http/errors/translator/daily-record-error.translator.ts',
    'src/app/contexts/daily-record/adapter/out/http/errors/translator/other-record-http-error.translator.ts',
    'src/app/contexts/daily-record/adapter/out/http/errors/translator/daily-record-http-error.translator.test.ts',

    'src/app/contexts/daily-record/reporting/history/adapter/out/http/errors/daily-record-reporting-history-unexpected-response.error.ts',
    'src/app/contexts/daily-record/reporting/history/adapter/out/http/errors/translator/daily-record-reporting-history-http-error.translator.ts',

    'src/app/shared/adapter/out/http/errors/unexpected-response.error.ts',
    'src/app/shared/adapter/out/http/errors/shared.unexpected-response.error.ts',
    'src/app/shared/adapter/out/http/errors/translator/http-error.translator.ts',
    'src/app/shared/network/adapter/out/http/errors/shared-unavailable-service.error.ts',

    'src/app/contexts/daily-record/adapter/in/web/logging/page/daily-record.summary.page.html',
    'src/app/contexts/daily-record/adapter/in/web/logging/page/daily-record.logging.component.html',
    'src/app/contexts/daily-record/adapter/in/web/logging/page/daily-record.logging.page.spec.html',
    'src/app/contexts/daily-record/adapter/in/web/logging/page/daily-record.summary.page.scss',
    'src/app/contexts/daily-record/adapter/in/web/logging/page/daily-record.logging.component.scss',
    'src/app/contexts/daily-record/adapter/in/web/logging/model/daily-record.logging.model.html',
    'src/app/contexts/daily-record/adapter/in/web/logging/presenter/daily-record.logging.presenter.scss',

    'src/app/shared/adapter/in/web/navigation/page/navigation.page.html',
    'src/app/shared/adapter/in/web/navigation/page/shared.menu.page.scss',

    'src/app/shell/navigation/page/shell.navigation.page.html',
    'src/app/shell/navigation/page/app.page.scss',
  ];

  for (const filePath of rejectedFileNames) {
    it(`rejects ${filePath}`, () => {
      assert.equal(accepts(filePath), false);
    });
  }

  it('provides readable allowed filenames for diagnostics', () => {
    const conventions = conventionsFor(
      'src/app/contexts/daily-record/application/service/wrong.ts',
    );

    assert.deepEqual(
      conventions.flatMap((convention) => convention.allowedFileNames),
      ['<kebab-case-responsibility>.service.ts', '<kebab-case-responsibility>.service.spec.ts'],
    );
  });
});
