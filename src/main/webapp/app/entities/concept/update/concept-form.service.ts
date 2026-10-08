import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IConcept, NewConcept } from '../concept.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IConcept for edit and NewConceptFormGroupInput for create.
 */
type ConceptFormGroupInput = IConcept | PartialWithRequiredKeyOf<NewConcept>;

type ConceptFormDefaults = Pick<NewConcept, 'id'>;

type ConceptFormGroupContent = {
  id: FormControl<IConcept['id'] | NewConcept['id']>;
  name: FormControl<IConcept['name']>;
  explanation: FormControl<IConcept['explanation']>;
  legalContent: FormControl<IConcept['legalContent']>;
  topic: FormControl<IConcept['topic']>;
};

export type ConceptFormGroup = FormGroup<ConceptFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ConceptFormService {
  createConceptFormGroup(concept?: ConceptFormGroupInput): ConceptFormGroup {
    const conceptRawValue = {
      ...this.getFormDefaults(),
      ...(concept ?? { id: null }),
    };

    return new FormGroup<ConceptFormGroupContent>({
      id: new FormControl(
        { value: conceptRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(conceptRawValue.name, {
        validators: [Validators.required],
      }),
      explanation: new FormControl(conceptRawValue.explanation),
      legalContent: new FormControl(conceptRawValue.legalContent),
      topic: new FormControl(conceptRawValue.topic),
    });
  }

  getConcept(form: ConceptFormGroup): IConcept | NewConcept {
    return form.getRawValue();
  }

  resetForm(form: ConceptFormGroup, concept: ConceptFormGroupInput): void {
    const conceptRawValue = { ...this.getFormDefaults(), ...concept };
    form.reset({
      ...conceptRawValue,
      id: { value: conceptRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ConceptFormDefaults {
    return {
      id: null,
    };
  }
}
