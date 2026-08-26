import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IModule, NewModule } from '../module.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IModule for edit and NewModuleFormGroupInput for create.
 */
type ModuleFormGroupInput = IModule | PartialWithRequiredKeyOf<NewModule>;

type ModuleFormDefaults = Pick<NewModule, 'id'>;

type ModuleFormGroupContent = {
  id: FormControl<IModule['id'] | NewModule['id']>;
  name: FormControl<IModule['name']>;
};

export type ModuleFormGroup = FormGroup<ModuleFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ModuleFormService {
  createModuleFormGroup(module?: ModuleFormGroupInput): ModuleFormGroup {
    const moduleRawValue = {
      ...this.getFormDefaults(),
      ...(module ?? { id: null }),
    };

    return new FormGroup<ModuleFormGroupContent>({
      id: new FormControl(
        { value: moduleRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(moduleRawValue.name, {
        validators: [Validators.required],
      }),
    });
  }

  getModule(form: ModuleFormGroup): IModule | NewModule {
    return form.getRawValue();
  }

  resetForm(form: ModuleFormGroup, module: ModuleFormGroupInput): void {
    const moduleRawValue = { ...this.getFormDefaults(), ...module };
    form.reset({
      ...moduleRawValue,
      id: { value: moduleRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ModuleFormDefaults {
    return {
      id: null,
    };
  }
}
