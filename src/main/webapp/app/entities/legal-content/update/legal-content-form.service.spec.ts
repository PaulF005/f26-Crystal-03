import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../legal-content.test-samples';

import { LegalContentFormService } from './legal-content-form.service';

describe('LegalContent Form Service', () => {
  let service: LegalContentFormService;

  beforeEach(() => {
    service = TestBed.inject(LegalContentFormService);
  });

  describe('Service methods', () => {
    describe('createLegalContentFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createLegalContentFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
          }),
        );
      });

      it('passing ILegalContent should create a new form with FormGroup', () => {
        const formGroup = service.createLegalContentFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
          }),
        );
      });
    });

    describe('getLegalContent', () => {
      it('should return NewLegalContent for default LegalContent initial value', () => {
        const formGroup = service.createLegalContentFormGroup(sampleWithNewData);

        const legalContent = service.getLegalContent(formGroup);

        expect(legalContent).toMatchObject(sampleWithNewData);
      });

      it('should return NewLegalContent for empty LegalContent initial value', () => {
        const formGroup = service.createLegalContentFormGroup();

        const legalContent = service.getLegalContent(formGroup);

        expect(legalContent).toMatchObject({});
      });

      it('should return ILegalContent', () => {
        const formGroup = service.createLegalContentFormGroup(sampleWithRequiredData);

        const legalContent = service.getLegalContent(formGroup);

        expect(legalContent).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ILegalContent should not enable id FormControl', () => {
        const formGroup = service.createLegalContentFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewLegalContent should disable id FormControl', () => {
        const formGroup = service.createLegalContentFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
