import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config';
import { ITopicProgress, NewTopicProgress } from '../topic-progress.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITopicProgress for edit and NewTopicProgressFormGroupInput for create.
 */
type TopicProgressFormGroupInput = ITopicProgress | PartialWithRequiredKeyOf<NewTopicProgress>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITopicProgress | NewTopicProgress> = Omit<T, 'lastPracticedAt'> & {
  lastPracticedAt?: string | null;
};

type TopicProgressFormRawValue = FormValueOf<ITopicProgress>;

type NewTopicProgressFormRawValue = FormValueOf<NewTopicProgress>;

type TopicProgressFormDefaults = Pick<NewTopicProgress, 'id' | 'lastPracticedAt'>;

type TopicProgressFormGroupContent = {
  id: FormControl<TopicProgressFormRawValue['id'] | NewTopicProgress['id']>;
  competency: FormControl<TopicProgressFormRawValue['competency']>;
  improvement: FormControl<TopicProgressFormRawValue['improvement']>;
  evidenceCount: FormControl<TopicProgressFormRawValue['evidenceCount']>;
  lastPracticedAt: FormControl<TopicProgressFormRawValue['lastPracticedAt']>;
  topic: FormControl<TopicProgressFormRawValue['topic']>;
  userProfile: FormControl<TopicProgressFormRawValue['userProfile']>;
};

export type TopicProgressFormGroup = FormGroup<TopicProgressFormGroupContent>;

@Service()
export class TopicProgressFormService {
  createTopicProgressFormGroup(topicProgress?: TopicProgressFormGroupInput): TopicProgressFormGroup {
    const topicProgressRawValue = this.convertTopicProgressToTopicProgressRawValue({
      ...this.getFormDefaults(),
      ...(topicProgress ?? { id: null }),
    });

    return new FormGroup<TopicProgressFormGroupContent>({
      id: new FormControl(
        { value: topicProgressRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      competency: new FormControl(topicProgressRawValue.competency, {
        validators: [Validators.required, Validators.min(0), Validators.max(1)],
      }),
      improvement: new FormControl(topicProgressRawValue.improvement, {
        validators: [Validators.required],
      }),
      evidenceCount: new FormControl(topicProgressRawValue.evidenceCount, {
        validators: [Validators.required, Validators.min(0)],
      }),
      lastPracticedAt: new FormControl(topicProgressRawValue.lastPracticedAt, {
        validators: [Validators.required],
      }),
      topic: new FormControl(topicProgressRawValue.topic),
      userProfile: new FormControl(topicProgressRawValue.userProfile),
    });
  }

  getTopicProgress(form: TopicProgressFormGroup): ITopicProgress | NewTopicProgress {
    return this.convertTopicProgressRawValueToTopicProgress(form.getRawValue());
  }

  resetForm(form: TopicProgressFormGroup, topicProgress: TopicProgressFormGroupInput): void {
    const topicProgressRawValue = this.convertTopicProgressToTopicProgressRawValue({ ...this.getFormDefaults(), ...topicProgress });
    form.reset({
      ...topicProgressRawValue,
      id: { value: topicProgressRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): TopicProgressFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      lastPracticedAt: currentTime,
    };
  }

  private convertTopicProgressRawValueToTopicProgress(
    rawTopicProgress: TopicProgressFormRawValue | NewTopicProgressFormRawValue,
  ): ITopicProgress | NewTopicProgress {
    return {
      ...rawTopicProgress,
      lastPracticedAt: dayjs(rawTopicProgress.lastPracticedAt, DATE_TIME_FORMAT),
    };
  }

  private convertTopicProgressToTopicProgressRawValue(
    topicProgress: ITopicProgress | (Partial<NewTopicProgress> & TopicProgressFormDefaults),
  ): TopicProgressFormRawValue | PartialWithRequiredKeyOf<NewTopicProgressFormRawValue> {
    return {
      ...topicProgress,
      lastPracticedAt: topicProgress.lastPracticedAt ? topicProgress.lastPracticedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
