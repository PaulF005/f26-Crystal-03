import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IFeedback, NewFeedback } from '../feedback.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFeedback for edit and NewFeedbackFormGroupInput for create.
 */
type FeedbackFormGroupInput = IFeedback | PartialWithRequiredKeyOf<NewFeedback>;

type FeedbackFormDefaults = Pick<NewFeedback, 'id'>;

type FeedbackFormGroupContent = {
  id: FormControl<IFeedback['id'] | NewFeedback['id']>;
  feedback: FormControl<IFeedback['feedback']>;
  explanation: FormControl<IFeedback['explanation']>;
};

export type FeedbackFormGroup = FormGroup<FeedbackFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FeedbackFormService {
  createFeedbackFormGroup(feedback?: FeedbackFormGroupInput): FeedbackFormGroup {
    const feedbackRawValue = {
      ...this.getFormDefaults(),
      ...(feedback ?? { id: null }),
    };

    return new FormGroup<FeedbackFormGroupContent>({
      id: new FormControl(
        { value: feedbackRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      feedback: new FormControl(feedbackRawValue.feedback, {
        validators: [Validators.required],
      }),
      explanation: new FormControl(feedbackRawValue.explanation),
    });
  }

  getFeedback(form: FeedbackFormGroup): IFeedback | NewFeedback {
    return form.getRawValue();
  }

  resetForm(form: FeedbackFormGroup, feedback: FeedbackFormGroupInput): void {
    const feedbackRawValue = { ...this.getFormDefaults(), ...feedback };
    form.reset({
      ...feedbackRawValue,
      id: { value: feedbackRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): FeedbackFormDefaults {
    return {
      id: null,
    };
  }
}
