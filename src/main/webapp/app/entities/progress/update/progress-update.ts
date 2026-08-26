import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { IModule } from 'app/entities/module/module.model';
import { ModuleService } from 'app/entities/module/service/module.service';
import { UserDetailService } from 'app/entities/user-detail/service/user-detail.service';
import { IUserDetail } from 'app/entities/user-detail/user-detail.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { IProgress } from '../progress.model';
import { ProgressService } from '../service/progress.service';

import { ProgressFormGroup, ProgressFormService } from './progress-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-progress-update',
  templateUrl: './progress-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ProgressUpdate implements OnInit {
  readonly isSaving = signal(false);
  progress: IProgress | null = null;

  modulesSharedCollection = signal<IModule[]>([]);
  userDetailsSharedCollection = signal<IUserDetail[]>([]);

  protected progressService = inject(ProgressService);
  protected progressFormService = inject(ProgressFormService);
  protected moduleService = inject(ModuleService);
  protected userDetailService = inject(UserDetailService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ProgressFormGroup = this.progressFormService.createProgressFormGroup();

  compareModule = (o1: IModule | null, o2: IModule | null): boolean => this.moduleService.compareModule(o1, o2);

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

    this.modulesSharedCollection.update(modules => this.moduleService.addModuleToCollectionIfMissing<IModule>(modules, progress.module));
    this.userDetailsSharedCollection.update(userDetails =>
      this.userDetailService.addUserDetailToCollectionIfMissing<IUserDetail>(userDetails, progress.user),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.moduleService
      .query()
      .pipe(map((res: HttpResponse<IModule[]>) => res.body ?? []))
      .pipe(map((modules: IModule[]) => this.moduleService.addModuleToCollectionIfMissing<IModule>(modules, this.progress?.module)))
      .subscribe((modules: IModule[]) => this.modulesSharedCollection.set(modules));

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
