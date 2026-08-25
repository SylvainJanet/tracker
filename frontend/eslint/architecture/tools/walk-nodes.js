// @ts-check

'use strict';

/**
 * @param {import('@typescript-eslint/utils').TSESTree.Node} root
 * @param {(node: import('@typescript-eslint/utils').TSESTree.Node) => void} visit
 */
function walkNodes(root, visit) {
  const visited = new WeakSet();

  /** @param {unknown} value */
  function walk(value) {
    if (value === null || typeof value !== 'object' || visited.has(value)) {
      return;
    }

    visited.add(value);

    if (Array.isArray(value)) {
      for (const item of value) {
        walk(item);
      }

      return;
    }

    if ('type' in value && typeof value.type === 'string') {
      visit(/** @type {import('@typescript-eslint/utils').TSESTree.Node} */ (value));
    }

    for (const [key, child] of Object.entries(value)) {
      if (key !== 'parent') {
        walk(child);
      }
    }
  }

  walk(root);
}

module.exports = { walkNodes };
