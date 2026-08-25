// @ts-check

'use strict';

const DECLARATION_NAME_FROM_FILE_PARTS =
  '<PascalImplementation><PascalResponsibility><PascalSegment>';

/** @type {import('./types').ExpectedDeclaration[]} */
const MODEL_AUXILIARY_DECLARATIONS = [
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

const declarations = /** @satisfies {Record<string, import('./types').DeclarationExpectation>} */ ({
  singleClass: {
    requiredExports: [
      {
        name: '<PascalFileStem>',
        types: ['class'],
      },
    ],
  },

  presenter: {
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
  },

  domain: {
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
  },

  service: {
    requiredExports: [
      {
        name: '<PascalFileStem>',
        types: ['class'],
        implements: ['<PascalResponsibility>UseCase'],
      },
    ],
  },

  inboundPort: {
    requiredExports: [
      {
        name: '<PascalResponsibility>Result',
        types: ['interface', 'type'],
        requiredProperties: ['resultData'],
      },
      {
        name: DECLARATION_NAME_FROM_FILE_PARTS,
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

  outboundPort: {
    requiredExports: [
      {
        name: DECLARATION_NAME_FROM_FILE_PARTS,
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
  },

  outboundAdapter: {
    requiredExports: [
      {
        name: DECLARATION_NAME_FROM_FILE_PARTS,
        types: ['class'],
        implements: ['<PascalResponsibility><PascalSegment>'],
      },
    ],
  },

  contextNavigation: {
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

  contextRoutes: {
    requiredExports: [
      {
        name: '<UpperSnakeScope>_ROUTES',
        types: ['const'],
      },
    ],
  },

  exactModel: {
    requiredExports: [
      {
        name: '<PascalFileStem>',
        types: ['class'],
      },
    ],
    optionalExports: MODEL_AUXILIARY_DECLARATIONS,
  },

  sharedModel: {
    requiredExports: [
      {
        name: '<PascalName><PascalLocalScope>Model',
        types: ['class'],
      },
    ],
    optionalExports: MODEL_AUXILIARY_DECLARATIONS,
  },

  contextHttpContract: {
    minimumExports: 1,
    requiredExports: [],
    optionalExports: [
      {
        name: '<PascalScope>Url',
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
    ],
  },

  httpErrorTranslator: {
    minimumExports: 1,
    requiredExports: [],
    optionalExports: [
      {
        name: 'translate<OptionalPascalName><PascalScope>HttpError',
        types: ['function'],
      },
    ],
  },

  contextProvider: {
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

  shellProvider: {
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
  },

  sharedHttpDto: {
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

  sharedNavigation: {
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

  shellRoutes: {
    requiredExports: [
      {
        name: 'routes',
        types: ['const'],
      },
    ],
  },

  shellSections: {
    requiredExports: [
      {
        name: '<UpperSnakeScope>_SECTIONS',
        types: ['const'],
      },
    ],
  },

  applicationConfig: {
    requiredExports: [
      {
        name: 'appConfig',
        types: ['const'],
      },
    ],
  },

  applicationContextsConfig: {
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
  },
});

module.exports = declarations;
