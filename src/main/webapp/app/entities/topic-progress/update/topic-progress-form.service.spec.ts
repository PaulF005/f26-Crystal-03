import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../topic-progress.test-samples';

import { TopicProgressFormService } from './topic-progress-form.service';

describe('TopicProgress Form Service', () => {
  let service: TopicProgressFormService;

  beforeEach(() => {
    service = TestBed.inject(TopicProgressFormService);
  });

  describe('Service methods', () => {
    describe('createTopicProgressFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTopicProgressFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            competency: expect.any(Object),
            improvement: expect.any(Object),
            evidenceCount: expect.any(Object),
            lastPracticedAt: expect.any(Object),
            topic: expect.any(Object),
            userProfile: expect.any(Object),
          }),
        );
      });

      it('passing ITopicProgress should create a new form with FormGroup', () => {
        const formGroup = service.createTopicProgressFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            competency: expect.any(Object),
            improvement: expect.any(Object),
            evidenceCount: expect.any(Object),
            lastPracticedAt: expect.any(Object),
            topic: expect.any(Object),
            userProfile: expect.any(Object),
          }),
        );
      });
    });

    describe('getTopicProgress', () => {
      it('should return NewTopicProgress for default TopicProgress initial value', () => {
        const formGroup = service.createTopicProgressFormGroup(sampleWithNewData);

        const topicProgress = service.getTopicProgress(formGroup);

        expect(topicProgress).toMatchObject(sampleWithNewData);
      });

      it('should return NewTopicProgress for empty TopicProgress initial value', () => {
        const formGroup = service.createTopicProgressFormGroup();

        const topicProgress = service.getTopicProgress(formGroup);

        expect(topicProgress).toMatchObject({});
      });

      it('should return ITopicProgress', () => {
        const formGroup = service.createTopicProgressFormGroup(sampleWithRequiredData);

        const topicProgress = service.getTopicProgress(formGroup);

        expect(topicProgress).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITopicProgress should not enable id FormControl', () => {
        const formGroup = service.createTopicProgressFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTopicProgress should disable id FormControl', () => {
        const formGroup = service.createTopicProgressFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
