import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../stage-attempt.test-samples';

import { StageAttemptFormService } from './stage-attempt-form.service';

describe('StageAttempt Form Service', () => {
  let service: StageAttemptFormService;

  beforeEach(() => {
    service = TestBed.inject(StageAttemptFormService);
  });

  describe('Service methods', () => {
    describe('createStageAttemptFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createStageAttemptFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            answeredAt: expect.any(Object),
            correct: expect.any(Object),
            user: expect.any(Object),
            stage: expect.any(Object),
            selectedAnswer: expect.any(Object),
            gameSession: expect.any(Object),
          }),
        );
      });

      it('passing IStageAttempt should create a new form with FormGroup', () => {
        const formGroup = service.createStageAttemptFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            answeredAt: expect.any(Object),
            correct: expect.any(Object),
            user: expect.any(Object),
            stage: expect.any(Object),
            selectedAnswer: expect.any(Object),
            gameSession: expect.any(Object),
          }),
        );
      });
    });

    describe('getStageAttempt', () => {
      it('should return NewStageAttempt for default StageAttempt initial value', () => {
        const formGroup = service.createStageAttemptFormGroup(sampleWithNewData);

        const stageAttempt = service.getStageAttempt(formGroup);

        expect(stageAttempt).toMatchObject(sampleWithNewData);
      });

      it('should return NewStageAttempt for empty StageAttempt initial value', () => {
        const formGroup = service.createStageAttemptFormGroup();

        const stageAttempt = service.getStageAttempt(formGroup);

        expect(stageAttempt).toMatchObject({});
      });

      it('should return IStageAttempt', () => {
        const formGroup = service.createStageAttemptFormGroup(sampleWithRequiredData);

        const stageAttempt = service.getStageAttempt(formGroup);

        expect(stageAttempt).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IStageAttempt should not enable id FormControl', () => {
        const formGroup = service.createStageAttemptFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewStageAttempt should disable id FormControl', () => {
        const formGroup = service.createStageAttemptFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
