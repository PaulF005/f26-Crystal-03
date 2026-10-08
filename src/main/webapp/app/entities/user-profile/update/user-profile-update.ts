import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { UserProfileService } from '../service/user-profile.service';
import { IUserProfile } from '../user-profile.model';

import { UserProfileFormGroup, UserProfileFormService } from './user-profile-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-user-profile-update',
  templateUrl: './user-profile-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class UserProfileUpdate implements OnInit {
  readonly isSaving = signal(false);
  userProfile: IUserProfile | null = null;

  usersSharedCollection = signal<IUser[]>([]);

  protected userProfileService = inject(UserProfileService);
  protected userProfileFormService = inject(UserProfileFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: UserProfileFormGroup = this.userProfileFormService.createUserProfileFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userProfile }) => {
      this.userProfile = userProfile;
      if (userProfile) {
        this.updateForm(userProfile);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const userProfile = this.userProfileFormService.getUserProfile(this.editForm);
    if (userProfile.id === null) {
      this.subscribeToSaveResponse(this.userProfileService.create(userProfile));
    } else {
      this.subscribeToSaveResponse(this.userProfileService.update(userProfile));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IUserProfile | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(userProfile: IUserProfile): void {
    this.userProfile = userProfile;
    this.userProfileFormService.resetForm(this.editForm, userProfile);

    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, userProfile.dataUser));
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.userProfile?.dataUser)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));
  }
}
