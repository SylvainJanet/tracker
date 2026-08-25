import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavigationPage } from '../../navigation/api/page';

@Component({
  selector: 'app-page',
  imports: [NavigationPage, RouterOutlet],
  templateUrl: './app.page.html',
  styleUrl: './app.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AppPage {}
