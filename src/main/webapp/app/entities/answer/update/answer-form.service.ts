import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IAnswer, NewAnswer } from '../answer.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAnswer for edit and NewAnswerFormGroupInput for create.
 */
type AnswerFormGroupInput = IAnswer | PartialWithRequiredKeyOf<NewAnswer>;

type AnswerFormDefaults = Pick<NewAnswer, 'id' | 'correct'>;

type AnswerFormGroupContent = {
  id: FormControl<IAnswer['id'] | NewAnswer['id']>;
  answer: FormControl<IAnswer['answer']>;
  correct: FormControl<IAnswer['correct']>;
  nextStage: FormControl<IAnswer['nextStage']>;
  feedback: FormControl<IAnswer['feedback']>;
  question: FormControl<IAnswer['question']>;
};

export type AnswerFormGroup = FormGroup<AnswerFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AnswerFormService {
  createAnswerFormGroup(answer?: AnswerFormGroupInput): AnswerFormGroup {
    const answerRawValue = {
      ...this.getFormDefaults(),
      ...(answer ?? { id: null }),
    };

    return new FormGroup<AnswerFormGroupContent>({
      id: new FormControl(
        { value: answerRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      answer: new FormControl(answerRawValue.answer, {
        validators: [Validators.required],
      }),
      correct: new FormControl(answerRawValue.correct, {
        validators: [Validators.required],
      }),
      nextStage: new FormControl(answerRawValue.nextStage),
      feedback: new FormControl(answerRawValue.feedback),
      question: new FormControl(answerRawValue.question),
    });
  }

  getAnswer(form: AnswerFormGroup): IAnswer | NewAnswer {
    return form.getRawValue();
  }

  resetForm(form: AnswerFormGroup, answer: AnswerFormGroupInput): void {
    const answerRawValue = { ...this.getFormDefaults(), ...answer };
    form.reset({
      ...answerRawValue,
      id: { value: answerRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): AnswerFormDefaults {
    return {
      id: null,
      correct: false,
    };
  }
}
