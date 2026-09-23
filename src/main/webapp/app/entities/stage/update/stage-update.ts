import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { IQuestion } from 'app/entities/question/question.model';
import { QuestionService } from 'app/entities/question/service/question.service';
import { IScenario } from 'app/entities/scenario/scenario.model';
import { ScenarioService } from 'app/entities/scenario/service/scenario.service';
import { AlertError } from 'app/shared/alert';
import { StageService } from '../service/stage.service';
import { IStage } from '../stage.model';

import { StageFormGroup, StageFormService } from './stage-form.service';

@Component({
  selector: 'jhi-stage-update',
  templateUrl: './stage-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class StageUpdate implements OnInit {
  readonly isSaving = signal(false);
  stage: IStage | null = null;

  questionsSharedCollection = signal<IQuestion[]>([]);
  scenariosSharedCollection = signal<IScenario[]>([]);

  protected stageService = inject(StageService);
  protected stageFormService = inject(StageFormService);
  protected questionService = inject(QuestionService);
  protected scenarioService = inject(ScenarioService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: StageFormGroup = this.stageFormService.createStageFormGroup();

  compareQuestion = (o1: IQuestion | null, o2: IQuestion | null): boolean => this.questionService.compareQuestion(o1, o2);

  compareScenario = (o1: IScenario | null, o2: IScenario | null): boolean => this.scenarioService.compareScenario(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ stage }) => {
      this.stage = stage;
      if (stage) {
        this.updateForm(stage);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const stage = this.stageFormService.getStage(this.editForm);
    if (stage.id === null) {
      this.subscribeToSaveResponse(this.stageService.create(stage));
    } else {
      this.subscribeToSaveResponse(this.stageService.update(stage));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IStage | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(stage: IStage): void {
    this.stage = stage;
    this.stageFormService.resetForm(this.editForm, stage);

    this.questionsSharedCollection.update(questions =>
      this.questionService.addQuestionToCollectionIfMissing<IQuestion>(questions, stage.question),
    );
    this.scenariosSharedCollection.update(scenarios =>
      this.scenarioService.addScenarioToCollectionIfMissing<IScenario>(scenarios, stage.scenario),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.questionService
      .query()
      .pipe(map((res: HttpResponse<IQuestion[]>) => res.body ?? []))
      .pipe(
        map((questions: IQuestion[]) => this.questionService.addQuestionToCollectionIfMissing<IQuestion>(questions, this.stage?.question)),
      )
      .subscribe((questions: IQuestion[]) => this.questionsSharedCollection.set(questions));

    this.scenarioService
      .query()
      .pipe(map((res: HttpResponse<IScenario[]>) => res.body ?? []))
      .pipe(
        map((scenarios: IScenario[]) => this.scenarioService.addScenarioToCollectionIfMissing<IScenario>(scenarios, this.stage?.scenario)),
      )
      .subscribe((scenarios: IScenario[]) => this.scenariosSharedCollection.set(scenarios));
  }
}
