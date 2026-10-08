import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { IConcept } from '../concept.model';
import { ConceptService } from '../service/concept.service';

@Component({
  templateUrl: './concept-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class ConceptDeleteDialog {
  concept?: IConcept;

  protected readonly conceptService = inject(ConceptService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.conceptService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
