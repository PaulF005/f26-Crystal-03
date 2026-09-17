import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../question.test-samples';

import { QuestionFormService } from './question-form.service';

describe('Question Form Service', () => {
  let service: QuestionFormService;

  beforeEach(() => {
    service = TestBed.inject(QuestionFormService);
  });

  describe('Service methods', () => {
    describe('createQuestionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createQuestionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            question: expect.any(Object),
            concept: expect.any(Object),
          }),
        );
      });

      it('passing IQuestion should create a new form with FormGroup', () => {
        const formGroup = service.createQuestionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            question: expect.any(Object),
            concept: expect.any(Object),
          }),
        );
      });
    });

    describe('getQuestion', () => {
      it('should return NewQuestion for default Question initial value', () => {
        const formGroup = service.createQuestionFormGroup(sampleWithNewData);

        const question = service.getQuestion(formGroup);

        expect(question).toMatchObject(sampleWithNewData);
      });

      it('should return NewQuestion for empty Question initial value', () => {
        const formGroup = service.createQuestionFormGroup();

        const question = service.getQuestion(formGroup);

        expect(question).toMatchObject({});
      });

      it('should return IQuestion', () => {
        const formGroup = service.createQuestionFormGroup(sampleWithRequiredData);

        const question = service.getQuestion(formGroup);

        expect(question).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IQuestion should not enable id FormControl', () => {
        const formGroup = service.createQuestionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewQuestion should disable id FormControl', () => {
        const formGroup = service.createQuestionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
