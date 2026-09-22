import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IUserProfile, NewUserProfile } from '../user-profile.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IUserProfile for edit and NewUserProfileFormGroupInput for create.
 */
type UserProfileFormGroupInput = IUserProfile | PartialWithRequiredKeyOf<NewUserProfile>;

type UserProfileFormDefaults = Pick<NewUserProfile, 'id'>;

type UserProfileFormGroupContent = {
  id: FormControl<IUserProfile['id'] | NewUserProfile['id']>;
  username: FormControl<IUserProfile['username']>;
  email: FormControl<IUserProfile['email']>;
  dataUser: FormControl<IUserProfile['dataUser']>;
};

export type UserProfileFormGroup = FormGroup<UserProfileFormGroupContent>;

@Service()
export class UserProfileFormService {
  createUserProfileFormGroup(userProfile?: UserProfileFormGroupInput): UserProfileFormGroup {
    const userProfileRawValue = {
      ...this.getFormDefaults(),
      ...(userProfile ?? { id: null }),
    };

    return new FormGroup<UserProfileFormGroupContent>({
      id: new FormControl(
        { value: userProfileRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      username: new FormControl(userProfileRawValue.username, {
        validators: [Validators.required],
      }),
      email: new FormControl(userProfileRawValue.email, {
        validators: [Validators.required],
      }),
      dataUser: new FormControl(userProfileRawValue.dataUser),
    });
  }

  getUserProfile(form: UserProfileFormGroup): IUserProfile | NewUserProfile {
    return form.getRawValue();
  }

  resetForm(form: UserProfileFormGroup, userProfile: UserProfileFormGroupInput): void {
    const userProfileRawValue = { ...this.getFormDefaults(), ...userProfile };
    form.reset({
      ...userProfileRawValue,
      id: { value: userProfileRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): UserProfileFormDefaults {
    return {
      id: null,
    };
  }
}
