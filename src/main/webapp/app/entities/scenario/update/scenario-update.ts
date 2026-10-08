import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { IGame } from 'app/entities/game/game.model';
import { GameService } from 'app/entities/game/service/game.service';
import { StageService } from 'app/entities/stage/service/stage.service';
import { IStage } from 'app/entities/stage/stage.model';
import { TopicService } from 'app/entities/topic/service/topic.service';
import { ITopic } from 'app/entities/topic/topic.model';
import { AlertError } from 'app/shared/alert';
import { IScenario } from '../scenario.model';
import { ScenarioService } from '../service/scenario.service';

import { ScenarioFormGroup, ScenarioFormService } from './scenario-form.service';

@Component({
  selector: 'jhi-scenario-update',
  templateUrl: './scenario-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ScenarioUpdate implements OnInit {
  readonly isSaving = signal(false);
  scenario: IScenario | null = null;

  stagesSharedCollection = signal<IStage[]>([]);
  topicsSharedCollection = signal<ITopic[]>([]);
  gamesSharedCollection = signal<IGame[]>([]);

  protected scenarioService = inject(ScenarioService);
  protected scenarioFormService = inject(ScenarioFormService);
  protected stageService = inject(StageService);
  protected topicService = inject(TopicService);
  protected gameService = inject(GameService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ScenarioFormGroup = this.scenarioFormService.createScenarioFormGroup();

  compareStage = (o1: IStage | null, o2: IStage | null): boolean => this.stageService.compareStage(o1, o2);

  compareTopic = (o1: ITopic | null, o2: ITopic | null): boolean => this.topicService.compareTopic(o1, o2);

  compareGame = (o1: IGame | null, o2: IGame | null): boolean => this.gameService.compareGame(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ scenario }) => {
      this.scenario = scenario;
      if (scenario) {
        this.updateForm(scenario);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const scenario = this.scenarioFormService.getScenario(this.editForm);
    if (scenario.id === null) {
      this.subscribeToSaveResponse(this.scenarioService.create(scenario));
    } else {
      this.subscribeToSaveResponse(this.scenarioService.update(scenario));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IScenario | null>): void {
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

  protected updateForm(scenario: IScenario): void {
    this.scenario = scenario;
    this.scenarioFormService.resetForm(this.editForm, scenario);

    this.stagesSharedCollection.update(stages => this.stageService.addStageToCollectionIfMissing<IStage>(stages, scenario.startingStage));
    this.topicsSharedCollection.update(topics => this.topicService.addTopicToCollectionIfMissing<ITopic>(topics, scenario.topic));
    this.gamesSharedCollection.update(games => this.gameService.addGameToCollectionIfMissing<IGame>(games, scenario.game));
  }

  protected loadRelationshipsOptions(): void {
    this.stageService
      .query()
      .pipe(map((res: HttpResponse<IStage[]>) => res.body ?? []))
      .pipe(map((stages: IStage[]) => this.stageService.addStageToCollectionIfMissing<IStage>(stages, this.scenario?.startingStage)))
      .subscribe((stages: IStage[]) => this.stagesSharedCollection.set(stages));

    this.topicService
      .query()
      .pipe(map((res: HttpResponse<ITopic[]>) => res.body ?? []))
      .pipe(map((topics: ITopic[]) => this.topicService.addTopicToCollectionIfMissing<ITopic>(topics, this.scenario?.topic)))
      .subscribe((topics: ITopic[]) => this.topicsSharedCollection.set(topics));

    this.gameService
      .query()
      .pipe(map((res: HttpResponse<IGame[]>) => res.body ?? []))
      .pipe(map((games: IGame[]) => this.gameService.addGameToCollectionIfMissing<IGame>(games, this.scenario?.game)))
      .subscribe((games: IGame[]) => this.gamesSharedCollection.set(games));
  }
}
