// @ts-check

'use strict';

const { createFileNameConvention, matchExpectedFile } = require('../tools/file-names');

/** @type {import('../types').RuleDefinition} */
module.exports = {
  messages: {
    invalidFileName:
      '{{filePath}} does not follow the allowed frontend filename conventions: ' +
      'files in {{directoryPath}} must be named one of [{{allowedFileNames}}].',
  },

  check(file, expected) {
    if (
      file.path.relativePath === null ||
      file.path.isAngularInlineTemplate ||
      expected.violation !== null
    ) {
      return [];
    }

    const matchingFile = matchExpectedFile(
      expected.folder.files,
      file.path.fileName,
      expected.namingScope,
    );

    if (matchingFile !== null) {
      return [];
    }

    const allowedFileNames = expected.folder.files.flatMap(
      (expectedFile) =>
        createFileNameConvention(expectedFile.naming, expected.namingScope).allowedFileNames,
    );

    return [
      {
        node: file.node,
        messageId: 'invalidFileName',
        data: {
          filePath: `src/app/${file.path.relativePath}`,
          directoryPath: ['src', 'app', ...file.path.folders].join('/'),
          allowedFileNames: allowedFileNames.join(', '),
        },
      },
    ];
  },
};
