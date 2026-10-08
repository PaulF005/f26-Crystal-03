import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { IUserProfile } from '../user-profile.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-user-profile-detail',
  templateUrl: './user-profile-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class UserProfileDetail {
  readonly userProfile = input<IUserProfile | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
