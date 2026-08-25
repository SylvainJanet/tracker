import type { TSESTree } from '@typescript-eslint/utils';

export type ModuleKind = 'application' | 'context' | 'shared' | 'shell';

export interface FolderMetadata {
  moduleKind?: ModuleKind;
  moduleSegment?: boolean;
  layer?: string;
  visibility?: 'internal' | 'public-api';
  namingSegment?: boolean;
  reservedDeclarationSuffixes?: string[];
  allowedDeclarationSuffixes?: string[];
  allowedDependencyLayers?: string[];
  allowsCrossContextDependencies?: boolean;
  allowedApplicationRootDependencyLayers?: string[];
}

export interface DynamicFolder {
  label: string;
  folder: ArchitectureFolder | 'same';
  metadata?: FolderMetadata;
  excludedNames?: string[];
}

export interface FileNaming {
  stems: string[];
  extensions: string[];
}

export interface FileNameMatch {
  implementation: string | null;
  responsibility: string | null;
  segment: string | null;
}

export interface FileNameConvention {
  allowedFileNames: string[];
  match(fileName: string): FileNameMatch | null;
  matches(fileName: string): boolean;
}

export interface ExpectedDeclaration {
  name: string;
  types: DeclarationType[];
  implements?: string[];
  functionType?: {
    parameterCount: number;
    returnType: string;
  };
  requiredProperties?: string[];
}

export interface DerivedExportExpectation {
  nameFrom: string;
  transform: 'upper-snake';
  types: DeclarationType[];
}

export interface DeclarationExpectation {
  minimumExports?: number;
  requiredExports: ExpectedDeclaration[];
  optionalExports?: ExpectedDeclaration[];
  derivedExports?: DerivedExportExpectation[];
  maximumOptionalExports?: number;
}

export type ReExportSource = { exact: string } | { prefix: string };

export interface ReExportExpectation {
  statements: 'one' | 'one-or-more';
  source: ReExportSource;
  names: string[] | 'any';
}

export interface ExpectedFile {
  naming: FileNaming;
  declarations?: DeclarationExpectation;
  reExports?: ReExportExpectation;
}

export interface ExpectedFileMatch {
  expectedFile: ExpectedFile;
  name: FileNameMatch;
}

export interface ArchitectureFolder {
  files: ExpectedFile[];
  folders?: Record<string, ArchitectureFolder>;
  anyFolder?: DynamicFolder;
  metadata?: FolderMetadata;
}

export interface FilePath {
  value: string;
  relativePath: string | null;
  folders: string[];
  fileName: string;
  stem: string;
  extension: string;
  nameParts: string[];
  isTest: boolean;
  isAngularInlineTemplate: boolean;
}

export type DeclarationType = 'class' | 'interface' | 'type' | 'function' | 'const';

export interface FunctionTypeAnalysis {
  parameterCount: number;
  returnType: string | null;
}

export interface ExportedDeclaration {
  name: string;
  type: DeclarationType;
  implements?: string[] | null;
  functionType?: FunctionTypeAnalysis;
  requiredProperties?: string[];
}

export interface ReExportedName {
  imported: string;
  exported: string;
}

export interface ReExport {
  source: string;
  names: ReExportedName[];
}

export interface DeclarationAnalysis {
  exported: ExportedDeclaration[];
  reExports: ReExport[];
  unsupportedExports: boolean;
}

export interface AnalyzedFile {
  node: TSESTree.Program;
  path: FilePath;
  declarations: DeclarationAnalysis;
  declaredNames: string[];
  dependencies: AnalyzedDependency[];
}

export interface AnalyzedDependency {
  source: string;
}

export interface MatchedDependency {
  source: string;
  path: FilePath;
  target: ArchitectureMatch;
}

export interface ArchitectureModule {
  kind: ModuleKind;
  name: string;
  root: string;
  scope: string[];
}

export interface StructureViolation {
  directoryPath: string;
  allowsFiles: boolean;
  allowedFolders: string[];
}

export interface ArchitectureMatch {
  folder: ArchitectureFolder;
  metadata: FolderMetadata;
  module: ArchitectureModule | null;
  layer: string | null;
  namingScope: string[];
  violation: StructureViolation | null;
}

export interface RuleDefinition {
  messages: Record<string, string>;
  check(file: AnalyzedFile, expected: ArchitectureMatch): RuleViolation[];
}

export interface RuleViolation {
  node: TSESTree.Node;
  messageId: string;
  data?: Record<string, string>;
}
