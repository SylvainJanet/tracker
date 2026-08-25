import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-other-dummy-see-page',
  template: '<p>OtherDummy context - See page</p>',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OtherDummySeePage {}
