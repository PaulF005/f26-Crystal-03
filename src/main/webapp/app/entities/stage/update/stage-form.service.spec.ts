import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../stage.test-samples';

import { StageFormService } from './stage-form.service';

describe('Stage Form Service', () => {
  let service: StageFormService;

  beforeEach(() => {
    service = TestBed.inject(StageFormService);
  });

  describe('Service methods', () => {
    describe('createStageFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createStageFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            question: expect.any(Object),
            scenario: expect.any(Object),
          }),
        );
      });

      it('passing IStage should create a new form with FormGroup', () => {
        const formGroup = service.createStageFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            question: expect.any(Object),
            scenario: expect.any(Object),
          }),
        );
      });
    });

    describe('getStage', () => {
      it('should return NewStage for default Stage initial value', () => {
        const formGroup = service.createStageFormGroup(sampleWithNewData);

        const stage = service.getStage(formGroup);

        expect(stage).toMatchObject(sampleWithNewData);
      });

      it('should return NewStage for empty Stage initial value', () => {
        const formGroup = service.createStageFormGroup();

        const stage = service.getStage(formGroup);

        expect(stage).toMatchObject({});
      });

      it('should return IStage', () => {
        const formGroup = service.createStageFormGroup(sampleWithRequiredData);

        const stage = service.getStage(formGroup);

        expect(stage).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IStage should not enable id FormControl', () => {
        const formGroup = service.createStageFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewStage should disable id FormControl', () => {
        const formGroup = service.createStageFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
