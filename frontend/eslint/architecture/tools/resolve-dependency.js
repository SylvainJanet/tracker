// @ts-check

'use strict';

const path = require('node:path');

const { analyzePath } = require('./analyze-path');

/**
 * @param {import('../types').FilePath} file
 * @param {import('../types').AnalyzedDependency} dependency
 * @returns {import('../types').FilePath | null}
 */
function resolveDependency(file, dependency) {
  if (file.relativePath === null || !dependency.source.startsWith('.')) {
    return null;
  }

  const sourcePath = `src/app/${file.relativePath}`;
  const resolvedPath = path.posix.normalize(
    path.posix.join(path.posix.dirname(sourcePath), dependency.source),
  );

  return analyzePath(resolvedPath);
}

module.exports = { resolveDependency };
