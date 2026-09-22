import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { IUserProfile } from '../user-profile.model';

@Component({
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
