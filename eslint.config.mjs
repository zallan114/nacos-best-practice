// @ts-check

import globals from 'globals';
import prettier from 'eslint-plugin-prettier/recommended';
import tseslint from 'typescript-eslint';
import js from '@eslint/js';

/** @type {import('eslint').Linter.Config[]} */
export default tseslint.config(
  {
    languageOptions: {
      globals: {
        ...globals.node,
      },
    },
  },
  { ignores: ['node_modules/', 'target/', '.mvn/'] },
  js.configs.recommended,
  ...tseslint.configs.recommended.map(config =>
    config.name === 'typescript-eslint/base' ? config : { ...config, files: ['**/*.ts', '**/*.tsx', '**/*.mts', '**/*.cts'] },
  ),
  {
    files: ['**/*.{js,cjs,mjs}'],
    rules: {
      'no-unused-vars': ['error', { argsIgnorePattern: '^_' }],
    },
  },
  prettier,
);
