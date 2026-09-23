import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { IAnswer } from 'app/entities/answer/answer.model';
import { AnswerService } from 'app/entities/answer/service/answer.service';
import { IGameSession } from 'app/entities/game-session/game-session.model';
import { GameSessionService } from 'app/entities/game-session/service/game-session.service';
import { StageService } from 'app/entities/stage/service/stage.service';
import { IStage } from 'app/entities/stage/stage.model';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { AlertError } from 'app/shared/alert';
import { StageAttemptService } from '../service/stage-attempt.service';
import { IStageAttempt } from '../stage-attempt.model';

import { StageAttemptFormGroup, StageAttemptFormService } from './stage-attempt-form.service';

@Component({
  selector: 'jhi-stage-attempt-update',
  templateUrl: './stage-attempt-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class StageAttemptUpdate implements OnInit {
  readonly isSaving = signal(false);
  stageAttempt: IStageAttempt | null = null;

  userProfilesSharedCollection = signal<IUserProfile[]>([]);
  stagesSharedCollection = signal<IStage[]>([]);
  answersSharedCollection = signal<IAnswer[]>([]);
  gameSessionsSharedCollection = signal<IGameSession[]>([]);

  protected stageAttemptService = inject(StageAttemptService);
  protected stageAttemptFormService = inject(StageAttemptFormService);
  protected userProfileService = inject(UserProfileService);
  protected stageService = inject(StageService);
  protected answerService = inject(AnswerService);
  protected gameSessionService = inject(GameSessionService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: StageAttemptFormGroup = this.stageAttemptFormService.createStageAttemptFormGroup();

  compareUserProfile = (o1: IUserProfile | null, o2: IUserProfile | null): boolean => this.userProfileService.compareUserProfile(o1, o2);

  compareStage = (o1: IStage | null, o2: IStage | null): boolean => this.stageService.compareStage(o1, o2);

  compareAnswer = (o1: IAnswer | null, o2: IAnswer | null): boolean => this.answerService.compareAnswer(o1, o2);

  compareGameSession = (o1: IGameSession | null, o2: IGameSession | null): boolean => this.gameSessionService.compareGameSession(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ stageAttempt }) => {
      this.stageAttempt = stageAttempt;
      if (stageAttempt) {
        this.updateForm(stageAttempt);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const stageAttempt = this.stageAttemptFormService.getStageAttempt(this.editForm);
    if (stageAttempt.id === null) {
      this.subscribeToSaveResponse(this.stageAttemptService.create(stageAttempt));
    } else {
      this.subscribeToSaveResponse(this.stageAttemptService.update(stageAttempt));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IStageAttempt | null>): void {
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

  protected updateForm(stageAttempt: IStageAttempt): void {
    this.stageAttempt = stageAttempt;
    this.stageAttemptFormService.resetForm(this.editForm, stageAttempt);

    this.userProfilesSharedCollection.update(userProfiles =>
      this.userProfileService.addUserProfileToCollectionIfMissing<IUserProfile>(userProfiles, stageAttempt.user),
    );
    this.stagesSharedCollection.update(stages => this.stageService.addStageToCollectionIfMissing<IStage>(stages, stageAttempt.stage));
    this.answersSharedCollection.update(answers =>
      this.answerService.addAnswerToCollectionIfMissing<IAnswer>(answers, stageAttempt.selectedAnswer),
    );
    this.gameSessionsSharedCollection.update(gameSessions =>
      this.gameSessionService.addGameSessionToCollectionIfMissing<IGameSession>(gameSessions, stageAttempt.gameSession),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userProfileService
      .query()
      .pipe(map((res: HttpResponse<IUserProfile[]>) => res.body ?? []))
      .pipe(
        map((userProfiles: IUserProfile[]) =>
          this.userProfileService.addUserProfileToCollectionIfMissing<IUserProfile>(userProfiles, this.stageAttempt?.user),
        ),
      )
      .subscribe((userProfiles: IUserProfile[]) => this.userProfilesSharedCollection.set(userProfiles));

    this.stageService
      .query()
      .pipe(map((res: HttpResponse<IStage[]>) => res.body ?? []))
      .pipe(map((stages: IStage[]) => this.stageService.addStageToCollectionIfMissing<IStage>(stages, this.stageAttempt?.stage)))
      .subscribe((stages: IStage[]) => this.stagesSharedCollection.set(stages));

    this.answerService
      .query()
      .pipe(map((res: HttpResponse<IAnswer[]>) => res.body ?? []))
      .pipe(
        map((answers: IAnswer[]) => this.answerService.addAnswerToCollectionIfMissing<IAnswer>(answers, this.stageAttempt?.selectedAnswer)),
      )
      .subscribe((answers: IAnswer[]) => this.answersSharedCollection.set(answers));

    this.gameSessionService
      .query()
      .pipe(map((res: HttpResponse<IGameSession[]>) => res.body ?? []))
      .pipe(
        map((gameSessions: IGameSession[]) =>
          this.gameSessionService.addGameSessionToCollectionIfMissing<IGameSession>(gameSessions, this.stageAttempt?.gameSession),
        ),
      )
      .subscribe((gameSessions: IGameSession[]) => this.gameSessionsSharedCollection.set(gameSessions));
  }
}
