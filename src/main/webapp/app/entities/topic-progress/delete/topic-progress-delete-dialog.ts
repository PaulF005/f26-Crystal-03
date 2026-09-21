import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { TopicProgressService } from '../service/topic-progress.service';
import { ITopicProgress } from '../topic-progress.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './topic-progress-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class TopicProgressDeleteDialog {
  topicProgress?: ITopicProgress;

  protected readonly topicProgressService = inject(TopicProgressService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.topicProgressService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
