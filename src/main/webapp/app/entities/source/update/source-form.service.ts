import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ISource, NewSource } from '../source.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISource for edit and NewSourceFormGroupInput for create.
 */
type SourceFormGroupInput = ISource | PartialWithRequiredKeyOf<NewSource>;

type SourceFormDefaults = Pick<NewSource, 'id'>;

type SourceFormGroupContent = {
  id: FormControl<ISource['id'] | NewSource['id']>;
  name: FormControl<ISource['name']>;
  url: FormControl<ISource['url']>;
  date: FormControl<ISource['date']>;
  data: FormControl<ISource['data']>;
  legalContent: FormControl<ISource['legalContent']>;
};

export type SourceFormGroup = FormGroup<SourceFormGroupContent>;

@Service()
export class SourceFormService {
  createSourceFormGroup(source?: SourceFormGroupInput): SourceFormGroup {
    const sourceRawValue = {
      ...this.getFormDefaults(),
      ...(source ?? { id: null }),
    };

    return new FormGroup<SourceFormGroupContent>({
      id: new FormControl(
        { value: sourceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(sourceRawValue.name, {
        validators: [Validators.required],
      }),
      url: new FormControl(sourceRawValue.url),
      date: new FormControl(sourceRawValue.date),
      data: new FormControl(sourceRawValue.data, {
        validators: [Validators.required],
      }),
      legalContent: new FormControl(sourceRawValue.legalContent),
    });
  }

  getSource(form: SourceFormGroup): ISource | NewSource {
    return form.getRawValue();
  }

  resetForm(form: SourceFormGroup, source: SourceFormGroupInput): void {
    const sourceRawValue = { ...this.getFormDefaults(), ...source };
    form.reset({
      ...sourceRawValue,
      id: { value: sourceRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): SourceFormDefaults {
    return {
      id: null,
    };
  }
}
