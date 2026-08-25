// @ts-check

'use strict';

const assert = require('node:assert/strict');
const { describe, it } = require('node:test');
const parser = require('@typescript-eslint/parser');

const {
  analyzeDeclarations,
  matchesDeclarationExpectation,
  matchesReExportExpectation,
  analyzeDeclaredNames,
} = require('../../tools/analyze-declarations');

/** @param {string} source */
function parseModule(source) {
  return parser.parse(source, { sourceType: 'module' });
}

/** @type {import('../../types').FileNameMatch} */
const PAGE_NAME = {
  implementation: null,
  responsibility: null,
  segment: 'page',
};

/** @type {import('../../types').FileNameMatch} */
const DOMAIN_NAME = {
  implementation: null,
  responsibility: 'daily-record',
  segment: null,
};

/** @type {import('../../types').FileNameMatch} */
const SERVICE_NAME = {
  implementation: null,
  responsibility: 'daily-record',
  segment: 'service',
};

describe('analyzeDeclarations', () => {
  it('describes direct named exports using normalized syntax names', () => {
    const program = parseModule(`
        export interface DailyRecord {}
        export type DailyRecordId = string;
        export function parseDailyRecord() {}
        export const EMPTY_DAILY_RECORD = {};
        export class DailyRecordBuilder implements DailyRecordFactory {}
      `);

    assert.deepEqual(analyzeDeclarations(program), {
      exported: [
        { name: 'DailyRecord', type: 'interface' },
        { name: 'DailyRecordId', type: 'type' },
        { name: 'parseDailyRecord', type: 'function' },
        { name: 'EMPTY_DAILY_RECORD', type: 'const' },
        {
          name: 'DailyRecordBuilder',
          type: 'class',
          implements: ['DailyRecordFactory'],
        },
      ],
      reExports: [],
      unsupportedExports: false,
    });
  });

  it('marks export forms that direct-declaration rules cannot interpret', () => {
    const program = parseModule(`
        export default class DailyRecord {}
        export const first = 1, second = 2;
      `);

    assert.deepEqual(analyzeDeclarations(program), {
      exported: [],
      reExports: [],
      unsupportedExports: true,
    });
  });
  it('compares direct exports with a concrete declaration expectation', () => {
    /** @type {import('../../types').DeclarationExpectation} */
    const expectation = {
      requiredExports: [
        {
          name: '<PascalFileStem>',
          types: ['class'],
        },
      ],
    };

    assert.equal(
      matchesDeclarationExpectation(
        analyzeDeclarations(parseModule('export class DailyRecordPage {}')),
        expectation,
        'daily-record.page',
        PAGE_NAME,
      ),
      true,
    );

    assert.equal(
      matchesDeclarationExpectation(
        analyzeDeclarations(
          parseModule(`
              export class WrongPage {}
              export function extra() {}
            `),
        ),
        expectation,
        'daily-record.page',
        PAGE_NAME,
      ),
      false,
    );
  });

  it('allows only the listed optional exports', () => {
    /** @type {import('../../types').DeclarationExpectation} */
    const expectation = {
      requiredExports: [
        {
          name: '<PascalFileStem>',
          types: ['interface', 'type'],
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
    };

    assert.equal(
      matchesDeclarationExpectation(
        analyzeDeclarations(
          parseModule(`
              export interface DailyRecord {}
              export function parseDailyRecord() {}
            `),
        ),
        expectation,
        'daily-record',
        DOMAIN_NAME,
      ),
      true,
    );

    assert.equal(
      matchesDeclarationExpectation(
        analyzeDeclarations(
          parseModule(`
              export interface DailyRecord {}
              export function formatDailyRecord() {}
            `),
        ),
        expectation,
        'daily-record',
        DOMAIN_NAME,
      ),
      false,
    );
  });
  it('marks generic class implementations as unsupported', () => {
    const program = parseModule(`
        export class DailyRecordService implements DailyRecordUseCase<string> {}
      `);

    assert.deepEqual(analyzeDeclarations(program), {
      exported: [
        {
          name: 'DailyRecordService',
          type: 'class',
          implements: null,
        },
      ],
      reExports: [],
      unsupportedExports: false,
    });
  });
  it('compares exact class implementations', () => {
    /** @type {import('../../types').DeclarationExpectation} */
    const expectation = {
      requiredExports: [
        {
          name: '<PascalFileStem>',
          types: ['class'],
          implements: ['DailyRecordUseCase'],
        },
      ],
    };

    assert.equal(
      matchesDeclarationExpectation(
        analyzeDeclarations(
          parseModule('export class DailyRecordService implements DailyRecordUseCase {}'),
        ),
        expectation,
        'daily-record.service',
        SERVICE_NAME,
      ),
      true,
    );

    assert.equal(
      matchesDeclarationExpectation(
        analyzeDeclarations(
          parseModule(
            'export class DailyRecordService implements DailyRecordUseCase, OtherUseCase {}',
          ),
        ),
        expectation,
        'daily-record.service',
        SERVICE_NAME,
      ),
      false,
    );
  });
  it('uses the matched filename parts when checking a declaration name', () => {
    /** @type {import('../../types').DeclarationExpectation} */
    const expectation = {
      requiredExports: [
        {
          name: '<PascalImplementation><PascalResponsibility><PascalSegment>',
          types: ['class'],
        },
      ],
    };

    assert.equal(
      matchesDeclarationExpectation(
        analyzeDeclarations(parseModule('export class HttpDailyRecordGateway {}')),
        expectation,
        'http-daily-record.gateway',
        {
          implementation: 'http',
          responsibility: 'daily-record',
          segment: 'gateway',
        },
      ),
      true,
    );
  });
  it('uses the architectural naming scope when checking a declaration name', () => {
    /** @type {import('../../types').DeclarationExpectation} */
    const expectation = {
      requiredExports: [
        {
          name: '<UpperSnakeScope>_ROUTES',
          types: ['const'],
        },
      ],
    };

    assert.equal(
      matchesDeclarationExpectation(
        analyzeDeclarations(parseModule('export const DAILY_RECORD_REPORTING_ROUTES = [];')),
        expectation,
        'daily-record.reporting.routes',
        {
          implementation: null,
          responsibility: null,
          segment: 'routes',
        },
        ['daily-record', 'reporting'],
      ),
      true,
    );
  });
  it('allows an optional export with a variable PascalCase name', () => {
    /** @type {import('../../types').DeclarationExpectation} */
    const expectation = {
      requiredExports: [
        {
          name: '<PascalFileStem>',
          types: ['class'],
        },
      ],
      optionalExports: [
        {
          name: '<PascalName>View',
          types: ['interface'],
        },
      ],
    };

    assert.equal(
      matchesDeclarationExpectation(
        analyzeDeclarations(
          parseModule(`
            export interface DailyRecordView {}
            export class DailyRecordModel {}
          `),
        ),
        expectation,
        'daily-record.model',
        {
          implementation: null,
          responsibility: null,
          segment: 'model',
        },
        [],
      ),
      true,
    );

    assert.equal(
      matchesDeclarationExpectation(
        analyzeDeclarations(
          parseModule(`
          export class FirstModel {}
          export class SecondModel {}
        `),
        ),
        {
          requiredExports: [
            {
              name: '<PascalName>Model',
              types: ['class'],
            },
          ],
        },
        'shared.navigation.model',
        {
          implementation: null,
          responsibility: null,
          segment: 'model',
        },
        [],
      ),
      false,
    );
  });
  it('can require at least one allowed export without requiring a specific name', () => {
    /** @type {import('../../types').DeclarationExpectation} */
    const expectation = {
      minimumExports: 1,
      requiredExports: [],
      optionalExports: [
        {
          name: '<PascalName>View',
          types: ['interface'],
        },
      ],
    };
    const matchedName = {
      implementation: null,
      responsibility: null,
      segment: 'model',
    };

    assert.equal(
      matchesDeclarationExpectation(
        analyzeDeclarations(parseModule('export interface DailyRecordView {}')),
        expectation,
        'daily-record.model',
        matchedName,
        [],
      ),
      true,
    );

    assert.equal(
      matchesDeclarationExpectation(
        analyzeDeclarations(parseModule('')),
        expectation,
        'daily-record.model',
        matchedName,
        [],
      ),
      false,
    );
  });
  it('describes named re-exports without deciding whether they are valid', () => {
    const program = parseModule(`
      export {
        DailyRecordPage,
        DailyRecordPresenter as Presenter
      } from '../internal';
    `);

    assert.deepEqual(analyzeDeclarations(program), {
      exported: [],
      reExports: [
        {
          source: '../internal',
          names: [
            {
              imported: 'DailyRecordPage',
              exported: 'DailyRecordPage',
            },
            {
              imported: 'DailyRecordPresenter',
              exported: 'Presenter',
            },
          ],
        },
      ],
      unsupportedExports: false,
    });
  });
  it('compares named re-exports with an exact public API expectation', () => {
    /** @type {import('../../types').ReExportExpectation} */
    const expectation = {
      statements: 'one',
      source: {
        exact: '../configuration/routes/<scope>.routes',
      },
      names: ['<UpperSnakeScope>_ROUTES'],
    };
    const matchedName = {
      implementation: null,
      responsibility: null,
      segment: 'routes',
    };
    const namingScope = ['daily-record', 'reporting'];

    assert.equal(
      matchesReExportExpectation(
        analyzeDeclarations(
          parseModule(`
            export { DAILY_RECORD_REPORTING_ROUTES }
              from '../configuration/routes/daily-record.reporting.routes';
          `),
        ),
        expectation,
        'routes',
        matchedName,
        namingScope,
      ),
      true,
    );

    assert.equal(
      matchesReExportExpectation(
        analyzeDeclarations(
          parseModule(`
            export { DAILY_RECORD_REPORTING_ROUTES as routes }
              from '../configuration/routes/daily-record.reporting.routes';
          `),
        ),
        expectation,
        'routes',
        matchedName,
        namingScope,
      ),
      false,
    );

    assert.equal(
      matchesReExportExpectation(
        analyzeDeclarations(
          parseModule(`
            export { DAILY_RECORD_REPORTING_ROUTES }
              from '../configuration/routes/wrong.routes';
          `),
        ),
        expectation,
        'routes',
        matchedName,
        namingScope,
      ),
      false,
    );
  });
  it('describes exported function type aliases', () => {
    const program = parseModule(`
      export type DailyRecordPresenterFactory =
        () => DailyRecordPresenter;

      export type InvalidPresenterFactory =
        (dependency: string) => Promise<DailyRecordPresenter>;
    `);

    assert.deepEqual(analyzeDeclarations(program), {
      exported: [
        {
          name: 'DailyRecordPresenterFactory',
          type: 'type',
          functionType: {
            parameterCount: 0,
            returnType: 'DailyRecordPresenter',
          },
        },
        {
          name: 'InvalidPresenterFactory',
          type: 'type',
          functionType: {
            parameterCount: 1,
            returnType: null,
          },
        },
      ],
      reExports: [],
      unsupportedExports: false,
    });
  });
  it('compares function type aliases with their expected signature', () => {
    /** @type {import('../../types').DeclarationExpectation} */
    const expectation = {
      requiredExports: [
        {
          name: '<PascalFileStem>Factory',
          types: ['type'],
          functionType: {
            parameterCount: 0,
            returnType: '<PascalFileStem>',
          },
        },
      ],
    };
    const matchedName = {
      implementation: null,
      responsibility: null,
      segment: 'presenter',
    };

    assert.equal(
      matchesDeclarationExpectation(
        analyzeDeclarations(
          parseModule(`
            export type DailyRecordPresenterFactory =
              () => DailyRecordPresenter;
          `),
        ),
        expectation,
        'daily-record.presenter',
        matchedName,
      ),
      true,
    );

    assert.equal(
      matchesDeclarationExpectation(
        analyzeDeclarations(
          parseModule(`
            export type DailyRecordPresenterFactory =
              (dependency: string) => Promise<DailyRecordPresenter>;
          `),
        ),
        expectation,
        'daily-record.presenter',
        matchedName,
      ),
      false,
    );
  });
  it('derives an expected export name from another exported declaration', () => {
    /** @type {import('../../types').DeclarationExpectation} */
    const expectation = {
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
    };
    const matchedName = {
      implementation: null,
      responsibility: null,
      segment: 'presenter',
    };

    assert.equal(
      matchesDeclarationExpectation(
        analyzeDeclarations(
          parseModule(`
            export class DailyRecordPresenter {}
            export type ApplicationDailyRecordPresenterFactory =
              () => DailyRecordPresenter;
            export const APPLICATION_DAILY_RECORD_PRESENTER_FACTORY =
              Symbol();
          `),
        ),
        expectation,
        'daily-record.presenter',
        matchedName,
      ),
      true,
    );

    assert.equal(
      matchesDeclarationExpectation(
        analyzeDeclarations(
          parseModule(`
            export class DailyRecordPresenter {}
            export type ApplicationDailyRecordPresenterFactory =
              () => DailyRecordPresenter;
            export const WRONG_PRESENTER_FACTORY = Symbol();
          `),
        ),
        expectation,
        'daily-record.presenter',
        matchedName,
      ),
      false,
    );
  });
  it('describes required top-level properties of exported data contracts', () => {
    const program = parseModule(`
      export interface CreateDailyRecordResult {
        readonly resultData: string;
        optionalNote?: string;
      }

      export type FindDailyRecordOutcome =
        | {
            readonly outcomeData: string;
          }
        | {
            readonly kind: 'not-found';
          };
    `);

    assert.deepEqual(analyzeDeclarations(program), {
      exported: [
        {
          name: 'CreateDailyRecordResult',
          type: 'interface',
          requiredProperties: ['resultData'],
        },
        {
          name: 'FindDailyRecordOutcome',
          type: 'type',
          requiredProperties: ['outcomeData', 'kind'],
        },
      ],
      reExports: [],
      unsupportedExports: false,
    });
  });
  it('compares required top-level properties', () => {
    /** @type {import('../../types').DeclarationExpectation} */
    const expectation = {
      requiredExports: [
        {
          name: 'CreateDailyRecordResult',
          types: ['interface', 'type'],
          requiredProperties: ['resultData'],
        },
      ],
    };
    const matchedName = {
      implementation: null,
      responsibility: 'create-daily-record',
      segment: 'use-case',
    };

    assert.equal(
      matchesDeclarationExpectation(
        analyzeDeclarations(
          parseModule(`
            export interface CreateDailyRecordResult {
              readonly resultData: string;
            }
          `),
        ),
        expectation,
        'create-daily-record.use-case',
        matchedName,
      ),
      true,
    );

    assert.equal(
      matchesDeclarationExpectation(
        analyzeDeclarations(
          parseModule(`
            export interface CreateDailyRecordResult {}
          `),
        ),
        expectation,
        'create-daily-record.use-case',
        matchedName,
      ),
      false,
    );
  });
  it('limits how many optional exports may be present', () => {
    /** @type {import('../../types').DeclarationExpectation} */
    const expectation = {
      requiredExports: [
        {
          name: 'CreateDailyRecordResult',
          types: ['interface', 'type'],
        },
        {
          name: 'CreateDailyRecordUseCase',
          types: ['interface'],
        },
      ],
      optionalExports: [
        {
          name: 'CreateDailyRecordCommand',
          types: ['interface', 'type'],
        },
        {
          name: 'CreateDailyRecordQuery',
          types: ['interface', 'type'],
        },
      ],
      maximumOptionalExports: 1,
    };
    const matchedName = {
      implementation: null,
      responsibility: 'create-daily-record',
      segment: 'use-case',
    };

    assert.equal(
      matchesDeclarationExpectation(
        analyzeDeclarations(
          parseModule(`
            export interface CreateDailyRecordCommand {}
            export interface CreateDailyRecordResult {}
            export interface CreateDailyRecordUseCase {}
          `),
        ),
        expectation,
        'create-daily-record.use-case',
        matchedName,
      ),
      true,
    );

    assert.equal(
      matchesDeclarationExpectation(
        analyzeDeclarations(
          parseModule(`
            export interface CreateDailyRecordCommand {}
            export interface CreateDailyRecordQuery {}
            export interface CreateDailyRecordResult {}
            export interface CreateDailyRecordUseCase {}
          `),
        ),
        expectation,
        'create-daily-record.use-case',
        matchedName,
      ),
      false,
    );
  });
  it('ignores statements that are not exports when analyzing exports', () => {
    const program = parseModule(`
      import { Injectable } from '@angular/core';

      interface InternalView {}
      const internalValue = {};

      export interface PublicContract {}
    `);

    assert.deepEqual(analyzeDeclarations(program), {
      exported: [
        {
          name: 'PublicContract',
          type: 'interface',
        },
      ],
      reExports: [],
      unsupportedExports: false,
    });
  });
  it('collects names of declarations whether or not they are exported', () => {
    const program = parseModule(`
      interface DailyRecordView {}
      export type CreateDailyRecordCommand = {};

      function createFixture() {
        class HiddenDailyRecordOutcome {}
      }

      enum CalendarResult {
        Found
      }
    `);

    assert.deepEqual(analyzeDeclaredNames(program), [
      'DailyRecordView',
      'CreateDailyRecordCommand',
      'HiddenDailyRecordOutcome',
      'CalendarResult',
    ]);
  });
});
