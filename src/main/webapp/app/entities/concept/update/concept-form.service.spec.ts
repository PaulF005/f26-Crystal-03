import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../concept.test-samples';

import { ConceptFormService } from './concept-form.service';

describe('Concept Form Service', () => {
  let service: ConceptFormService;

  beforeEach(() => {
    service = TestBed.inject(ConceptFormService);
  });

  describe('Service methods', () => {
    describe('createConceptFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createConceptFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            explanation: expect.any(Object),
            legalContent: expect.any(Object),
            topic: expect.any(Object),
          }),
        );
      });

      it('passing IConcept should create a new form with FormGroup', () => {
        const formGroup = service.createConceptFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            explanation: expect.any(Object),
            legalContent: expect.any(Object),
            topic: expect.any(Object),
          }),
        );
      });
    });

    describe('getConcept', () => {
      it('should return NewConcept for default Concept initial value', () => {
        const formGroup = service.createConceptFormGroup(sampleWithNewData);

        const concept = service.getConcept(formGroup);

        expect(concept).toMatchObject(sampleWithNewData);
      });

      it('should return NewConcept for empty Concept initial value', () => {
        const formGroup = service.createConceptFormGroup();

        const concept = service.getConcept(formGroup);

        expect(concept).toMatchObject({});
      });

      it('should return IConcept', () => {
        const formGroup = service.createConceptFormGroup(sampleWithRequiredData);

        const concept = service.getConcept(formGroup);

        expect(concept).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IConcept should not enable id FormControl', () => {
        const formGroup = service.createConceptFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewConcept should disable id FormControl', () => {
        const formGroup = service.createConceptFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
