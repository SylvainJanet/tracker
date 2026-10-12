import {
  ChangeDetectionStrategy,
  Component,
  type ElementRef,
  input,
  output,
  viewChild,
} from '@angular/core';

export type SharedModalClosedBy = 'any' | 'closerequest' | 'none';

@Component({
  selector: 'app-shared-modal',
  templateUrl: './shared.modal.page.html',
  styleUrl: './shared.modal.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SharedModalPage {
  readonly labelledBy = input.required<string>();
  readonly describedBy = input<string>();
  readonly closedBy = input<SharedModalClosedBy>('any');

  readonly closed = output<string>();

  private readonly dialog = viewChild.required<ElementRef<HTMLDialogElement>>('dialog');

  open(): void {
    const dialog = this.dialog().nativeElement;

    if (dialog.open) {
      return;
    }

    dialog.returnValue = '';
    dialog.showModal();
  }

  close(returnValue = ''): void {
    const dialog = this.dialog().nativeElement;

    if (dialog.open) {
      dialog.close(returnValue);
    }
  }

  onDialogClosed(): void {
    this.closed.emit(this.dialog().nativeElement.returnValue);
  }
}
