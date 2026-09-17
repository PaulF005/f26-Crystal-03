import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../feedback.test-samples';

import { FeedbackFormService } from './feedback-form.service';

describe('Feedback Form Service', () => {
  let service: FeedbackFormService;

  beforeEach(() => {
    service = TestBed.inject(FeedbackFormService);
  });

  describe('Service methods', () => {
    describe('createFeedbackFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createFeedbackFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            feedback: expect.any(Object),
            explanation: expect.any(Object),
          }),
        );
      });

      it('passing IFeedback should create a new form with FormGroup', () => {
        const formGroup = service.createFeedbackFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            feedback: expect.any(Object),
            explanation: expect.any(Object),
          }),
        );
      });
    });

    describe('getFeedback', () => {
      it('should return NewFeedback for default Feedback initial value', () => {
        const formGroup = service.createFeedbackFormGroup(sampleWithNewData);

        const feedback = service.getFeedback(formGroup);

        expect(feedback).toMatchObject(sampleWithNewData);
      });

      it('should return NewFeedback for empty Feedback initial value', () => {
        const formGroup = service.createFeedbackFormGroup();

        const feedback = service.getFeedback(formGroup);

        expect(feedback).toMatchObject({});
      });

      it('should return IFeedback', () => {
        const formGroup = service.createFeedbackFormGroup(sampleWithRequiredData);

        const feedback = service.getFeedback(formGroup);

        expect(feedback).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IFeedback should not enable id FormControl', () => {
        const formGroup = service.createFeedbackFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewFeedback should disable id FormControl', () => {
        const formGroup = service.createFeedbackFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
