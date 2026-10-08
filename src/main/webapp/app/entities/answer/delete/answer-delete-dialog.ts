import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { IAnswer } from '../answer.model';
import { AnswerService } from '../service/answer.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './answer-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class AnswerDeleteDialog {
  answer?: IAnswer;

  protected readonly answerService = inject(AnswerService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.answerService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
