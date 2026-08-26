import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { IModule } from '../module.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-module-detail',
  templateUrl: './module-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class ModuleDetail {
  readonly module = input<IModule | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
