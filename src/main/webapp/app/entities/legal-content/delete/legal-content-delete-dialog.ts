import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { ILegalContent } from '../legal-content.model';
import { LegalContentService } from '../service/legal-content.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './legal-content-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class LegalContentDeleteDialog {
  legalContent?: ILegalContent;

  protected readonly legalContentService = inject(LegalContentService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.legalContentService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
