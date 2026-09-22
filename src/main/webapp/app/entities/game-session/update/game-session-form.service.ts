import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config';
import { IGameSession, NewGameSession } from '../game-session.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IGameSession for edit and NewGameSessionFormGroupInput for create.
 */
type GameSessionFormGroupInput = IGameSession | PartialWithRequiredKeyOf<NewGameSession>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IGameSession | NewGameSession> = Omit<T, 'startedAt' | 'completedAt'> & {
  startedAt?: string | null;
  completedAt?: string | null;
};

type GameSessionFormRawValue = FormValueOf<IGameSession>;

type NewGameSessionFormRawValue = FormValueOf<NewGameSession>;

type GameSessionFormDefaults = Pick<NewGameSession, 'id' | 'startedAt' | 'completedAt'>;

type GameSessionFormGroupContent = {
  id: FormControl<GameSessionFormRawValue['id'] | NewGameSession['id']>;
  startedAt: FormControl<GameSessionFormRawValue['startedAt']>;
  completedAt: FormControl<GameSessionFormRawValue['completedAt']>;
  user: FormControl<GameSessionFormRawValue['user']>;
  game: FormControl<GameSessionFormRawValue['game']>;
};

export type GameSessionFormGroup = FormGroup<GameSessionFormGroupContent>;

@Service()
export class GameSessionFormService {
  createGameSessionFormGroup(gameSession?: GameSessionFormGroupInput): GameSessionFormGroup {
    const gameSessionRawValue = this.convertGameSessionToGameSessionRawValue({
      ...this.getFormDefaults(),
      ...(gameSession ?? { id: null }),
    });

    return new FormGroup<GameSessionFormGroupContent>({
      id: new FormControl(
        { value: gameSessionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      startedAt: new FormControl(gameSessionRawValue.startedAt, {
        validators: [Validators.required],
      }),
      completedAt: new FormControl(gameSessionRawValue.completedAt, {
        validators: [Validators.required],
      }),
      user: new FormControl(gameSessionRawValue.user),
      game: new FormControl(gameSessionRawValue.game),
    });
  }

  getGameSession(form: GameSessionFormGroup): IGameSession | NewGameSession {
    return this.convertGameSessionRawValueToGameSession(form.getRawValue());
  }

  resetForm(form: GameSessionFormGroup, gameSession: GameSessionFormGroupInput): void {
    const gameSessionRawValue = this.convertGameSessionToGameSessionRawValue({ ...this.getFormDefaults(), ...gameSession });
    form.reset({
      ...gameSessionRawValue,
      id: { value: gameSessionRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): GameSessionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      startedAt: currentTime,
      completedAt: currentTime,
    };
  }

  private convertGameSessionRawValueToGameSession(
    rawGameSession: GameSessionFormRawValue | NewGameSessionFormRawValue,
  ): IGameSession | NewGameSession {
    return {
      ...rawGameSession,
      startedAt: dayjs(rawGameSession.startedAt, DATE_TIME_FORMAT),
      completedAt: dayjs(rawGameSession.completedAt, DATE_TIME_FORMAT),
    };
  }

  private convertGameSessionToGameSessionRawValue(
    gameSession: IGameSession | (Partial<NewGameSession> & GameSessionFormDefaults),
  ): GameSessionFormRawValue | PartialWithRequiredKeyOf<NewGameSessionFormRawValue> {
    return {
      ...gameSession,
      startedAt: gameSession.startedAt ? gameSession.startedAt.format(DATE_TIME_FORMAT) : undefined,
      completedAt: gameSession.completedAt ? gameSession.completedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
