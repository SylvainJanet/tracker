// @ts-check

'use strict';

module.exports = /** @satisfies {import('eslint').Linter.Processor} */ ({
  preprocess(_text, _filename) {
    void _text;
    void _filename;
    return [''];
  },

  postprocess(messageLists, _filename) {
    void _filename;
    return messageLists.flat();
  },
});
