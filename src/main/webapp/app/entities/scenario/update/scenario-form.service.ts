import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IScenario, NewScenario } from '../scenario.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IScenario for edit and NewScenarioFormGroupInput for create.
 */
type ScenarioFormGroupInput = IScenario | PartialWithRequiredKeyOf<NewScenario>;

type ScenarioFormDefaults = Pick<NewScenario, 'id'>;

type ScenarioFormGroupContent = {
  id: FormControl<IScenario['id'] | NewScenario['id']>;
  name: FormControl<IScenario['name']>;
  topic: FormControl<IScenario['topic']>;
  startingStage: FormControl<IScenario['startingStage']>;
  game: FormControl<IScenario['game']>;
};

export type ScenarioFormGroup = FormGroup<ScenarioFormGroupContent>;

@Service()
export class ScenarioFormService {
  createScenarioFormGroup(scenario?: ScenarioFormGroupInput): ScenarioFormGroup {
    const scenarioRawValue = {
      ...this.getFormDefaults(),
      ...(scenario ?? { id: null }),
    };

    return new FormGroup<ScenarioFormGroupContent>({
      id: new FormControl(
        { value: scenarioRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(scenarioRawValue.name, {
        validators: [Validators.required],
      }),
      topic: new FormControl(scenarioRawValue.topic, {
        validators: [Validators.required],
      }),
      startingStage: new FormControl(scenarioRawValue.startingStage, {
        validators: [Validators.required],
      }),
      game: new FormControl(scenarioRawValue.game, {
        validators: [Validators.required],
      }),
    });
  }

  getScenario(form: ScenarioFormGroup): IScenario | NewScenario {
    return form.getRawValue();
  }

  resetForm(form: ScenarioFormGroup, scenario: ScenarioFormGroupInput): void {
    const scenarioRawValue = { ...this.getFormDefaults(), ...scenario };
    form.reset({
      ...scenarioRawValue,
      id: { value: scenarioRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ScenarioFormDefaults {
    return {
      id: null,
    };
  }
}
