import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize } from 'rxjs';

import { AlertError } from 'app/shared/alert/alert-error';
import { IModule } from '../module.model';
import { ModuleService } from '../service/module.service';

import { ModuleFormGroup, ModuleFormService } from './module-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-module-update',
  templateUrl: './module-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ModuleUpdate implements OnInit {
  readonly isSaving = signal(false);
  module: IModule | null = null;

  protected moduleService = inject(ModuleService);
  protected moduleFormService = inject(ModuleFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ModuleFormGroup = this.moduleFormService.createModuleFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ module }) => {
      this.module = module;
      if (module) {
        this.updateForm(module);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const module = this.moduleFormService.getModule(this.editForm);
    if (module.id === null) {
      this.subscribeToSaveResponse(this.moduleService.create(module));
    } else {
      this.subscribeToSaveResponse(this.moduleService.update(module));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IModule | null>): void {
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

  protected updateForm(module: IModule): void {
    this.module = module;
    this.moduleFormService.resetForm(this.editForm, module);
  }
}
