import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITopic, NewTopic } from '../topic.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITopic for edit and NewTopicFormGroupInput for create.
 */
type TopicFormGroupInput = ITopic | PartialWithRequiredKeyOf<NewTopic>;

type TopicFormDefaults = Pick<NewTopic, 'id'>;

type TopicFormGroupContent = {
  id: FormControl<ITopic['id'] | NewTopic['id']>;
  name: FormControl<ITopic['name']>;
};

export type TopicFormGroup = FormGroup<TopicFormGroupContent>;

@Service()
export class TopicFormService {
  createTopicFormGroup(topic?: TopicFormGroupInput): TopicFormGroup {
    const topicRawValue = {
      ...this.getFormDefaults(),
      ...(topic ?? { id: null }),
    };

    return new FormGroup<TopicFormGroupContent>({
      id: new FormControl(
        { value: topicRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(topicRawValue.name, {
        validators: [Validators.required],
      }),
    });
  }

  getTopic(form: TopicFormGroup): ITopic | NewTopic {
    return form.getRawValue();
  }

  resetForm(form: TopicFormGroup, topic: TopicFormGroupInput): void {
    const topicRawValue = { ...this.getFormDefaults(), ...topic };
    form.reset({
      ...topicRawValue,
      id: { value: topicRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): TopicFormDefaults {
    return {
      id: null,
    };
  }
}
