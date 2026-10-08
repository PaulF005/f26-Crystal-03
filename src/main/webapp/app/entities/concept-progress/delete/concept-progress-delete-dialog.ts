import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { IConceptProgress } from '../concept-progress.model';
import { ConceptProgressService } from '../service/concept-progress.service';

@Component({
  templateUrl: './concept-progress-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class ConceptProgressDeleteDialog {
  conceptProgress?: IConceptProgress;

  protected readonly conceptProgressService = inject(ConceptProgressService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.conceptProgressService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
