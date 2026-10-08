import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../concept-progress.test-samples';

import { ConceptProgressFormService } from './concept-progress-form.service';

describe('ConceptProgress Form Service', () => {
  let service: ConceptProgressFormService;

  beforeEach(() => {
    service = TestBed.inject(ConceptProgressFormService);
  });

  describe('Service methods', () => {
    describe('createConceptProgressFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createConceptProgressFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            competency: expect.any(Object),
            improvement: expect.any(Object),
            evidenceCount: expect.any(Object),
            lastPracticedAt: expect.any(Object),
            maxQuestions: expect.any(Object),
            userProfile: expect.any(Object),
            concept: expect.any(Object),
          }),
        );
      });

      it('passing IConceptProgress should create a new form with FormGroup', () => {
        const formGroup = service.createConceptProgressFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            competency: expect.any(Object),
            improvement: expect.any(Object),
            evidenceCount: expect.any(Object),
            lastPracticedAt: expect.any(Object),
            maxQuestions: expect.any(Object),
            userProfile: expect.any(Object),
            concept: expect.any(Object),
          }),
        );
      });
    });

    describe('getConceptProgress', () => {
      it('should return NewConceptProgress for default ConceptProgress initial value', () => {
        const formGroup = service.createConceptProgressFormGroup(sampleWithNewData);

        const conceptProgress = service.getConceptProgress(formGroup);

        expect(conceptProgress).toMatchObject(sampleWithNewData);
      });

      it('should return NewConceptProgress for empty ConceptProgress initial value', () => {
        const formGroup = service.createConceptProgressFormGroup();

        const conceptProgress = service.getConceptProgress(formGroup);

        expect(conceptProgress).toMatchObject({});
      });

      it('should return IConceptProgress', () => {
        const formGroup = service.createConceptProgressFormGroup(sampleWithRequiredData);

        const conceptProgress = service.getConceptProgress(formGroup);

        expect(conceptProgress).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IConceptProgress should not enable id FormControl', () => {
        const formGroup = service.createConceptProgressFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewConceptProgress should disable id FormControl', () => {
        const formGroup = service.createConceptProgressFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
