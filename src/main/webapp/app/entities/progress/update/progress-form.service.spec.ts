import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../progress.test-samples';

import { ProgressFormService } from './progress-form.service';

describe('Progress Form Service', () => {
  let service: ProgressFormService;

  beforeEach(() => {
    service = TestBed.inject(ProgressFormService);
  });

  describe('Service methods', () => {
    describe('createProgressFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProgressFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            moduleCompletion: expect.any(Object),
            module: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });

      it('passing IProgress should create a new form with FormGroup', () => {
        const formGroup = service.createProgressFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            moduleCompletion: expect.any(Object),
            module: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });
    });

    describe('getProgress', () => {
      it('should return NewProgress for default Progress initial value', () => {
        const formGroup = service.createProgressFormGroup(sampleWithNewData);

        const progress = service.getProgress(formGroup);

        expect(progress).toMatchObject(sampleWithNewData);
      });

      it('should return NewProgress for empty Progress initial value', () => {
        const formGroup = service.createProgressFormGroup();

        const progress = service.getProgress(formGroup);

        expect(progress).toMatchObject({});
      });

      it('should return IProgress', () => {
        const formGroup = service.createProgressFormGroup(sampleWithRequiredData);

        const progress = service.getProgress(formGroup);

        expect(progress).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProgress should not enable id FormControl', () => {
        const formGroup = service.createProgressFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProgress should disable id FormControl', () => {
        const formGroup = service.createProgressFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
