import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-other-dummy-nothing-page',
  template: '<p>OtherDummy context - Nothing page</p>',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OtherDummyNothingPage {}
