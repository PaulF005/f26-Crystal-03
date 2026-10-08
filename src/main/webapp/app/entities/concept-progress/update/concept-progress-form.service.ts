import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config';
import { IConceptProgress, NewConceptProgress } from '../concept-progress.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IConceptProgress for edit and NewConceptProgressFormGroupInput for create.
 */
type ConceptProgressFormGroupInput = IConceptProgress | PartialWithRequiredKeyOf<NewConceptProgress>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IConceptProgress | NewConceptProgress> = Omit<T, 'lastPracticedAt'> & {
  lastPracticedAt?: string | null;
};

type ConceptProgressFormRawValue = FormValueOf<IConceptProgress>;

type NewConceptProgressFormRawValue = FormValueOf<NewConceptProgress>;

type ConceptProgressFormDefaults = Pick<NewConceptProgress, 'id' | 'lastPracticedAt'>;

type ConceptProgressFormGroupContent = {
  id: FormControl<ConceptProgressFormRawValue['id'] | NewConceptProgress['id']>;
  competency: FormControl<ConceptProgressFormRawValue['competency']>;
  improvement: FormControl<ConceptProgressFormRawValue['improvement']>;
  evidenceCount: FormControl<ConceptProgressFormRawValue['evidenceCount']>;
  lastPracticedAt: FormControl<ConceptProgressFormRawValue['lastPracticedAt']>;
  userProfile: FormControl<ConceptProgressFormRawValue['userProfile']>;
  concept: FormControl<ConceptProgressFormRawValue['concept']>;
};

export type ConceptProgressFormGroup = FormGroup<ConceptProgressFormGroupContent>;

@Service()
export class ConceptProgressFormService {
  createConceptProgressFormGroup(conceptProgress?: ConceptProgressFormGroupInput): ConceptProgressFormGroup {
    const conceptProgressRawValue = this.convertConceptProgressToConceptProgressRawValue({
      ...this.getFormDefaults(),
      ...(conceptProgress ?? { id: null }),
    });

    return new FormGroup<ConceptProgressFormGroupContent>({
      id: new FormControl(
        { value: conceptProgressRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      competency: new FormControl(conceptProgressRawValue.competency, {
        validators: [Validators.required, Validators.min(0), Validators.max(1)],
      }),
      improvement: new FormControl(conceptProgressRawValue.improvement, {
        validators: [Validators.required],
      }),
      evidenceCount: new FormControl(conceptProgressRawValue.evidenceCount, {
        validators: [Validators.required, Validators.min(0)],
      }),
      lastPracticedAt: new FormControl(conceptProgressRawValue.lastPracticedAt, {
        validators: [Validators.required],
      }),
      userProfile: new FormControl(conceptProgressRawValue.userProfile),
      concept: new FormControl(conceptProgressRawValue.concept),
    });
  }

  getConceptProgress(form: ConceptProgressFormGroup): IConceptProgress | NewConceptProgress {
    return this.convertConceptProgressRawValueToConceptProgress(form.getRawValue());
  }

  resetForm(form: ConceptProgressFormGroup, conceptProgress: ConceptProgressFormGroupInput): void {
    const conceptProgressRawValue = this.convertConceptProgressToConceptProgressRawValue({ ...this.getFormDefaults(), ...conceptProgress });
    form.reset({
      ...conceptProgressRawValue,
      id: { value: conceptProgressRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ConceptProgressFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      lastPracticedAt: currentTime,
    };
  }

  private convertConceptProgressRawValueToConceptProgress(
    rawConceptProgress: ConceptProgressFormRawValue | NewConceptProgressFormRawValue,
  ): IConceptProgress | NewConceptProgress {
    return {
      ...rawConceptProgress,
      lastPracticedAt: dayjs(rawConceptProgress.lastPracticedAt, DATE_TIME_FORMAT),
    };
  }

  private convertConceptProgressToConceptProgressRawValue(
    conceptProgress: IConceptProgress | (Partial<NewConceptProgress> & ConceptProgressFormDefaults),
  ): ConceptProgressFormRawValue | PartialWithRequiredKeyOf<NewConceptProgressFormRawValue> {
    return {
      ...conceptProgress,
      lastPracticedAt: conceptProgress.lastPracticedAt ? conceptProgress.lastPracticedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
