import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../scenario.test-samples';

import { ScenarioFormService } from './scenario-form.service';

describe('Scenario Form Service', () => {
  let service: ScenarioFormService;

  beforeEach(() => {
    service = TestBed.inject(ScenarioFormService);
  });

  describe('Service methods', () => {
    describe('createScenarioFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createScenarioFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            startingStage: expect.any(Object),
            topic: expect.any(Object),
            game: expect.any(Object),
          }),
        );
      });

      it('passing IScenario should create a new form with FormGroup', () => {
        const formGroup = service.createScenarioFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            startingStage: expect.any(Object),
            topic: expect.any(Object),
            game: expect.any(Object),
          }),
        );
      });
    });

    describe('getScenario', () => {
      it('should return NewScenario for default Scenario initial value', () => {
        const formGroup = service.createScenarioFormGroup(sampleWithNewData);

        const scenario = service.getScenario(formGroup);

        expect(scenario).toMatchObject(sampleWithNewData);
      });

      it('should return NewScenario for empty Scenario initial value', () => {
        const formGroup = service.createScenarioFormGroup();

        const scenario = service.getScenario(formGroup);

        expect(scenario).toMatchObject({});
      });

      it('should return IScenario', () => {
        const formGroup = service.createScenarioFormGroup(sampleWithRequiredData);

        const scenario = service.getScenario(formGroup);

        expect(scenario).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IScenario should not enable id FormControl', () => {
        const formGroup = service.createScenarioFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewScenario should disable id FormControl', () => {
        const formGroup = service.createScenarioFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
