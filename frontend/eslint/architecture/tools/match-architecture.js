// @ts-check

'use strict';

/**
 * @param {import('../types').ArchitectureFolder} architecture
 * @param {import('../types').FilePath} path
 * @returns {import('../types').ArchitectureMatch}
 */
function matchArchitecture(architecture, path) {
  if (path.relativePath === null) {
    return {
      folder: architecture,
      metadata: {},
      module: null,
      layer: null,
      namingScope: [],
      violation: null,
    };
  }

  let folder = architecture;
  let metadata = { ...architecture.metadata };
  const matchedFolders = [];
  const namingScope = [];

  /** @type {import('../types').ArchitectureModule | null} */
  let matchedModule =
    metadata.moduleKind === undefined
      ? null
      : {
          kind: metadata.moduleKind,
          name: metadata.moduleKind,
          root: 'src/app',
          scope: [],
        };

  let layer = metadata.layer ?? null;
  /** @type {import('../types').StructureViolation | null} */
  let violation = null;

  for (const segment of path.folders) {
    const fixedFolder = folder.folders?.[segment];
    const dynamicFolder = folder.anyFolder;

    /** @type {import('../types').ArchitectureFolder | undefined} */
    let nextFolder;
    /** @type {import('../types').FolderMetadata} */
    let nextMetadata;

    if (fixedFolder !== undefined) {
      nextFolder = fixedFolder;
      nextMetadata = { ...fixedFolder.metadata };
    } else if (dynamicFolder !== undefined && !dynamicFolder.excludedNames?.includes(segment)) {
      nextFolder = dynamicFolder.folder === 'same' ? folder : dynamicFolder.folder;
      nextMetadata = {
        ...nextFolder.metadata,
        ...dynamicFolder.metadata,
      };
    } else {
      violation = structureViolation(folder, matchedFolders);
      break;
    }

    folder = nextFolder;
    matchedFolders.push(segment);
    metadata = { ...metadata, ...nextMetadata };

    const directoryPath = ['src', 'app', ...matchedFolders].join('/');

    if (nextMetadata.moduleKind !== undefined && nextMetadata.moduleKind !== matchedModule?.kind) {
      matchedModule = {
        kind: nextMetadata.moduleKind,
        name: nextMetadata.moduleKind,
        root: directoryPath,
        scope: [],
      };
    }

    if (nextMetadata.moduleSegment === true && matchedModule !== null) {
      matchedModule = {
        ...matchedModule,
        name: segment,
        root: directoryPath,
        scope: [...matchedModule.scope, segment],
      };
    }

    if (nextMetadata.moduleSegment === true || nextMetadata.namingSegment === true) {
      namingScope.push(segment);
    }

    if (nextMetadata.layer !== undefined) {
      layer = nextMetadata.layer;
    }
  }

  if (violation === null && folder.files.length === 0) {
    violation = structureViolation(folder, matchedFolders);
  }

  return {
    folder,
    metadata,
    module: matchedModule,
    layer,
    namingScope,
    violation,
  };
}

/**
 * @param {import('../types').ArchitectureFolder} folder
 * @param {string[]} matchedFolders
 * @returns {import('../types').StructureViolation}
 */
function structureViolation(folder, matchedFolders) {
  const allowedFolders = Object.keys(folder.folders ?? {});

  if (folder.anyFolder !== undefined) {
    allowedFolders.push(folder.anyFolder.label);
  }

  return {
    directoryPath: ['src', 'app', ...matchedFolders].join('/'),
    allowsFiles: folder.files.length > 0,
    allowedFolders,
  };
}

module.exports = { matchArchitecture };
