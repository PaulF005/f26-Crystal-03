import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../game-session.test-samples';

import { GameSessionFormService } from './game-session-form.service';

describe('GameSession Form Service', () => {
  let service: GameSessionFormService;

  beforeEach(() => {
    service = TestBed.inject(GameSessionFormService);
  });

  describe('Service methods', () => {
    describe('createGameSessionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createGameSessionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            startedAt: expect.any(Object),
            completedAt: expect.any(Object),
            user: expect.any(Object),
            game: expect.any(Object),
          }),
        );
      });

      it('passing IGameSession should create a new form with FormGroup', () => {
        const formGroup = service.createGameSessionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            startedAt: expect.any(Object),
            completedAt: expect.any(Object),
            user: expect.any(Object),
            game: expect.any(Object),
          }),
        );
      });
    });

    describe('getGameSession', () => {
      it('should return NewGameSession for default GameSession initial value', () => {
        const formGroup = service.createGameSessionFormGroup(sampleWithNewData);

        const gameSession = service.getGameSession(formGroup);

        expect(gameSession).toMatchObject(sampleWithNewData);
      });

      it('should return NewGameSession for empty GameSession initial value', () => {
        const formGroup = service.createGameSessionFormGroup();

        const gameSession = service.getGameSession(formGroup);

        expect(gameSession).toMatchObject({});
      });

      it('should return IGameSession', () => {
        const formGroup = service.createGameSessionFormGroup(sampleWithRequiredData);

        const gameSession = service.getGameSession(formGroup);

        expect(gameSession).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IGameSession should not enable id FormControl', () => {
        const formGroup = service.createGameSessionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewGameSession should disable id FormControl', () => {
        const formGroup = service.createGameSessionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
