import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { IFeedback } from '../feedback.model';
import { FeedbackService } from '../service/feedback.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './feedback-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class FeedbackDeleteDialog {
  feedback?: IFeedback;

  protected readonly feedbackService = inject(FeedbackService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.feedbackService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
