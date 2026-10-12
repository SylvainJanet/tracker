import { Component, signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { describe, expect, it, vi } from 'vitest';

import { type SharedModalClosedBy, SharedModalPage } from './shared.modal.page';

@Component({
  imports: [SharedModalPage],
  template: `
    <app-shared-modal
      labelledBy="modal-title"
      describedBy="modal-description"
      [closedBy]="closedBy()"
      (closed)="closedReturnValue.set($event)"
    >
      <h2 id="modal-title">Modal title</h2>
      <p id="modal-description">Modal content</p>
    </app-shared-modal>
  `,
})
class SharedModalTestHost {
  readonly closedBy = signal<SharedModalClosedBy>('any');
  readonly closedReturnValue = signal<string | undefined>(undefined);
}

describe('SharedModalPage', () => {
  it('projects accessible content into a native dialog', async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModalTestHost],
    }).compileComponents();

    const fixture = TestBed.createComponent(SharedModalTestHost);

    fixture.detectChanges();

    const dialog = fixture.nativeElement.querySelector('dialog') as HTMLDialogElement | null;

    expect(dialog).not.toBeNull();
    expect(dialog?.getAttribute('aria-labelledby')).toBe('modal-title');
    expect(dialog?.getAttribute('aria-describedby')).toBe('modal-description');
    expect(dialog?.getAttribute('closedby')).toBe('any');
    expect(dialog?.textContent).toContain('Modal title');
    expect(dialog?.textContent).toContain('Modal content');

    fixture.componentInstance.closedBy.set('none');
    fixture.detectChanges();

    expect(dialog?.getAttribute('closedby')).toBe('none');
  });

  it('opens once, resets its return value, and emits the result when closed', async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModalTestHost],
    }).compileComponents();

    const fixture = TestBed.createComponent(SharedModalTestHost);

    fixture.detectChanges();

    const modalElement = fixture.debugElement.query(By.directive(SharedModalPage));
    const modal = modalElement.injector.get(SharedModalPage);
    const dialog = modalElement.nativeElement.querySelector('dialog') as HTMLDialogElement;

    const showModal = vi.fn(() => {
      dialog.setAttribute('open', '');
    });
    const close = vi.fn((returnValue = '') => {
      dialog.returnValue = returnValue;
      dialog.removeAttribute('open');
      dialog.dispatchEvent(new Event('close'));
    });

    Object.defineProperties(dialog, {
      showModal: {
        configurable: true,
        value: showModal,
      },
      close: {
        configurable: true,
        value: close,
      },
    });

    dialog.returnValue = 'previous-result';

    modal.open();
    modal.open();

    expect(showModal).toHaveBeenCalledOnce();
    expect(dialog.returnValue).toBe('');

    modal.close('saved');

    expect(close).toHaveBeenCalledWith('saved');
    expect(fixture.componentInstance.closedReturnValue()).toBe('saved');
  });
});
