import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ILegalContent, NewLegalContent } from '../legal-content.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILegalContent for edit and NewLegalContentFormGroupInput for create.
 */
type LegalContentFormGroupInput = ILegalContent | PartialWithRequiredKeyOf<NewLegalContent>;

type LegalContentFormDefaults = Pick<NewLegalContent, 'id'>;

type LegalContentFormGroupContent = {
  id: FormControl<ILegalContent['id'] | NewLegalContent['id']>;
  name: FormControl<ILegalContent['name']>;
};

export type LegalContentFormGroup = FormGroup<LegalContentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LegalContentFormService {
  createLegalContentFormGroup(legalContent?: LegalContentFormGroupInput): LegalContentFormGroup {
    const legalContentRawValue = {
      ...this.getFormDefaults(),
      ...(legalContent ?? { id: null }),
    };

    return new FormGroup<LegalContentFormGroupContent>({
      id: new FormControl(
        { value: legalContentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(legalContentRawValue.name, {
        validators: [Validators.required],
      }),
    });
  }

  getLegalContent(form: LegalContentFormGroup): ILegalContent | NewLegalContent {
    return form.getRawValue();
  }

  resetForm(form: LegalContentFormGroup, legalContent: LegalContentFormGroupInput): void {
    const legalContentRawValue = { ...this.getFormDefaults(), ...legalContent };
    form.reset({
      ...legalContentRawValue,
      id: { value: legalContentRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): LegalContentFormDefaults {
    return {
      id: null,
    };
  }
}
