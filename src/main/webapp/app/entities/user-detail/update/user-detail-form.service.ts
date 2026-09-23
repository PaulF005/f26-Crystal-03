import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IUserDetail, NewUserDetail } from '../user-detail.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IUserDetail for edit and NewUserDetailFormGroupInput for create.
 */
type UserDetailFormGroupInput = IUserDetail | PartialWithRequiredKeyOf<NewUserDetail>;

type UserDetailFormDefaults = Pick<NewUserDetail, 'id'>;

type UserDetailFormGroupContent = {
  id: FormControl<IUserDetail['id'] | NewUserDetail['id']>;
  username: FormControl<IUserDetail['username']>;
  email: FormControl<IUserDetail['email']>;
  dataUser: FormControl<IUserDetail['dataUser']>;
};

export type UserDetailFormGroup = FormGroup<UserDetailFormGroupContent>;

@Service()
export class UserDetailFormService {
  createUserDetailFormGroup(userDetail?: UserDetailFormGroupInput): UserDetailFormGroup {
    const userDetailRawValue = {
      ...this.getFormDefaults(),
      ...(userDetail ?? { id: null }),
    };

    return new FormGroup<UserDetailFormGroupContent>({
      id: new FormControl(
        { value: userDetailRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      username: new FormControl(userDetailRawValue.username, {
        validators: [Validators.required],
      }),
      email: new FormControl(userDetailRawValue.email, {
        validators: [Validators.required],
      }),
      dataUser: new FormControl(userDetailRawValue.dataUser),
    });
  }

  getUserDetail(form: UserDetailFormGroup): IUserDetail | NewUserDetail {
    return form.getRawValue();
  }

  resetForm(form: UserDetailFormGroup, userDetail: UserDetailFormGroupInput): void {
    const userDetailRawValue = { ...this.getFormDefaults(), ...userDetail };
    form.reset({
      ...userDetailRawValue,
      id: { value: userDetailRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): UserDetailFormDefaults {
    return {
      id: null,
    };
  }
}
