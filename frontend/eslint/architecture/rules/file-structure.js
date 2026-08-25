// @ts-check

'use strict';

/** @type {import('../types').RuleDefinition} */
module.exports = {
  messages: {
    invalidStructure:
      '{{filePath}} does not follow the allowed frontend folder structure: ' +
      'the folder {{directoryPath}} {{directFilesPolicy}}; ' +
      'allowed folders are [{{allowedFolders}}].',
  },

  check(file, expected) {
    const violation = expected.violation;

    if (
      file.path.relativePath === null ||
      file.path.isAngularInlineTemplate ||
      violation === null
    ) {
      return [];
    }

    return [
      {
        node: file.node,
        messageId: 'invalidStructure',
        data: {
          filePath: `src/app/${file.path.relativePath}`,
          directoryPath: violation.directoryPath,
          directFilesPolicy: violation.allowsFiles
            ? 'allows direct files'
            : 'does not allow direct files',
          allowedFolders: violation.allowedFolders.join(', '),
        },
      },
    ];
  },
};
