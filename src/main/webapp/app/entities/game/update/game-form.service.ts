import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IGame, NewGame } from '../game.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IGame for edit and NewGameFormGroupInput for create.
 */
type GameFormGroupInput = IGame | PartialWithRequiredKeyOf<NewGame>;

type GameFormDefaults = Pick<NewGame, 'id'>;

type GameFormGroupContent = {
  id: FormControl<IGame['id'] | NewGame['id']>;
  name: FormControl<IGame['name']>;
};

export type GameFormGroup = FormGroup<GameFormGroupContent>;

@Service()
export class GameFormService {
  createGameFormGroup(game?: GameFormGroupInput): GameFormGroup {
    const gameRawValue = {
      ...this.getFormDefaults(),
      ...(game ?? { id: null }),
    };

    return new FormGroup<GameFormGroupContent>({
      id: new FormControl(
        { value: gameRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(gameRawValue.name, {
        validators: [Validators.required],
      }),
    });
  }

  getGame(form: GameFormGroup): IGame | NewGame {
    return form.getRawValue();
  }

  resetForm(form: GameFormGroup, game: GameFormGroupInput): void {
    const gameRawValue = { ...this.getFormDefaults(), ...game };
    form.reset({
      ...gameRawValue,
      id: { value: gameRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): GameFormDefaults {
    return {
      id: null,
    };
  }
}
