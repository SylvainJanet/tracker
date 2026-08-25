// @ts-check

const eslint = require('@eslint/js');
const tseslint = require('typescript-eslint');
const angular = require('angular-eslint');
const eslintConfigPrettier = require('eslint-config-prettier');
const architecture = require('./eslint/architecture');

module.exports = tseslint.config(
  {
    ignores: ['.angular/**', 'coverage/**', 'dist/**', 'node_modules/**'],
  },

  {
    files: ['eslint.config.js', 'eslint/**/*.js'],

    extends: [eslint.configs.recommended],

    languageOptions: {
      ecmaVersion: 'latest',
      sourceType: 'commonjs',
      globals: {
        __dirname: 'readonly',
        module: 'readonly',
        require: 'readonly',
      },
    },
  },

  {
    files: ['**/*.ts'],

    extends: [
      eslint.configs.recommended,
      ...tseslint.configs.recommended,
      ...tseslint.configs.stylistic,
      ...angular.configs.tsRecommended,
    ],

    processor: angular.processInlineTemplates,

    rules: {
      '@angular-eslint/directive-selector': [
        'error',
        {
          type: 'attribute',
          prefix: 'app',
          style: 'camelCase',
        },
      ],

      '@angular-eslint/component-selector': [
        'error',
        {
          type: 'element',
          prefix: 'app',
          style: 'kebab-case',
        },
      ],

      '@typescript-eslint/consistent-type-imports': [
        'error',
        {
          prefer: 'type-imports',
          fixStyle: 'inline-type-imports',
        },
      ],

      curly: ['error', 'all'],
      eqeqeq: ['error', 'always'],
    },
  },

  {
    files: ['src/app/**/*.ts'],

    plugins: {
      architecture,
    },

    rules: {
      'architecture/declarations': 'error',
      'architecture/reserved-vocabulary': 'error',
      'architecture/hexagonal-architecture': 'error',
      'architecture/module-boundaries': 'error',
      'architecture/file-naming': 'error',
      'architecture/file-structure': 'error',
    },
  },

  {
    files: ['**/*.html'],

    extends: [...angular.configs.templateRecommended, ...angular.configs.templateAccessibility],

    plugins: {
      architecture,
    },

    rules: { 'architecture/file-naming': 'error', 'architecture/file-structure': 'error' },
  },

  {
    files: ['src/app/**/*.scss'],

    plugins: {
      architecture,
    },

    processor: 'architecture/filename-only',

    rules: { 'architecture/file-naming': 'error', 'architecture/file-structure': 'error' },
  },

  /* Domain code must remain independent of framework and reactive infrastructure. */
  {
    files: ['src/app/**/domain/**/*.ts'],
    ignores: ['**/*.spec.ts', '**/*.test.ts'],

    rules: {
      'no-restricted-imports': [
        'error',
        {
          patterns: [
            {
              group: ['@angular/**', 'rxjs', 'rxjs/**'],
              message: 'Domain code must remain independent of Angular and RxJS.',
            },
          ],
        },
      ],
    },
  },

  /* Application code must remain independent of framework and reactive infrastructure. */
  {
    files: ['src/app/**/application/**/*.ts'],
    ignores: ['**/*.spec.ts', '**/*.test.ts'],

    rules: {
      'no-restricted-imports': [
        'error',
        {
          patterns: [
            {
              group: ['@angular/**', 'rxjs', 'rxjs/**'],
              message: 'Application code must remain independent of Angular and RxJS.',
            },
          ],
        },
      ],
    },
  },

  /*
   * This must remain last so that ESLint does not compete
   * with Prettier over code formatting.
   */
  eslintConfigPrettier,
);
