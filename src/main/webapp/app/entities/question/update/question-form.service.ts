import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IQuestion, NewQuestion } from '../question.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IQuestion for edit and NewQuestionFormGroupInput for create.
 */
type QuestionFormGroupInput = IQuestion | PartialWithRequiredKeyOf<NewQuestion>;

type QuestionFormDefaults = Pick<NewQuestion, 'id'>;

type QuestionFormGroupContent = {
  id: FormControl<IQuestion['id'] | NewQuestion['id']>;
  question: FormControl<IQuestion['question']>;
  concept: FormControl<IQuestion['concept']>;
};

export type QuestionFormGroup = FormGroup<QuestionFormGroupContent>;

@Service()
export class QuestionFormService {
  createQuestionFormGroup(question?: QuestionFormGroupInput): QuestionFormGroup {
    const questionRawValue = {
      ...this.getFormDefaults(),
      ...(question ?? { id: null }),
    };

    return new FormGroup<QuestionFormGroupContent>({
      id: new FormControl(
        { value: questionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      question: new FormControl(questionRawValue.question, {
        validators: [Validators.required],
      }),
      concept: new FormControl(questionRawValue.concept),
    });
  }

  getQuestion(form: QuestionFormGroup): IQuestion | NewQuestion {
    return form.getRawValue();
  }

  resetForm(form: QuestionFormGroup, question: QuestionFormGroupInput): void {
    const questionRawValue = { ...this.getFormDefaults(), ...question };
    form.reset({
      ...questionRawValue,
      id: { value: questionRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): QuestionFormDefaults {
    return {
      id: null,
    };
  }
}
