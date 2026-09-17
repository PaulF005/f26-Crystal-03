import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IStage, NewStage } from '../stage.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IStage for edit and NewStageFormGroupInput for create.
 */
type StageFormGroupInput = IStage | PartialWithRequiredKeyOf<NewStage>;

type StageFormDefaults = Pick<NewStage, 'id'>;

type StageFormGroupContent = {
  id: FormControl<IStage['id'] | NewStage['id']>;
  question: FormControl<IStage['question']>;
  scenario: FormControl<IStage['scenario']>;
};

export type StageFormGroup = FormGroup<StageFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class StageFormService {
  createStageFormGroup(stage?: StageFormGroupInput): StageFormGroup {
    const stageRawValue = {
      ...this.getFormDefaults(),
      ...(stage ?? { id: null }),
    };

    return new FormGroup<StageFormGroupContent>({
      id: new FormControl(
        { value: stageRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      question: new FormControl(stageRawValue.question),
      scenario: new FormControl(stageRawValue.scenario),
    });
  }

  getStage(form: StageFormGroup): IStage | NewStage {
    return form.getRawValue();
  }

  resetForm(form: StageFormGroup, stage: StageFormGroupInput): void {
    const stageRawValue = { ...this.getFormDefaults(), ...stage };
    form.reset({
      ...stageRawValue,
      id: { value: stageRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): StageFormDefaults {
    return {
      id: null,
    };
  }
}
