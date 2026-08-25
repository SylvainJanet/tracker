// @ts-check

'use strict';

const {
  matchesDeclarationExpectation,
  matchesReExportExpectation,
} = require('../tools/analyze-declarations');
const { matchExpectedFile } = require('../tools/file-names');
const { resolveDeclarationName } = require('../tools/names');
const { resolveDependency } = require('../tools/resolve-dependency');

/** @type {import('../types').RuleDefinition} */
module.exports = {
  messages: {
    invalidExports:
      '{{filePath}} does not follow its architectural declaration convention; ' +
      'expected exported declarations are [{{expectedExports}}].',
    invalidReExports:
      '{{filePath}} does not follow its architectural public API convention; ' +
      'expected {{expectedReExports}}.',
  },

  check(file, expected) {
    if (
      file.path.extension !== '.ts' ||
      file.path.relativePath === null ||
      file.path.isTest ||
      file.path.isAngularInlineTemplate ||
      expected.violation !== null
    ) {
      return [];
    }

    const expectedFileMatch = matchExpectedFile(
      expected.folder.files,
      file.path.fileName,
      expected.namingScope,
    );

    if (expectedFileMatch === null) {
      return [];
    }
    const reExports = expectedFileMatch.expectedFile.reExports;

    if (reExports !== undefined) {
      if (
        matchesReExportExpectation(
          file.declarations,
          reExports,
          file.path.stem,
          expectedFileMatch.name,
          expected.namingScope,
        ) &&
        reExportsStayWithinModule(file, expected, reExports)
      ) {
        return [];
      }

      return [
        {
          node: file.node,
          messageId: 'invalidReExports',
          data: {
            filePath: `src/app/${file.path.relativePath}`,
            expectedReExports: describeReExports(
              reExports,
              file.path.stem,
              expectedFileMatch.name,
              expected.namingScope,
            ),
          },
        },
      ];
    }

    const declarations = expectedFileMatch.expectedFile.declarations;

    if (
      declarations === undefined ||
      matchesDeclarationExpectation(
        file.declarations,
        declarations,
        file.path.stem,
        expectedFileMatch.name,
        expected.namingScope,
      )
    ) {
      return [];
    }

    const describedExports =
      declarations.requiredExports.length === 0
        ? (declarations.optionalExports ?? [])
        : declarations.requiredExports;

    const expectedExports = [
      ...describedExports.map((declaration) =>
        describeExpectedDeclaration(
          declaration,
          file.path.stem,
          expectedFileMatch.name,
          expected.namingScope,
        ),
      ),
      ...(declarations.derivedExports ?? []).map((declaration) =>
        describeDerivedExport(
          declaration,
          file.path.stem,
          expectedFileMatch.name,
          expected.namingScope,
        ),
      ),
    ].join(', ');

    return [
      {
        node: file.node,
        messageId: 'invalidExports',
        data: {
          filePath: `src/app/${file.path.relativePath}`,
          expectedExports,
        },
      },
    ];
  },
};

/**
 * @param {import('../types').ExpectedDeclaration} declaration
 * @param {string} fileStem
 * @param {import('../types').FileNameMatch} matchedName
 * @param {string[]} namingScope
 */
function describeExpectedDeclaration(declaration, fileStem, matchedName, namingScope) {
  const name = resolveDeclarationName(declaration.name, fileStem, matchedName, namingScope);
  const implementations = declaration.implements?.map((implementedType) =>
    resolveDeclarationName(implementedType, fileStem, matchedName, namingScope),
  );

  let description = `${declaration.types.join(' or ')} ${name}`;

  if (implementations !== undefined) {
    description += ` implementing exactly [${implementations.join(', ')}]`;
  }

  if (declaration.requiredProperties !== undefined) {
    description +=
      ` exposing required properties ` + `[${declaration.requiredProperties.join(', ')}]`;
  }

  if (declaration.functionType !== undefined) {
    const returnType = resolveDeclarationName(
      declaration.functionType.returnType,
      fileStem,
      matchedName,
      namingScope,
    );
    const parameters =
      declaration.functionType.parameterCount === 0
        ? 'a zero-argument function'
        : `a function with ${declaration.functionType.parameterCount} parameters`;

    description += ` as ${parameters} returning ${returnType}`;
  }

  return description;
}

/**
 * @param {import('../types').DerivedExportExpectation} declaration
 * @param {string} fileStem
 * @param {import('../types').FileNameMatch} matchedName
 * @param {string[]} namingScope
 */
function describeDerivedExport(declaration, fileStem, matchedName, namingScope) {
  const sourceName = resolveDeclarationName(
    declaration.nameFrom,
    fileStem,
    matchedName,
    namingScope,
  );
  const transformation =
    declaration.transform === 'upper-snake' ? 'UPPER_SNAKE_CASE' : declaration.transform;

  return (
    `${declaration.types.join(' or ')} named as the ` + `${transformation} form of ${sourceName}`
  );
}

/**
 * @param {import('../types').ReExportExpectation} expectation
 * @param {string} fileStem
 * @param {import('../types').FileNameMatch} matchedName
 * @param {string[]} namingScope
 */
function describeReExports(expectation, fileStem, matchedName, namingScope) {
  const quantity = expectation.statements === 'one' ? 'one' : 'one or more';
  const noun = expectation.statements === 'one' ? 're-export' : 're-exports';

  const names =
    expectation.names === 'any'
      ? 'any names'
      : `[${expectation.names
          .map((name) => resolveDeclarationName(name, fileStem, matchedName, namingScope))
          .join(', ')}]`;

  const source =
    'exact' in expectation.source ? expectation.source.exact : expectation.source.prefix;
  const resolvedSource = source.replaceAll('<scope>', namingScope.join('.'));
  const describedSource =
    'exact' in expectation.source ? resolvedSource : `paths starting with ${resolvedSource}`;

  return `${quantity} named, unaliased ${noun} of ${names} ` + `from ${describedSource}`;
}

/**
 * @param {import('../types').AnalyzedFile} file
 * @param {import('../types').ArchitectureMatch} expected
 * @param {import('../types').ReExportExpectation} expectation
 */
function reExportsStayWithinModule(file, expected, expectation) {
  if ('exact' in expectation.source) {
    return true;
  }

  const moduleRoot = expected.module?.root;

  if (moduleRoot === undefined) {
    return false;
  }

  return file.declarations.reExports.every((reExport) => {
    const target = resolveDependency(file.path, { source: reExport.source });

    return (
      target !== null && (target.value === moduleRoot || target.value.startsWith(`${moduleRoot}/`))
    );
  });
}
