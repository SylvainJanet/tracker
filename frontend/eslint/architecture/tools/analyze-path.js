// @ts-check

'use strict';

const ANGULAR_INLINE_TEMPLATE = /\.ts\/\d+_inline-template-[^/]+-\d+\.component\.html$/;
const FILE_EXTENSIONS = ['.spec.ts', '.ts', '.html', '.scss'];

/**
 * @param {string} filePath
 * @returns {import('../types').FilePath}
 */
function analyzePath(filePath) {
  const value = filePath.replaceAll('\\', '/');
  const allSegments = value.split('/');
  const appIndex = allSegments.findIndex(
    (segment, index) => segment === 'src' && allSegments[index + 1] === 'app',
  );
  const relativeSegments = appIndex === -1 ? null : allSegments.slice(appIndex + 2);
  const fileName = allSegments.at(-1) ?? '';
  const extension = FILE_EXTENSIONS.find((candidate) => fileName.endsWith(candidate)) ?? '';
  const stem = extension === '' ? fileName : fileName.slice(0, -extension.length);

  return {
    value,
    relativePath: relativeSegments?.join('/') ?? null,
    folders: relativeSegments?.slice(0, -1) ?? [],
    fileName,
    stem,
    extension,
    nameParts: stem === '' ? [] : stem.split('.'),
    isTest: extension === '.spec.ts',
    isAngularInlineTemplate: ANGULAR_INLINE_TEMPLATE.test(value),
  };
}

module.exports = { analyzePath };
