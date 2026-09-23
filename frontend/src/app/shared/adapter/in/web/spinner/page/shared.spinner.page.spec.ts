import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';

import { SharedSpinnerPage } from './shared.spinner.page';

describe('SharedSpinnerPage', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedSpinnerPage],
    }).compileComponents();
  });

  it('is decorative because the surrounding status describes the activity', () => {
    const fixture = TestBed.createComponent(SharedSpinnerPage);

    fixture.detectChanges();

    const spinner = fixture.nativeElement as HTMLElement;

    expect(spinner.getAttribute('aria-hidden')).toBe('true');
    expect(spinner.textContent).toBe('');
  });
});
