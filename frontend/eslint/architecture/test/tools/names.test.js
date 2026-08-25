// @ts-check

'use strict';

const assert = require('node:assert/strict');
const { describe, it } = require('node:test');

const {
  matchesDeclarationName,
  resolveDeclarationName,
  toLowerCamelCase,
  toPascalCase,
  toUpperSnakeCase,
} = require('../../tools/names');

describe('architectural names', () => {
  it('converts filename and scope segments to PascalCase', () => {
    assert.equal(toPascalCase('daily-record.reporting.page'), 'DailyRecordReportingPage');
  });

  it('converts a PascalCase declaration name to lower camel case', () => {
    assert.equal(toLowerCamelCase('DailyRecord'), 'dailyRecord');
  });

  it('converts a declaration name to upper snake case', () => {
    assert.equal(toUpperSnakeCase('DailyRecordAPI'), 'DAILY_RECORD_API');
  });
  it('resolves declaration names from the filename and its matched parts', () => {
    const page = {
      implementation: null,
      responsibility: null,
      segment: 'page',
    };
    const service = {
      implementation: null,
      responsibility: 'create-daily-record',
      segment: 'service',
    };
    const useCase = {
      implementation: null,
      responsibility: 'create-daily-record',
      segment: 'use-case',
    };
    const gateway = {
      implementation: null,
      responsibility: 'daily-record',
      segment: 'gateway',
    };
    const adapter = {
      implementation: 'http',
      responsibility: 'daily-record',
      segment: 'gateway',
    };

    assert.equal(
      resolveDeclarationName('<PascalFileStem>', 'daily-record.logging.page', page),
      'DailyRecordLoggingPage',
    );
    assert.equal(
      resolveDeclarationName('<LowerCamelFileStem>', 'daily-record', page),
      'dailyRecord',
    );
    assert.equal(
      resolveDeclarationName('is<PascalFileStem>', 'daily-record', page),
      'isDailyRecord',
    );
    assert.equal(
      resolveDeclarationName(
        '<PascalResponsibility>UseCase',
        'create-daily-record.service',
        service,
      ),
      'CreateDailyRecordUseCase',
    );

    const declarationName = '<PascalImplementation><PascalResponsibility><PascalSegment>';

    assert.equal(
      resolveDeclarationName(declarationName, 'create-daily-record.use-case', useCase),
      'CreateDailyRecordUseCase',
    );
    assert.equal(
      resolveDeclarationName(declarationName, 'daily-record.gateway', gateway),
      'DailyRecordGateway',
    );
    assert.equal(
      resolveDeclarationName(declarationName, 'http-daily-record.gateway', adapter),
      'HttpDailyRecordGateway',
    );
  });
  it('resolves declaration names from the architectural naming scope', () => {
    const matchedName = {
      implementation: null,
      responsibility: null,
      segment: 'routes',
    };
    const namingScope = ['daily-record', 'reporting'];

    assert.equal(
      resolveDeclarationName(
        '<PascalScope>Routes',
        'daily-record.reporting.routes',
        matchedName,
        namingScope,
      ),
      'DailyRecordReportingRoutes',
    );
    assert.equal(
      resolveDeclarationName(
        '<LowerCamelScope>Routes',
        'daily-record.reporting.routes',
        matchedName,
        namingScope,
      ),
      'dailyRecordReportingRoutes',
    );
    assert.equal(
      resolveDeclarationName(
        '<UpperSnakeScope>_ROUTES',
        'daily-record.reporting.routes',
        matchedName,
        namingScope,
      ),
      'DAILY_RECORD_REPORTING_ROUTES',
    );
    assert.equal(
      resolveDeclarationName('<PascalLocalScope>Model', 'shared.navigation.model', matchedName, [
        'shared',
        'navigation',
      ]),
      'NavigationModel',
    );
  });
  it('matches a variable PascalCase part in a declaration name', () => {
    const matchedName = {
      implementation: null,
      responsibility: null,
      segment: 'model',
    };

    assert.equal(
      matchesDeclarationName(
        'DailyRecordLoggingView',
        '<PascalName>View',
        'daily-record.logging.model',
        matchedName,
        [],
      ),
      true,
    );
    assert.equal(
      matchesDeclarationName(
        'View',
        '<PascalName>View',
        'daily-record.logging.model',
        matchedName,
        [],
      ),
      false,
    );
    assert.equal(
      matchesDeclarationName(
        'dailyRecordLoggingView',
        '<PascalName>View',
        'daily-record.logging.model',
        matchedName,
        [],
      ),
      false,
    );
  });
  it('matches an optional PascalCase part in a declaration name', () => {
    const matchedName = {
      implementation: null,
      responsibility: null,
      segment: null,
    };
    const expectedName = '<OptionalPascalName><PascalScope>ResponseDto';

    assert.equal(
      matchesDeclarationName(
        'DailyRecordResponseDto',
        expectedName,
        'daily-record-http-contract',
        matchedName,
        ['daily-record'],
      ),
      true,
    );
    assert.equal(
      matchesDeclarationName(
        'GetDailyRecordResponseDto',
        expectedName,
        'daily-record-http-contract',
        matchedName,
        ['daily-record'],
      ),
      true,
    );
    assert.equal(
      matchesDeclarationName(
        'ResponseDto',
        expectedName,
        'daily-record-http-contract',
        matchedName,
        ['daily-record'],
      ),
      false,
    );
  });
  it('matches an upper-snake name containing the architectural scope', () => {
    const matchedName = {
      implementation: null,
      responsibility: null,
      segment: 'providers',
    };
    const expectedName = '<UpperSnakeNameContainingScope>_USE_CASE';
    const namingScope = ['daily-record'];

    assert.equal(
      matchesDeclarationName(
        'GET_DAILY_RECORD_USE_CASE',
        expectedName,
        'daily-record.providers',
        matchedName,
        namingScope,
      ),
      true,
    );
    assert.equal(
      matchesDeclarationName(
        'DAILY_RECORD_USE_CASE',
        expectedName,
        'daily-record.providers',
        matchedName,
        namingScope,
      ),
      true,
    );
    assert.equal(
      matchesDeclarationName(
        'GET_RECORD_USE_CASE',
        expectedName,
        'daily-record.providers',
        matchedName,
        namingScope,
      ),
      false,
    );
    assert.equal(
      matchesDeclarationName(
        'get_DAILY_RECORD_USE_CASE',
        expectedName,
        'daily-record.providers',
        matchedName,
        namingScope,
      ),
      false,
    );
  });
});
