// @ts-check

'use strict';

const assert = require('node:assert/strict');
const { describe, it } = require('node:test');

const { analyzePath } = require('../../tools/analyze-path');

describe('analyzePath', () => {
  it('extracts raw facts from a file below src/app', () => {
    const result = analyzePath(
      String.raw`C:\tracker\frontend\src\app\contexts\daily-record\application\service\create-record.service.spec.ts`,
    );

    assert.deepEqual(result, {
      value:
        'C:/tracker/frontend/src/app/contexts/daily-record/application/service/create-record.service.spec.ts',
      relativePath: 'contexts/daily-record/application/service/create-record.service.spec.ts',
      folders: ['contexts', 'daily-record', 'application', 'service'],
      fileName: 'create-record.service.spec.ts',
      stem: 'create-record.service',
      extension: '.spec.ts',
      nameParts: ['create-record', 'service'],
      isTest: true,
      isAngularInlineTemplate: false,
    });
  });

  it('retains filename facts for a file outside src/app', () => {
    const result = analyzePath('/tracker/frontend/scripts/build.ts');

    assert.deepEqual(result, {
      value: '/tracker/frontend/scripts/build.ts',
      relativePath: null,
      folders: [],
      fileName: 'build.ts',
      stem: 'build',
      extension: '.ts',
      nameParts: ['build'],
      isTest: false,
      isAngularInlineTemplate: false,
    });
  });

  it('recognizes Angular inline-template virtual files', () => {
    const result = analyzePath(
      'src/app/shell/app/page/app.page.ts/1_inline-template-app.page.ts-1.component.html',
    );

    assert.equal(result.isAngularInlineTemplate, true);
  });
});
