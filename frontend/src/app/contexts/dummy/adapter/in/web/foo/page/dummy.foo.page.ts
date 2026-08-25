import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-dummy-foo-page',
  template: '<p>Dummy context - Foo page</p>',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DummyFooPage {}
