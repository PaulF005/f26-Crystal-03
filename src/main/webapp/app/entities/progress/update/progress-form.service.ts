import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IProgress, NewProgress } from '../progress.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProgress for edit and NewProgressFormGroupInput for create.
 */
type ProgressFormGroupInput = IProgress | PartialWithRequiredKeyOf<NewProgress>;

type ProgressFormDefaults = Pick<NewProgress, 'id'>;

type ProgressFormGroupContent = {
  id: FormControl<IProgress['id'] | NewProgress['id']>;
  moduleCompletion: FormControl<IProgress['moduleCompletion']>;
  module: FormControl<IProgress['module']>;
  user: FormControl<IProgress['user']>;
};

export type ProgressFormGroup = FormGroup<ProgressFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProgressFormService {
  createProgressFormGroup(progress?: ProgressFormGroupInput): ProgressFormGroup {
    const progressRawValue = {
      ...this.getFormDefaults(),
      ...(progress ?? { id: null }),
    };

    return new FormGroup<ProgressFormGroupContent>({
      id: new FormControl(
        { value: progressRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      moduleCompletion: new FormControl(progressRawValue.moduleCompletion, {
        validators: [Validators.required],
      }),
      module: new FormControl(progressRawValue.module),
      user: new FormControl(progressRawValue.user),
    });
  }

  getProgress(form: ProgressFormGroup): IProgress | NewProgress {
    return form.getRawValue();
  }

  resetForm(form: ProgressFormGroup, progress: ProgressFormGroupInput): void {
    const progressRawValue = { ...this.getFormDefaults(), ...progress };
    form.reset({
      ...progressRawValue,
      id: { value: progressRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ProgressFormDefaults {
    return {
      id: null,
    };
  }
}
