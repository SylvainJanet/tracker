import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-dummy-bar-page',
  template: '<p>Dummy context - Bar page</p>',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DummyBarPage {}
