import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { UserDetailService } from 'app/entities/user-detail/service/user-detail.service';
import { IUserDetail } from 'app/entities/user-detail/user-detail.model';
import { AlertError } from 'app/shared/alert';
import { IProgress } from '../progress.model';
import { ProgressService } from '../service/progress.service';

import { ProgressFormGroup, ProgressFormService } from './progress-form.service';

@Component({
  selector: 'jhi-progress-update',
  templateUrl: './progress-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ProgressUpdate implements OnInit {
  readonly isSaving = signal(false);
  progress: IProgress | null = null;

  userDetailsSharedCollection = signal<IUserDetail[]>([]);

  protected progressService = inject(ProgressService);
  protected progressFormService = inject(ProgressFormService);
  protected userDetailService = inject(UserDetailService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ProgressFormGroup = this.progressFormService.createProgressFormGroup();

  compareUserDetail = (o1: IUserDetail | null, o2: IUserDetail | null): boolean => this.userDetailService.compareUserDetail(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ progress }) => {
      this.progress = progress;
      if (progress) {
        this.updateForm(progress);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const progress = this.progressFormService.getProgress(this.editForm);
    if (progress.id === null) {
      this.subscribeToSaveResponse(this.progressService.create(progress));
    } else {
      this.subscribeToSaveResponse(this.progressService.update(progress));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IProgress | null>): void {
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

  protected updateForm(progress: IProgress): void {
    this.progress = progress;
    this.progressFormService.resetForm(this.editForm, progress);

    this.userDetailsSharedCollection.update(userDetails =>
      this.userDetailService.addUserDetailToCollectionIfMissing<IUserDetail>(userDetails, progress.user),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userDetailService
      .query()
      .pipe(map((res: HttpResponse<IUserDetail[]>) => res.body ?? []))
      .pipe(
        map((userDetails: IUserDetail[]) =>
          this.userDetailService.addUserDetailToCollectionIfMissing<IUserDetail>(userDetails, this.progress?.user),
        ),
      )
      .subscribe((userDetails: IUserDetail[]) => this.userDetailsSharedCollection.set(userDetails));
  }
}
