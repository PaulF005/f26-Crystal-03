import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { IQuestion } from '../question.model';
import { QuestionService } from '../service/question.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './question-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class QuestionDeleteDialog {
  question?: IQuestion;

  protected readonly questionService = inject(QuestionService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.questionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
