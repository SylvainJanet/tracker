// @ts-check

'use strict';

const { matchesDeclarationName, resolveDeclarationName, toUpperSnakeCase } = require('./names');
const { walkNodes } = require('./walk-nodes');

/**
 * @param {import('@typescript-eslint/utils').TSESTree.Program} program
 * @returns {string[]}
 */
function analyzeDeclaredNames(program) {
  /** @type {string[]} */
  const names = [];

  walkNodes(program, (node) => {
    switch (node.type) {
      case 'ClassDeclaration':
        if (node.id !== null) {
          names.push(node.id.name);
        }
        break;

      case 'TSInterfaceDeclaration':
      case 'TSTypeAliasDeclaration':
      case 'TSEnumDeclaration':
        names.push(node.id.name);
        break;
    }
  });

  return names;
}

/**
 * @param {import('@typescript-eslint/utils').TSESTree.Program} program
 * @returns {import('../types').DeclarationAnalysis}
 */
function analyzeDeclarations(program) {
  const exported = [];
  const reExports = [];
  let unsupportedExports = false;

  for (const statement of program.body) {
    if (!statement.type.startsWith('Export')) {
      continue;
    }

    if (statement.type !== 'ExportNamedDeclaration') {
      unsupportedExports = true;
      continue;
    }

    if (statement.declaration === null) {
      const reExport = describeReExport(statement);

      if (reExport === null) {
        unsupportedExports = true;
      } else {
        reExports.push(reExport);
      }

      continue;
    }

    const declaration = describeDeclaration(statement.declaration);

    if (declaration === null) {
      unsupportedExports = true;
    } else {
      exported.push(declaration);
    }
  }

  return {
    exported,
    reExports,
    unsupportedExports,
  };
}

/**
 * @param {import('@typescript-eslint/utils').TSESTree.ExportNamedDeclaration} statement
 * @returns {import('../types').ReExport | null}
 */
function describeReExport(statement) {
  if (
    statement.source === null ||
    typeof statement.source.value !== 'string' ||
    statement.specifiers.length === 0
  ) {
    return null;
  }

  const names = [];

  for (const specifier of statement.specifiers) {
    if (
      specifier.type !== 'ExportSpecifier' ||
      specifier.local.type !== 'Identifier' ||
      specifier.exported.type !== 'Identifier'
    ) {
      return null;
    }

    names.push({
      imported: specifier.local.name,
      exported: specifier.exported.name,
    });
  }

  return {
    source: statement.source.value,
    names,
  };
}

/**
 * @param {import('@typescript-eslint/utils').TSESTree.NamedExportDeclarations} declaration
 * @returns {import('../types').ExportedDeclaration | null}
 */
function describeDeclaration(declaration) {
  switch (declaration.type) {
    case 'ClassDeclaration':
      return declaration.id === null
        ? null
        : {
            name: declaration.id.name,
            type: 'class',
            implements: describeImplementedTypes(declaration.implements),
          };

    case 'TSInterfaceDeclaration': {
      const requiredProperties = describeRequiredProperties(declaration.body.body);
      const description = {
        name: declaration.id.name,
        type: /** @type {const} */ ('interface'),
      };

      return requiredProperties.length === 0 ? description : { ...description, requiredProperties };
    }

    case 'TSTypeAliasDeclaration': {
      const functionType = describeFunctionType(declaration.typeAnnotation);
      const requiredProperties = describeTypeRequiredProperties(declaration.typeAnnotation);
      const description = {
        name: declaration.id.name,
        type: /** @type {const} */ ('type'),
      };

      return {
        ...description,
        ...(functionType === undefined ? {} : { functionType }),
        ...(requiredProperties.length === 0 ? {} : { requiredProperties }),
      };
    }

    case 'FunctionDeclaration':
      return declaration.id === null ? null : { name: declaration.id.name, type: 'function' };

    case 'VariableDeclaration': {
      const [declarator] = declaration.declarations;

      return declaration.kind === 'const' &&
        declaration.declarations.length === 1 &&
        declarator?.id.type === 'Identifier'
        ? { name: declarator.id.name, type: 'const' }
        : null;
    }

    default:
      return null;
  }
}

/**
 * @param {import('@typescript-eslint/utils').TSESTree.TypeElement[]} members
 * @returns {string[]}
 */
function describeRequiredProperties(members) {
  return members.flatMap((member) =>
    member.type === 'TSPropertySignature' &&
    member.computed !== true &&
    member.optional !== true &&
    member.key.type === 'Identifier'
      ? [member.key.name]
      : [],
  );
}

/**
 * @param {import('@typescript-eslint/utils').TSESTree.TypeNode} type
 * @returns {string[]}
 */
function describeTypeRequiredProperties(type) {
  if (type.type === 'TSTypeLiteral') {
    return describeRequiredProperties(type.members);
  }

  if (type.type !== 'TSUnionType') {
    return [];
  }

  return [
    ...new Set(
      type.types.flatMap((alternative) =>
        alternative.type === 'TSTypeLiteral' ? describeRequiredProperties(alternative.members) : [],
      ),
    ),
  ];
}

/**
 * @param {import('@typescript-eslint/utils').TSESTree.TypeNode} type
 * @returns {import('../types').FunctionTypeAnalysis | undefined}
 */
function describeFunctionType(type) {
  if (type.type !== 'TSFunctionType') {
    return undefined;
  }

  const returnedType = type.returnType?.typeAnnotation;
  const returnType =
    returnedType?.type === 'TSTypeReference' &&
    returnedType.typeName.type === 'Identifier' &&
    returnedType.typeArguments === undefined
      ? returnedType.typeName.name
      : null;

  return {
    parameterCount: type.params.length,
    returnType,
  };
}

/**
 * @param {import('@typescript-eslint/utils').TSESTree.TSClassImplements[]} implementations
 * @returns {string[] | null}
 */
function describeImplementedTypes(implementations) {
  const names = [];

  for (const implementation of implementations) {
    if (
      implementation.expression.type !== 'Identifier' ||
      implementation.typeArguments !== undefined
    ) {
      return null;
    }

    names.push(implementation.expression.name);
  }

  return names;
}

/**
 * @param {import('../types').DeclarationAnalysis} analysis
 * @param {import('../types').DeclarationExpectation} expectation
 * @param {string} fileStem
 * @param {import('../types').FileNameMatch} matchedName
 * @param {string[]} [namingScope=[]]
 */
function matchesDeclarationExpectation(
  analysis,
  expectation,
  fileStem,
  matchedName,
  namingScope = [],
) {
  if (analysis.unsupportedExports || analysis.reExports.length > 0) {
    return false;
  }

  if (analysis.exported.length < (expectation.minimumExports ?? 0)) {
    return false;
  }

  const actualNames = analysis.exported.map((declaration) => declaration.name);

  if (new Set(actualNames).size !== actualNames.length) {
    return false;
  }

  const derivedExports = resolveDerivedExports(
    analysis.exported,
    expectation.derivedExports ?? [],
    fileStem,
    matchedName,
    namingScope,
  );

  if (derivedExports === null) {
    return false;
  }

  const requiredExports = [...expectation.requiredExports, ...derivedExports];
  const optionalExports = expectation.optionalExports ?? [];
  const allowedExports = [...requiredExports, ...optionalExports];

  const hasEveryRequiredExport = requiredExports.every(
    (required) =>
      analysis.exported.filter((actual) =>
        matchesDeclaration(actual, required, fileStem, matchedName, namingScope),
      ).length === 1,
  );

  const hasOnlyAllowedExports = analysis.exported.every((actual) =>
    allowedExports.some((allowed) =>
      matchesDeclaration(actual, allowed, fileStem, matchedName, namingScope),
    ),
  );

  const optionalExportCount = analysis.exported.filter((actual) =>
    optionalExports.some((optional) =>
      matchesDeclaration(actual, optional, fileStem, matchedName, namingScope),
    ),
  ).length;

  const hasAllowedNumberOfOptionalExports =
    expectation.maximumOptionalExports === undefined ||
    optionalExportCount <= expectation.maximumOptionalExports;

  return hasEveryRequiredExport && hasOnlyAllowedExports && hasAllowedNumberOfOptionalExports;
}

/**
 * @param {import('../types').ExportedDeclaration[]} exported
 * @param {import('../types').DerivedExportExpectation[]} expectations
 * @param {string} fileStem
 * @param {import('../types').FileNameMatch} matchedName
 * @param {string[]} namingScope
 * @returns {import('../types').ExpectedDeclaration[] | null}
 */
function resolveDerivedExports(exported, expectations, fileStem, matchedName, namingScope) {
  const resolved = [];

  for (const expectation of expectations) {
    const sources = exported.filter((declaration) =>
      matchesDeclarationName(
        declaration.name,
        expectation.nameFrom,
        fileStem,
        matchedName,
        namingScope,
      ),
    );
    const source = sources.at(0);

    if (sources.length !== 1 || source === undefined) {
      return null;
    }

    resolved.push({
      name: expectation.transform === 'upper-snake' ? toUpperSnakeCase(source.name) : source.name,
      types: expectation.types,
    });
  }

  return resolved;
}

/**
 * @param {import('../types').ExportedDeclaration} actual
 * @param {import('../types').ExpectedDeclaration} expected
 * @param {string} fileStem
 * @param {import('../types').FileNameMatch} matchedName
 * @param {string[]} namingScope
 */
function matchesDeclaration(actual, expected, fileStem, matchedName, namingScope) {
  if (
    !matchesDeclarationName(actual.name, expected.name, fileStem, matchedName, namingScope) ||
    !expected.types.includes(actual.type)
  ) {
    return false;
  }

  if (
    expected?.requiredProperties !== undefined &&
    !expected.requiredProperties.every((property) => actual.requiredProperties?.includes(property))
  ) {
    return false;
  }

  if (expected.functionType !== undefined) {
    const expectedReturnType = resolveDeclarationName(
      expected.functionType.returnType,
      fileStem,
      matchedName,
      namingScope,
    );

    if (
      actual.functionType === undefined ||
      actual.functionType.parameterCount !== expected.functionType.parameterCount ||
      actual.functionType.returnType !== expectedReturnType
    ) {
      return false;
    }
  }

  if (expected.implements === undefined) {
    return true;
  }

  if (actual.implements === undefined || actual.implements === null) {
    return false;
  }

  const expectedImplementations = expected.implements.map((name) =>
    resolveDeclarationName(name, fileStem, matchedName, namingScope),
  );

  return (
    actual.implements.length === expectedImplementations.length &&
    actual.implements.every((name) => expectedImplementations.includes(name))
  );
}

/**
 * @param {import('../types').DeclarationAnalysis} analysis
 * @param {import('../types').ReExportExpectation} expectation
 * @param {string} fileStem
 * @param {import('../types').FileNameMatch} matchedName
 * @param {string[]} namingScope
 */
function matchesReExportExpectation(analysis, expectation, fileStem, matchedName, namingScope) {
  if (
    analysis.unsupportedExports ||
    analysis.exported.length > 0 ||
    analysis.reExports.length === 0 ||
    (expectation.statements === 'one' && analysis.reExports.length !== 1)
  ) {
    return false;
  }

  const expectedSource =
    'exact' in expectation.source ? expectation.source.exact : expectation.source.prefix;
  const resolvedSource = expectedSource.replaceAll('<scope>', namingScope.join('.'));

  const sourcesMatch = analysis.reExports.every((reExport) =>
    'exact' in expectation.source
      ? reExport.source === resolvedSource
      : reExport.source.startsWith(resolvedSource),
  );

  if (!sourcesMatch) {
    return false;
  }

  const names = analysis.reExports.flatMap((reExport) => reExport.names);

  if (
    names.some((name) => name.imported !== name.exported) ||
    new Set(names.map((name) => name.exported)).size !== names.length
  ) {
    return false;
  }

  if (expectation.names === 'any') {
    return true;
  }

  const expectedNames = expectation.names.map((name) =>
    resolveDeclarationName(name, fileStem, matchedName, namingScope),
  );
  const exportedNames = names.map((name) => name.exported);

  return (
    exportedNames.length === expectedNames.length &&
    exportedNames.every((name) => expectedNames.includes(name))
  );
}

module.exports = {
  analyzeDeclarations,
  matchesDeclarationExpectation,
  matchesReExportExpectation,
  analyzeDeclaredNames,
};
