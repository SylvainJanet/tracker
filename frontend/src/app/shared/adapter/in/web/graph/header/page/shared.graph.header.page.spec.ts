import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';

import { SharedGraphHeaderPage } from './shared.graph.header.page';

describe('SharedGraphHeaderPage', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedGraphHeaderPage],
    }).compileComponents();
  });

  it('renders its title as a semantic graph heading', () => {
    const fixture = TestBed.createComponent(SharedGraphHeaderPage);

    fixture.componentRef.setInput('title', 'Weight trends');
    fixture.detectChanges();

    const header = fixture.nativeElement.querySelector('header') as HTMLElement | null;
    const heading = fixture.nativeElement.querySelector('h3') as HTMLHeadingElement | null;

    expect(header).not.toBeNull();
    expect(heading?.textContent?.trim()).toBe('Weight trends');
  });
});
