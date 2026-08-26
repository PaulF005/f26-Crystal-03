import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { UserDetailService } from '../service/user-detail.service';
import { IUserDetail } from '../user-detail.model';

import { UserDetailFormGroup, UserDetailFormService } from './user-detail-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-user-detail-update',
  templateUrl: './user-detail-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class UserDetailUpdate implements OnInit {
  readonly isSaving = signal(false);
  userDetail: IUserDetail | null = null;

  usersSharedCollection = signal<IUser[]>([]);

  protected userDetailService = inject(UserDetailService);
  protected userDetailFormService = inject(UserDetailFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: UserDetailFormGroup = this.userDetailFormService.createUserDetailFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userDetail }) => {
      this.userDetail = userDetail;
      if (userDetail) {
        this.updateForm(userDetail);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const userDetail = this.userDetailFormService.getUserDetail(this.editForm);
    if (userDetail.id === null) {
      this.subscribeToSaveResponse(this.userDetailService.create(userDetail));
    } else {
      this.subscribeToSaveResponse(this.userDetailService.update(userDetail));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IUserDetail | null>): void {
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

  protected updateForm(userDetail: IUserDetail): void {
    this.userDetail = userDetail;
    this.userDetailFormService.resetForm(this.editForm, userDetail);

    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, userDetail.dataUser));
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.userDetail?.dataUser)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));
  }
}
