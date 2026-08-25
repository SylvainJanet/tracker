import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-other-dummy-here-page',
  template: '<p>OtherDummy context - Here page</p>',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OtherDummyHerePage {}
