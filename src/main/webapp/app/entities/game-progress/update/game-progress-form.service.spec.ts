import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../game-progress.test-samples';

import { GameProgressFormService } from './game-progress-form.service';

describe('GameProgress Form Service', () => {
  let service: GameProgressFormService;

  beforeEach(() => {
    service = TestBed.inject(GameProgressFormService);
  });

  describe('Service methods', () => {
    describe('createGameProgressFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createGameProgressFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            sessionsPlayed: expect.any(Object),
            performance: expect.any(Object),
            evidenceCount: expect.any(Object),
            lastPlayedAt: expect.any(Object),
            game: expect.any(Object),
            user: expect.any(Object),
            userProfile: expect.any(Object),
          }),
        );
      });

      it('passing IGameProgress should create a new form with FormGroup', () => {
        const formGroup = service.createGameProgressFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            sessionsPlayed: expect.any(Object),
            performance: expect.any(Object),
            evidenceCount: expect.any(Object),
            lastPlayedAt: expect.any(Object),
            game: expect.any(Object),
            user: expect.any(Object),
            userProfile: expect.any(Object),
          }),
        );
      });
    });

    describe('getGameProgress', () => {
      it('should return NewGameProgress for default GameProgress initial value', () => {
        const formGroup = service.createGameProgressFormGroup(sampleWithNewData);

        const gameProgress = service.getGameProgress(formGroup);

        expect(gameProgress).toMatchObject(sampleWithNewData);
      });

      it('should return NewGameProgress for empty GameProgress initial value', () => {
        const formGroup = service.createGameProgressFormGroup();

        const gameProgress = service.getGameProgress(formGroup);

        expect(gameProgress).toMatchObject({});
      });

      it('should return IGameProgress', () => {
        const formGroup = service.createGameProgressFormGroup(sampleWithRequiredData);

        const gameProgress = service.getGameProgress(formGroup);

        expect(gameProgress).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IGameProgress should not enable id FormControl', () => {
        const formGroup = service.createGameProgressFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewGameProgress should disable id FormControl', () => {
        const formGroup = service.createGameProgressFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
