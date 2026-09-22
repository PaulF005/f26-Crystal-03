import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { TopicService } from '../service/topic.service';
import { ITopic } from '../topic.model';

@Component({
  templateUrl: './topic-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class TopicDeleteDialog {
  topic?: ITopic;

  protected readonly topicService = inject(TopicService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.topicService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
