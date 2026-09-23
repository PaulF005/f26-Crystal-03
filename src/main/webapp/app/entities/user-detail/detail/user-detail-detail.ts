import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { IUserDetail } from '../user-detail.model';

@Component({
  selector: 'jhi-user-detail-detail',
  templateUrl: './user-detail-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class UserDetailDetail {
  readonly userDetail = input<IUserDetail | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
