// @ts-check

'use strict';

const assert = require('node:assert/strict');
const path = require('node:path');
const { describe, it } = require('node:test');

const { ESLint } = require('eslint');
const tseslint = require('typescript-eslint');

const architecture = require('../../index');

const fixtures = path.resolve(__dirname, '../fixtures');

const eslint = new ESLint({
  cwd: fixtures,
  overrideConfigFile: true,
  overrideConfig: [
    {
      plugins: {
        architecture,
      },
      rules: {
        'architecture/declarations': 'error',
      },
    },
    {
      files: ['**/*.ts'],
      languageOptions: {
        parser: tseslint.parser,
      },
    },
    {
      files: ['**/*.html', '**/*.scss'],
      processor: 'architecture/filename-only',
    },
  ],
});

/** @param {string} filePath */
async function lintFixture(filePath) {
  const [result] = await eslint.lintFiles([filePath]);

  assert.ok(result);

  return result.messages;
}

/**
 * @param {string} source
 * @param {string} filePath
 */
async function lint(source, filePath) {
  const [result] = await eslint.lintText(source, { filePath });

  assert.ok(result);

  return result.messages;
}

describe('declarations', () => {
  it('accepts a file matching its declaration expectation', async () => {
    assert.deepEqual(
      await lintFixture(
        'src/app/contexts/nutrition/adapter/in/web/valid-declarations/page/' +
          'nutrition.valid-declarations.page.ts',
      ),
      [],
    );
  });

  it('explains why a declaration is rejected', async () => {
    const [message] = await lintFixture(
      'src/app/contexts/nutrition/adapter/in/web/invalid-declarations/page/' +
        'nutrition.invalid-declarations.page.ts',
    );

    assert.ok(message);
    assert.equal(message.ruleId, 'architecture/declarations');
    assert.equal(message.messageId, 'invalidExports');
    assert.equal(
      message.message,
      'src/app/contexts/nutrition/adapter/in/web/invalid-declarations/page/' +
        'nutrition.invalid-declarations.page.ts does not follow its architectural ' +
        'declaration convention; expected exported declarations are ' +
        '[class NutritionInvalidDeclarationsPage].',
    );
  });
  it('ignores page template and stylesheet companions', async () => {
    assert.deepEqual(
      await lintFixture(
        'src/app/contexts/nutrition/adapter/in/web/search/page/nutrition.search.page.html',
      ),
      [],
    );
    assert.deepEqual(
      await lintFixture(
        'src/app/contexts/nutrition/adapter/in/web/search/page/nutrition.search.page.scss',
      ),
      [],
    );
  });
  it('validates domain declarations and optional helpers', async () => {
    const filePath = 'src/app/contexts/nutrition/domain/food-entry.ts';

    assert.deepEqual(
      await lint(
        `
            export interface FoodEntry {}
            export function parseFoodEntry() {}
          `,
        filePath,
      ),
      [],
    );

    const [message] = await lint('export const FoodEntry = true;', filePath);

    assert.equal(message?.messageId, 'invalidExports');
  });
  it('validates an application service implementation', async () => {
    const filePath = 'src/app/contexts/nutrition/application/service/create-food-entry.service.ts';

    assert.deepEqual(
      await lint(
        `
            export class CreateFoodEntryService
              implements CreateFoodEntryUseCase {}
          `,
        filePath,
      ),
      [],
    );

    const [message] = await lint('export class CreateFoodEntryService {}', filePath);

    assert.equal(message?.messageId, 'invalidExports');
    assert.equal(
      message.message,
      `${filePath} does not follow its architectural declaration convention; ` +
        'expected exported declarations are ' +
        '[class CreateFoodEntryService implementing exactly [CreateFoodEntryUseCase]].',
    );
  });
  it('validates application port declarations from their filename parts', async () => {
    const validPorts = [
      {
        filePath: 'src/app/contexts/nutrition/application/port/in/create-food-entry.use-case.ts',
        source: `
    export interface CreateFoodEntryResult {
      readonly resultData: string;
    }

    export interface CreateFoodEntryUseCase {
      execute(): CreateFoodEntryResult;
    }
  `,
      },
      {
        filePath: 'src/app/contexts/nutrition/application/port/out/food-entry.gateway.ts',
        source: 'export interface FoodEntryGateway {}',
      },
      {
        filePath: 'src/app/contexts/nutrition/application/port/out/today.provider.ts',
        source: 'export interface TodayProvider {}',
      },
    ];

    for (const port of validPorts) {
      assert.deepEqual(await lint(port.source, port.filePath), []);
    }

    const filePath = 'src/app/contexts/nutrition/application/port/out/food-entry.gateway.ts';
    const [message] = await lint('export class FoodEntryGateway {}', filePath);

    assert.equal(message?.messageId, 'invalidExports');
    assert.equal(
      message.message,
      `${filePath} does not follow its architectural declaration convention; ` +
        'expected exported declarations are [interface FoodEntryGateway].',
    );
  });
  it('validates outbound adapter declarations from their filename parts', async () => {
    const validAdapters = [
      {
        filePath: 'src/app/contexts/nutrition/adapter/out/http/http-food-entry.gateway.ts',
        source: 'export class HttpFoodEntryGateway implements FoodEntryGateway {}',
      },
      {
        filePath: 'src/app/contexts/nutrition/adapter/out/time/browser-today.provider.ts',
        source: 'export class BrowserTodayProvider implements TodayProvider {}',
      },
    ];

    for (const adapter of validAdapters) {
      assert.deepEqual(await lint(adapter.source, adapter.filePath), []);
    }

    const filePath = 'src/app/contexts/nutrition/adapter/out/http/http-food-entry.gateway.ts';
    const [message] = await lint('export class HttpFoodEntryGateway {}', filePath);

    assert.equal(message?.messageId, 'invalidExports');
    assert.equal(
      message.message,
      `${filePath} does not follow its architectural declaration convention; ` +
        'expected exported declarations are ' +
        '[class HttpFoodEntryGateway implementing exactly [FoodEntryGateway]].',
    );
  });
  it('validates application-root configuration exports', async () => {
    assert.deepEqual(
      await lint('export const appConfig = {};', 'src/app/configuration/app.config.ts'),
      [],
    );

    assert.deepEqual(
      await lint(
        `
          export const APPLICATION_CONTEXTS = [];
          export const DEFAULT_APPLICATION_CONTEXT = {};
        `,
        'src/app/configuration/contexts.config.ts',
      ),
      [],
    );

    const filePath = 'src/app/configuration/app.config.ts';
    const [message] = await lint('export const wrongConfig = {};', filePath);

    assert.equal(message?.messageId, 'invalidExports');
    assert.equal(
      message.message,
      `${filePath} does not follow its architectural declaration convention; ` +
        'expected exported declarations are [const appConfig].',
    );
  });
  it('validates context configuration exports from the complete scope', async () => {
    assert.deepEqual(
      await lint(
        `
          export const NUTRITION_PATHS = {};
          export const NUTRITION_NAVIGATION = {};
        `,
        'src/app/contexts/nutrition/configuration/navigation/nutrition.navigation.ts',
      ),
      [],
    );

    const filePath =
      'src/app/contexts/nutrition/reporting/configuration/routes/' +
      'nutrition.reporting.routes.ts';

    assert.deepEqual(await lint('export const NUTRITION_REPORTING_ROUTES = [];', filePath), []);

    const [message] = await lint('export const NUTRITION_ROUTES = [];', filePath);

    assert.equal(message?.messageId, 'invalidExports');
    assert.equal(
      message.message,
      `${filePath} does not follow its architectural declaration convention; ` +
        'expected exported declarations are [const NUTRITION_REPORTING_ROUTES].',
    );
  });
  it('validates the shared navigation configuration contract', async () => {
    const filePath =
      'src/app/shared/configuration/navigation/' + 'shared.configuration.navigation.ts';

    assert.deepEqual(
      await lint(
        `
          export interface SharedConfigurationNavigation {}
          export function defineContextNavigation() {}
        `,
        filePath,
      ),
      [],
    );

    const [message] = await lint('export interface SharedConfigurationNavigation {}', filePath);

    assert.equal(message?.messageId, 'invalidExports');
    assert.equal(
      message.message,
      `${filePath} does not follow its architectural declaration convention; ` +
        'expected exported declarations are ' +
        '[interface SharedConfigurationNavigation, function defineContextNavigation].',
    );
  });
  it('validates shell route and section exports', async () => {
    assert.deepEqual(
      await lint(
        'export const routes = [];',
        'src/app/shell/app/configuration/routes/app.routes.ts',
      ),
      [],
    );

    const filePath =
      'src/app/shell/administration/users/configuration/sections/' +
      'administration.users.sections.ts';

    assert.deepEqual(await lint('export const ADMINISTRATION_USERS_SECTIONS = [];', filePath), []);

    const [message] = await lint('export const USERS_SECTIONS = [];', filePath);

    assert.equal(message?.messageId, 'invalidExports');
    assert.equal(
      message.message,
      `${filePath} does not follow its architectural declaration convention; ` +
        'expected exported declarations are ' +
        '[const ADMINISTRATION_USERS_SECTIONS].',
    );
  });
  it('validates a shared HTTP DTO and its optional guard', async () => {
    const filePath =
      'src/app/shared/adapter/out/http/contract/' + 'shared.problem-detail-response.dto.ts';

    assert.deepEqual(
      await lint(
        `
          export interface ProblemDetailResponseDto {}
          export function isProblemDetailResponseDto() {}
        `,
        filePath,
      ),
      [],
    );

    assert.deepEqual(await lint('export interface ProblemDetailResponseDto {}', filePath), []);

    const [message] = await lint(
      `
        export interface ProblemDetailResponseDto {}
        export function parseProblemDetailResponseDto() {}
      `,
      filePath,
    );

    assert.equal(message?.messageId, 'invalidExports');
    assert.equal(
      message.message,
      `${filePath} does not follow its architectural declaration convention; ` +
        'expected exported declarations are [interface ProblemDetailResponseDto].',
    );
  });
  it('validates exact and shared presentation model declarations', async () => {
    const contextFile =
      'src/app/contexts/nutrition/adapter/in/web/logging/model/' + 'nutrition.logging.model.ts';

    assert.deepEqual(
      await lint(
        `
          export interface NutritionLoggingView {}
          export type NutritionLoggingReadModel = {};
          export class NutritionLoggingModel {}
        `,
        contextFile,
      ),
      [],
    );

    const [contextMessage] = await lint('export class LoggingModel {}', contextFile);

    assert.equal(contextMessage?.messageId, 'invalidExports');

    const sharedFile =
      'src/app/shared/adapter/in/web/navigation/model/' + 'shared.navigation.model.ts';

    assert.deepEqual(
      await lint(
        `
          export interface ContextDestination {}
          export abstract class ContextNavigationModel {}
        `,
        sharedFile,
      ),
      [],
    );

    const [sharedMessage] = await lint(
      `
        export class FirstNavigationModel {}
        export class SecondNavigationModel {}
      `,
      sharedFile,
    );

    assert.equal(sharedMessage?.messageId, 'invalidExports');
  });
  it('validates context HTTP contract declarations', async () => {
    const filePath =
      'src/app/contexts/daily-record/adapter/out/http/contract/' + 'daily-record-http-contract.ts';

    assert.deepEqual(
      await lint(
        `
          export class DailyRecordUrl {}
          export interface GetDailyRecordRequestParameters {}
          export interface CreateDailyRecordRequestBodyDto {}
          export interface DailyRecordResponseDto {}
        `,
        filePath,
      ),
      [],
    );

    const [message] = await lint('export interface DailyRecordRequestDto {}', filePath);

    assert.equal(message?.messageId, 'invalidExports');
    assert.equal(
      message.message,
      `${filePath} does not follow its architectural declaration convention; ` +
        'expected exported declarations are ' +
        '[class DailyRecordUrl, ' +
        'interface <OptionalPascalName>DailyRecordRequestParameters, ' +
        'interface <OptionalPascalName>DailyRecordRequestBodyDto, ' +
        'interface <OptionalPascalName>DailyRecordResponseDto].',
    );
  });
  it('validates HTTP error translator functions', async () => {
    const filePath =
      'src/app/contexts/daily-record/adapter/out/http/errors/translator/' +
      'daily-record-http-error.translator.ts';

    assert.deepEqual(
      await lint(
        `
          export function translateGetDailyRecordHttpError() {}
          export function translateCreateDailyRecordHttpError() {}
        `,
        filePath,
      ),
      [],
    );

    assert.deepEqual(
      await lint('export function translateDailyRecordHttpError() {}', filePath),
      [],
    );

    const [message] = await lint('export function translateGetRecordHttpError() {}', filePath);

    assert.equal(message?.messageId, 'invalidExports');
    assert.equal(
      message.message,
      `${filePath} does not follow its architectural declaration convention; ` +
        'expected exported declarations are ' +
        '[function translate<OptionalPascalName>DailyRecordHttpError].',
    );
  });
  it('validates shell provider exports from their scope', async () => {
    assert.deepEqual(
      await lint(
        'export const appProviders = [];',
        'src/app/shell/app/configuration/providers/app.providers.ts',
      ),
      [],
    );

    const filePath =
      'src/app/shell/administration/users/configuration/providers/' +
      'administration.users.providers.ts';

    assert.deepEqual(
      await lint('export function provideApplicationAdministrationUsers() {}', filePath),
      [],
    );

    const [message] = await lint('export const usersProviders = [];', filePath);

    assert.equal(message?.messageId, 'invalidExports');
    assert.equal(
      message.message,
      `${filePath} does not follow its architectural declaration convention; ` +
        'expected exported declarations are ' +
        '[const administrationUsersProviders, ' +
        'function provideApplicationAdministrationUsers].',
    );
  });
  it('validates context provider tokens and factories', async () => {
    const filePath =
      'src/app/contexts/daily-record/configuration/providers/' + 'daily-record.providers.ts';

    assert.deepEqual(
      await lint(
        `
          export const GET_DAILY_RECORD_USE_CASE = {};
          export function provideDailyRecordContext() {}
          export function provideDailyRecordLoggingPresenter() {}
        `,
        filePath,
      ),
      [],
    );

    const [message] = await lint('export const GET_RECORD_USE_CASE = {};', filePath);

    assert.equal(message?.messageId, 'invalidExports');
    assert.equal(
      message.message,
      `${filePath} does not follow its architectural declaration convention; ` +
        'expected exported declarations are ' +
        '[const <UpperSnakeNameContainingScope>_USE_CASE, ' +
        'function provideDailyRecord<OptionalPascalName>Context, ' +
        'function provideDailyRecord<OptionalPascalName>Presenter].',
    );
  });
  it('validates exact context public API re-exports', async () => {
    const filePath = 'src/app/contexts/daily-record/reporting/api/routes.ts';
    const source = '../configuration/routes/daily-record.reporting.routes';

    assert.deepEqual(
      await lint(
        `
          export { DAILY_RECORD_REPORTING_ROUTES }
            from '${source}';
        `,
        filePath,
      ),
      [],
    );

    const [message] = await lint(
      `
        export {
          DAILY_RECORD_REPORTING_ROUTES as routes
        } from '${source}';
      `,
      filePath,
    );

    assert.equal(message?.messageId, 'invalidReExports');
    assert.equal(
      message.message,
      `${filePath} does not follow its architectural public API convention; ` +
        'expected one named, unaliased re-export of ' +
        '[DAILY_RECORD_REPORTING_ROUTES] from ' +
        '../configuration/routes/daily-record.reporting.routes.',
    );
  });
  it('validates shell public API re-exports', async () => {
    const filePath = 'src/app/shell/administration/users/api/page.ts';
    const source = '../page/administration.users.page';

    assert.deepEqual(
      await lint(`export { AdministrationUsersPage } from '${source}';`, filePath),
      [],
    );

    const [message] = await lint(`export { WrongPage } from '${source}';`, filePath);

    assert.equal(message?.messageId, 'invalidReExports');
    assert.equal(
      message.message,
      `${filePath} does not follow its architectural public API convention; ` +
        'expected one named, unaliased re-export of ' +
        '[AdministrationUsersPage] from ' +
        '../page/administration.users.page.',
    );
  });

  it('validates shared public API re-exports', async () => {
    const filePath = 'src/app/shared/api/shared.context-navigation.ts';

    assert.deepEqual(
      await lint(
        `
          export { ContextNavigationModel } from '../adapter/in/navigation';
          export { defineContextNavigation } from '../configuration/navigation';
        `,
        filePath,
      ),
      [],
    );

    const [crossModuleMessage] = await lint(
      `export { DAILY_RECORD_ROUTES } from '../../contexts/daily-record/api/routes';`,
      filePath,
    );
    assert.equal(crossModuleMessage?.messageId, 'invalidReExports');

    const [externalMessage] = await lint(`export { Component } from '@angular/core';`, filePath);
    assert.equal(externalMessage?.messageId, 'invalidReExports');

    const [aliasMessage] = await lint(
      `export { ContextNavigationModel as Navigation } from '../internal';`,
      filePath,
    );
    assert.equal(aliasMessage?.messageId, 'invalidReExports');
  });
  it('validates presenter factories and their matching tokens', async () => {
    const filePath =
      'src/app/contexts/nutrition/adapter/in/web/logging/presenter/' +
      'nutrition.logging.presenter.ts';

    assert.deepEqual(
      await lint(
        `
          export class NutritionLoggingPresenter {}
          export type ApplicationNutritionLoggingPresenterFactory =
            () => NutritionLoggingPresenter;
          export const APPLICATION_NUTRITION_LOGGING_PRESENTER_FACTORY =
            Symbol();
        `,
        filePath,
      ),
      [],
    );

    const [message] = await lint(
      `
        export class NutritionLoggingPresenter {}
        export type ApplicationNutritionLoggingPresenterFactory =
          () => NutritionLoggingPresenter;
        export const WRONG_PRESENTER_FACTORY = Symbol();
      `,
      filePath,
    );

    assert.equal(message?.messageId, 'invalidExports');
    assert.equal(
      message.message,
      `${filePath} does not follow its architectural declaration convention; ` +
        'expected exported declarations are ' +
        '[class NutritionLoggingPresenter, ' +
        'type <OptionalPascalName>NutritionLoggingPresenterFactory ' +
        'as a zero-argument function returning NutritionLoggingPresenter, ' +
        'const named as the UPPER_SNAKE_CASE form of ' +
        '<OptionalPascalName>NutritionLoggingPresenterFactory].',
    );
  });
  it('requires inbound results to expose resultData', async () => {
    const filePath =
      'src/app/contexts/nutrition/application/port/in/' + 'create-food-entry.use-case.ts';

    const [message] = await lint(
      `
        export interface CreateFoodEntryResult {}
        export interface CreateFoodEntryUseCase {}
      `,
      filePath,
    );

    assert.equal(message?.messageId, 'invalidExports');
    assert.equal(
      message.message,
      `${filePath} does not follow its architectural declaration convention; ` +
        'expected exported declarations are ' +
        '[interface or type CreateFoodEntryResult exposing required properties ' +
        '[resultData], interface CreateFoodEntryUseCase].',
    );
  });
  it('validates outbound port data contracts', async () => {
    const filePath = 'src/app/contexts/nutrition/application/port/out/' + 'food-entry.gateway.ts';

    assert.deepEqual(
      await lint(
        `
          export interface FindFoodEntryCriteria {}
          export interface FindFoodEntryOutcome {
            readonly outcomeData: string;
          }
          export interface FoodEntryGateway {}
        `,
        filePath,
      ),
      [],
    );

    const [missingData] = await lint(
      `
        export interface FindFoodEntryOutcome {}
        export interface FoodEntryGateway {}
      `,
      filePath,
    );

    assert.equal(missingData?.messageId, 'invalidExports');

    const [invalidContract] = await lint(
      `
        export interface FindFoodEntryResponse {}
        export interface FoodEntryGateway {}
      `,
      filePath,
    );

    assert.equal(invalidContract?.messageId, 'invalidExports');
  });
  it('rejects unsupported exports and ignores specification files', async () => {
    const filePath =
      'src/app/contexts/nutrition/adapter/in/web/search/page/' + 'nutrition.search.page.ts';

    const [unsupportedExport] = await lint('export default class NutritionSearchPage {}', filePath);

    assert.equal(unsupportedExport?.messageId, 'invalidExports');

    assert.deepEqual(
      await lint(
        'export interface PageFixture {}',
        'src/app/contexts/nutrition/adapter/in/web/search/page/' + 'nutrition.search.page.spec.ts',
      ),
      [],
    );
  });
});
