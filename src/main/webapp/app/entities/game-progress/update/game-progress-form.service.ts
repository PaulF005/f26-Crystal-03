import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config';
import { IGameProgress, NewGameProgress } from '../game-progress.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IGameProgress for edit and NewGameProgressFormGroupInput for create.
 */
type GameProgressFormGroupInput = IGameProgress | PartialWithRequiredKeyOf<NewGameProgress>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IGameProgress | NewGameProgress> = Omit<T, 'lastPlayedAt'> & {
  lastPlayedAt?: string | null;
};

type GameProgressFormRawValue = FormValueOf<IGameProgress>;

type NewGameProgressFormRawValue = FormValueOf<NewGameProgress>;

type GameProgressFormDefaults = Pick<NewGameProgress, 'id' | 'lastPlayedAt'>;

type GameProgressFormGroupContent = {
  id: FormControl<GameProgressFormRawValue['id'] | NewGameProgress['id']>;
  sessionsPlayed: FormControl<GameProgressFormRawValue['sessionsPlayed']>;
  performance: FormControl<GameProgressFormRawValue['performance']>;
  evidenceCount: FormControl<GameProgressFormRawValue['evidenceCount']>;
  lastPlayedAt: FormControl<GameProgressFormRawValue['lastPlayedAt']>;
  game: FormControl<GameProgressFormRawValue['game']>;
  userProfile: FormControl<GameProgressFormRawValue['userProfile']>;
};

export type GameProgressFormGroup = FormGroup<GameProgressFormGroupContent>;

@Service()
export class GameProgressFormService {
  createGameProgressFormGroup(gameProgress?: GameProgressFormGroupInput): GameProgressFormGroup {
    const gameProgressRawValue = this.convertGameProgressToGameProgressRawValue({
      ...this.getFormDefaults(),
      ...(gameProgress ?? { id: null }),
    });

    return new FormGroup<GameProgressFormGroupContent>({
      id: new FormControl(
        { value: gameProgressRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      sessionsPlayed: new FormControl(gameProgressRawValue.sessionsPlayed, {
        validators: [Validators.required, Validators.min(0)],
      }),
      performance: new FormControl(gameProgressRawValue.performance, {
        validators: [Validators.required, Validators.min(0), Validators.max(1)],
      }),
      evidenceCount: new FormControl(gameProgressRawValue.evidenceCount, {
        validators: [Validators.required, Validators.min(0)],
      }),
      lastPlayedAt: new FormControl(gameProgressRawValue.lastPlayedAt, {
        validators: [Validators.required],
      }),
      game: new FormControl(gameProgressRawValue.game, {
        validators: [Validators.required],
      }),
      userProfile: new FormControl(gameProgressRawValue.userProfile, {
        validators: [Validators.required],
      }),
    });
  }

  getGameProgress(form: GameProgressFormGroup): IGameProgress | NewGameProgress {
    return this.convertGameProgressRawValueToGameProgress(form.getRawValue());
  }

  resetForm(form: GameProgressFormGroup, gameProgress: GameProgressFormGroupInput): void {
    const gameProgressRawValue = this.convertGameProgressToGameProgressRawValue({ ...this.getFormDefaults(), ...gameProgress });
    form.reset({
      ...gameProgressRawValue,
      id: { value: gameProgressRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): GameProgressFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      lastPlayedAt: currentTime,
    };
  }

  private convertGameProgressRawValueToGameProgress(
    rawGameProgress: GameProgressFormRawValue | NewGameProgressFormRawValue,
  ): IGameProgress | NewGameProgress {
    return {
      ...rawGameProgress,
      lastPlayedAt: dayjs(rawGameProgress.lastPlayedAt, DATE_TIME_FORMAT),
    };
  }

  private convertGameProgressToGameProgressRawValue(
    gameProgress: IGameProgress | (Partial<NewGameProgress> & GameProgressFormDefaults),
  ): GameProgressFormRawValue | PartialWithRequiredKeyOf<NewGameProgressFormRawValue> {
    return {
      ...gameProgress,
      lastPlayedAt: gameProgress.lastPlayedAt ? gameProgress.lastPlayedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
