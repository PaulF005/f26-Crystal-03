import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { IScenario } from '../scenario.model';
import { ScenarioService } from '../service/scenario.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './scenario-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class ScenarioDeleteDialog {
  scenario?: IScenario;

  protected readonly scenarioService = inject(ScenarioService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.scenarioService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
