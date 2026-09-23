import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-shared-spinner',
  template: '',
  styleUrl: './shared.spinner.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: {
    'aria-hidden': 'true',
  },
})
export class SharedSpinnerPage {}
