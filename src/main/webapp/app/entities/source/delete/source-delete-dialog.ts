import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { SourceService } from '../service/source.service';
import { ISource } from '../source.model';

@Component({
  templateUrl: './source-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class SourceDeleteDialog {
  source?: ISource;

  protected readonly sourceService = inject(SourceService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.sourceService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
