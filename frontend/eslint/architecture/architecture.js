// @ts-check

'use strict';

const fileExpectations = require('./file-expectations');

const PRESENTATION_DECLARATION_SUFFIXES = ['Intent', 'View'];
const INBOUND_PORT_DECLARATION_SUFFIXES = ['Command', 'Query', 'Result'];
const OUTBOUND_PORT_DECLARATION_SUFFIXES = ['Criteria', 'Instruction', 'Outcome'];
const RESERVED_DECLARATION_SUFFIXES = [
  ...PRESENTATION_DECLARATION_SUFFIXES,
  ...INBOUND_PORT_DECLARATION_SUFFIXES,
  ...OUTBOUND_PORT_DECLARATION_SUFFIXES,
];

const DOMAIN_DEPENDENCY_LAYERS = ['domain'];
const PORT_DEPENDENCY_LAYERS = ['domain'];
const SERVICE_DEPENDENCY_LAYERS = ['domain', 'application/port/in', 'application/port/out'];
const INBOUND_ADAPTER_DEPENDENCY_LAYERS = ['domain', 'application/port/in', 'adapter/in'];
const OUTBOUND_ADAPTER_DEPENDENCY_LAYERS = ['domain', 'application/port/out', 'adapter/out'];
const CONFIGURATION_DEPENDENCY_LAYERS = [
  'domain',
  'application/port/in',
  'application/port/out',
  'application/service',
  'adapter/in',
  'adapter/out',
  'configuration',
];

const MODULE_CONFIGURATION_APPLICATION_ROOT_DEPENDENCY_LAYERS = ['configuration'];

const SHELL_UI_DEPENDENCY_LAYERS = ['model', 'page', 'presenter'];
const SHELL_CONFIGURATION_DEPENDENCY_LAYERS = [...SHELL_UI_DEPENDENCY_LAYERS, 'configuration'];
const SHELL_API_DEPENDENCY_LAYERS = ['api', 'configuration', 'page'];
const APPLICATION_CONFIGURATION_DEPENDENCY_LAYERS = ['configuration'];
const APPLICATION_COMPOSITION_DEPENDENCY_LAYERS = ['configuration', 'composition'];

const CONTEXT_API_DEPENDENCY_LAYERS = ['api', 'configuration'];
const SHARED_API_DEPENDENCY_LAYERS = ['api', ...CONFIGURATION_DEPENDENCY_LAYERS];

/** @type {import('./types').ArchitectureFolder} */
const webContent = {
  files: [],
  metadata: {
    allowedDeclarationSuffixes: PRESENTATION_DECLARATION_SUFFIXES,
  },
  folders: {
    model: {
      files: fileExpectations.web.exactModel,
    },
    page: {
      files: fileExpectations.web.page,
    },
    presenter: {
      files: fileExpectations.web.presenter,

      folders: {
        mapper: {
          files: fileExpectations.web.mapper,
        },
      },
    },
  },

  anyFolder: {
    label: '<subcontext>',
    folder: 'same',
    metadata: {
      namingSegment: true,
    },
  },
};

/**
 * @param {import('./types').ArchitectureFolder} content
 * @returns {import('./types').ArchitectureFolder}
 */
function createWeb(content) {
  return {
    files: [],
    anyFolder: {
      label: '<subcontext>',
      folder: content,
      metadata: {
        namingSegment: true,
      },
      excludedNames: Object.keys(content.folders ?? {}),
    },
  };
}

const contextWeb = createWeb(webContent);

const sharedWebContent = {
  ...webContent,
  folders: {
    ...webContent.folders,
    model: {
      files: fileExpectations.web.sharedModel,
    },
  },
};

const sharedWeb = createWeb(sharedWebContent);

/**
 * @param {import('./types').ExpectedFile[]} contractFiles
 * @returns {import('./types').ArchitectureFolder}
 */
function createHttpAdapter(contractFiles) {
  return {
    files: fileExpectations.boundedModule.httpGateway,

    folders: {
      contract: {
        files: contractFiles,
      },
      errors: {
        files: fileExpectations.boundedModule.httpError,

        folders: {
          translator: {
            files: fileExpectations.boundedModule.httpErrorTranslator,
          },
        },
      },
    },
  };
}

/**
 * @param {{
 *   apiFiles: import('./types').ExpectedFile[],
 *   contractFiles: import('./types').ExpectedFile[],
 *   configurationFiles: {
 *     navigation: import('./types').ExpectedFile[],
 *     providers: import('./types').ExpectedFile[],
 *     routes: import('./types').ExpectedFile[]
 *   },
 *   inboundWeb: import('./types').ArchitectureFolder,
 *   apiDependencyLayers: string[],
 *   metadata?: import('./types').FolderMetadata
 * }} options
 * @returns {import('./types').ArchitectureFolder}
 */
function createBoundedModule({
  apiFiles,
  contractFiles,
  configurationFiles,
  inboundWeb,
  apiDependencyLayers,
  metadata = {},
}) {
  return {
    files: [],
    metadata,
    folders: {
      api: {
        files: apiFiles,
        metadata: {
          layer: 'api',
          visibility: 'public-api',
          allowedDependencyLayers: apiDependencyLayers,
        },
      },
      domain: {
        files: fileExpectations.boundedModule.domain,
        metadata: {
          layer: 'domain',
          allowedDependencyLayers: DOMAIN_DEPENDENCY_LAYERS,
        },
      },
      application: {
        files: [],
        folders: {
          port: {
            files: [],
            folders: {
              in: {
                files: fileExpectations.boundedModule.inboundPort,
                metadata: {
                  layer: 'application/port/in',
                  allowedDeclarationSuffixes: INBOUND_PORT_DECLARATION_SUFFIXES,
                  allowedDependencyLayers: PORT_DEPENDENCY_LAYERS,
                },
              },
              out: {
                files: fileExpectations.boundedModule.outboundPort,
                metadata: {
                  layer: 'application/port/out',
                  allowedDeclarationSuffixes: OUTBOUND_PORT_DECLARATION_SUFFIXES,
                  allowedDependencyLayers: PORT_DEPENDENCY_LAYERS,
                },
              },
            },
          },
          service: {
            files: fileExpectations.boundedModule.service,

            metadata: {
              layer: 'application/service',
              allowedDependencyLayers: SERVICE_DEPENDENCY_LAYERS,
            },
          },
        },
      },
      adapter: {
        files: [],
        folders: {
          in: {
            files: [],
            metadata: {
              layer: 'adapter/in',
              allowedDependencyLayers: INBOUND_ADAPTER_DEPENDENCY_LAYERS,
            },
            folders: {
              web: inboundWeb,
            },
          },
          out: {
            files: [],
            metadata: {
              layer: 'adapter/out',
              allowedDependencyLayers: OUTBOUND_ADAPTER_DEPENDENCY_LAYERS,
              allowsCrossContextDependencies: true,
            },

            folders: {
              http: createHttpAdapter(contractFiles),
              time: {
                files: fileExpectations.boundedModule.timeProvider,
              },
            },
          },
        },
      },
      configuration: {
        files: [],
        metadata: {
          layer: 'configuration',
          allowedDependencyLayers: CONFIGURATION_DEPENDENCY_LAYERS,
          allowedApplicationRootDependencyLayers:
            MODULE_CONFIGURATION_APPLICATION_ROOT_DEPENDENCY_LAYERS,
        },
        folders: {
          navigation: {
            files: configurationFiles.navigation,
          },
          providers: {
            files: configurationFiles.providers,
          },
          routes: {
            files: configurationFiles.routes,
          },
        },
      },
    },
    anyFolder: {
      label: '<subcontext>',
      folder: 'same',
      metadata: {
        moduleSegment: true,
      },
    },
  };
}

const context = createBoundedModule({
  apiFiles: fileExpectations.context.api,
  contractFiles: fileExpectations.context.httpContracts,
  configurationFiles: fileExpectations.context.configuration,
  inboundWeb: contextWeb,
  apiDependencyLayers: CONTEXT_API_DEPENDENCY_LAYERS,
});

const shared = createBoundedModule({
  apiFiles: fileExpectations.shared.api,
  contractFiles: fileExpectations.shared.httpContracts,
  configurationFiles: fileExpectations.shared.configuration,
  inboundWeb: sharedWeb,
  apiDependencyLayers: SHARED_API_DEPENDENCY_LAYERS,
  metadata: {
    moduleKind: 'shared',
    moduleSegment: true,
  },
});

const reservedShellModuleNames = [
  ...Object.keys(context.folders ?? {}),
  ...Object.keys(webContent.folders ?? {}),
];

/** @type {import('./types').ArchitectureFolder} */
const shellModule = {
  files: [],
  folders: {
    api: {
      files: fileExpectations.shell.api,

      metadata: {
        layer: 'api',
        visibility: 'public-api',
        allowedDependencyLayers: SHELL_API_DEPENDENCY_LAYERS,
      },
    },
    configuration: {
      files: [],
      metadata: {
        layer: 'configuration',
        allowedDependencyLayers: SHELL_CONFIGURATION_DEPENDENCY_LAYERS,
        allowedApplicationRootDependencyLayers:
          MODULE_CONFIGURATION_APPLICATION_ROOT_DEPENDENCY_LAYERS,
      },
      folders: {
        providers: {
          files: fileExpectations.shell.configuration.providers,
        },
        routes: {
          files: fileExpectations.shell.configuration.routes,
        },
        sections: {
          files: fileExpectations.shell.configuration.sections,
        },
      },
    },
    model: {
      files: fileExpectations.web.exactModel,
      metadata: {
        layer: 'model',
        allowedDeclarationSuffixes: PRESENTATION_DECLARATION_SUFFIXES,
        allowedDependencyLayers: SHELL_UI_DEPENDENCY_LAYERS,
      },
    },

    page: {
      files: fileExpectations.web.page,
      metadata: {
        layer: 'page',
        allowedDeclarationSuffixes: PRESENTATION_DECLARATION_SUFFIXES,
        allowedDependencyLayers: SHELL_UI_DEPENDENCY_LAYERS,
      },
    },
    presenter: {
      files: fileExpectations.web.presenter,
      metadata: {
        layer: 'presenter',
        allowedDeclarationSuffixes: PRESENTATION_DECLARATION_SUFFIXES,
        allowedDependencyLayers: SHELL_UI_DEPENDENCY_LAYERS,
      },
      folders: {
        mapper: {
          files: fileExpectations.web.mapper,
        },
      },
    },
  },
  anyFolder: {
    label: '<module>',
    folder: 'same',
    metadata: {
      moduleSegment: true,
    },
    excludedNames: reservedShellModuleNames,
  },
};

/** @type {import('./types').ArchitectureFolder} */
const architecture = {
  files: [],
  metadata: {
    moduleKind: 'application',
    visibility: 'internal',
    reservedDeclarationSuffixes: RESERVED_DECLARATION_SUFFIXES,
  },
  folders: {
    configuration: {
      files: fileExpectations.application.configuration,

      metadata: {
        layer: 'configuration',
        allowedDependencyLayers: APPLICATION_CONFIGURATION_DEPENDENCY_LAYERS,
      },
    },
    contexts: {
      files: [],
      anyFolder: {
        label: '<context>',
        folder: context,
        metadata: {
          moduleKind: 'context',
          moduleSegment: true,
        },
      },
    },
    shared,
    shell: {
      files: [],
      anyFolder: {
        label: '<module>',
        folder: shellModule,
        metadata: {
          moduleKind: 'shell',
          moduleSegment: true,
        },
        excludedNames: reservedShellModuleNames,
      },
    },

    composition: {
      files: fileExpectations.application.composition,
      metadata: {
        layer: 'composition',
        allowedDependencyLayers: APPLICATION_COMPOSITION_DEPENDENCY_LAYERS,
      },
    },
  },
};

module.exports = architecture;
