import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-other-dummy-to-page',
  template: '<p>OtherDummy context - To page</p>',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OtherDummyToPage {}
