import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { IScenario } from '../scenario.model';

@Component({
  selector: 'jhi-scenario-detail',
  templateUrl: './scenario-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class ScenarioDetail {
  readonly scenario = input<IScenario | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
