import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config';
import { IStageAttempt, NewStageAttempt } from '../stage-attempt.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IStageAttempt for edit and NewStageAttemptFormGroupInput for create.
 */
type StageAttemptFormGroupInput = IStageAttempt | PartialWithRequiredKeyOf<NewStageAttempt>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IStageAttempt | NewStageAttempt> = Omit<T, 'answeredAt'> & {
  answeredAt?: string | null;
};

type StageAttemptFormRawValue = FormValueOf<IStageAttempt>;

type NewStageAttemptFormRawValue = FormValueOf<NewStageAttempt>;

type StageAttemptFormDefaults = Pick<NewStageAttempt, 'id' | 'answeredAt' | 'correct'>;

type StageAttemptFormGroupContent = {
  id: FormControl<StageAttemptFormRawValue['id'] | NewStageAttempt['id']>;
  answeredAt: FormControl<StageAttemptFormRawValue['answeredAt']>;
  correct: FormControl<StageAttemptFormRawValue['correct']>;
  user: FormControl<StageAttemptFormRawValue['user']>;
  stage: FormControl<StageAttemptFormRawValue['stage']>;
  selectedAnswer: FormControl<StageAttemptFormRawValue['selectedAnswer']>;
  gameSession: FormControl<StageAttemptFormRawValue['gameSession']>;
};

export type StageAttemptFormGroup = FormGroup<StageAttemptFormGroupContent>;

@Service()
export class StageAttemptFormService {
  createStageAttemptFormGroup(stageAttempt?: StageAttemptFormGroupInput): StageAttemptFormGroup {
    const stageAttemptRawValue = this.convertStageAttemptToStageAttemptRawValue({
      ...this.getFormDefaults(),
      ...(stageAttempt ?? { id: null }),
    });

    return new FormGroup<StageAttemptFormGroupContent>({
      id: new FormControl(
        { value: stageAttemptRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      answeredAt: new FormControl(stageAttemptRawValue.answeredAt, {
        validators: [Validators.required],
      }),
      correct: new FormControl(stageAttemptRawValue.correct, {
        validators: [Validators.required],
      }),
      user: new FormControl(stageAttemptRawValue.user),
      stage: new FormControl(stageAttemptRawValue.stage),
      selectedAnswer: new FormControl(stageAttemptRawValue.selectedAnswer),
      gameSession: new FormControl(stageAttemptRawValue.gameSession, {
        validators: [Validators.required],
      }),
    });
  }

  getStageAttempt(form: StageAttemptFormGroup): IStageAttempt | NewStageAttempt {
    return this.convertStageAttemptRawValueToStageAttempt(form.getRawValue());
  }

  resetForm(form: StageAttemptFormGroup, stageAttempt: StageAttemptFormGroupInput): void {
    const stageAttemptRawValue = this.convertStageAttemptToStageAttemptRawValue({ ...this.getFormDefaults(), ...stageAttempt });
    form.reset({
      ...stageAttemptRawValue,
      id: { value: stageAttemptRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): StageAttemptFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      answeredAt: currentTime,
      correct: false,
    };
  }

  private convertStageAttemptRawValueToStageAttempt(
    rawStageAttempt: StageAttemptFormRawValue | NewStageAttemptFormRawValue,
  ): IStageAttempt | NewStageAttempt {
    return {
      ...rawStageAttempt,
      answeredAt: dayjs(rawStageAttempt.answeredAt, DATE_TIME_FORMAT),
    };
  }

  private convertStageAttemptToStageAttemptRawValue(
    stageAttempt: IStageAttempt | (Partial<NewStageAttempt> & StageAttemptFormDefaults),
  ): StageAttemptFormRawValue | PartialWithRequiredKeyOf<NewStageAttemptFormRawValue> {
    return {
      ...stageAttempt,
      answeredAt: stageAttempt.answeredAt ? stageAttempt.answeredAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
