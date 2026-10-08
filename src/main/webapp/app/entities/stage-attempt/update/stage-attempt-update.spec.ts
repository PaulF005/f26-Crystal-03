import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IAnswer } from 'app/entities/answer/answer.model';
import { AnswerService } from 'app/entities/answer/service/answer.service';
import { IGameSession } from 'app/entities/game-session/game-session.model';
import { GameSessionService } from 'app/entities/game-session/service/game-session.service';
import { StageService } from 'app/entities/stage/service/stage.service';
import { IStage } from 'app/entities/stage/stage.model';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { StageAttemptService } from '../service/stage-attempt.service';
import { IStageAttempt } from '../stage-attempt.model';

import { StageAttemptFormService } from './stage-attempt-form.service';
import { StageAttemptUpdate } from './stage-attempt-update';

describe('StageAttempt Management Update Component', () => {
  let comp: StageAttemptUpdate;
  let fixture: ComponentFixture<StageAttemptUpdate>;
  let activatedRoute: ActivatedRoute;
  let stageAttemptFormService: StageAttemptFormService;
  let stageAttemptService: StageAttemptService;
  let userProfileService: UserProfileService;
  let stageService: StageService;
  let answerService: AnswerService;
  let gameSessionService: GameSessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(StageAttemptUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    stageAttemptFormService = TestBed.inject(StageAttemptFormService);
    stageAttemptService = TestBed.inject(StageAttemptService);
    userProfileService = TestBed.inject(UserProfileService);
    stageService = TestBed.inject(StageService);
    answerService = TestBed.inject(AnswerService);
    gameSessionService = TestBed.inject(GameSessionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call UserProfile query and add missing value', () => {
      const stageAttempt: IStageAttempt = { id: 30692 };
      const user: IUserProfile = { id: 22058 };
      stageAttempt.user = user;

      const userProfileCollection: IUserProfile[] = [{ id: 22058 }];
      vitest.spyOn(userProfileService, 'query').mockReturnValue(of(new HttpResponse({ body: userProfileCollection })));
      const additionalUserProfiles = [user];
      const expectedCollection: IUserProfile[] = [...additionalUserProfiles, ...userProfileCollection];
      vitest.spyOn(userProfileService, 'addUserProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ stageAttempt });
      comp.ngOnInit();

      expect(userProfileService.query).toHaveBeenCalled();
      expect(userProfileService.addUserProfileToCollectionIfMissing).toHaveBeenCalledWith(
        userProfileCollection,
        ...additionalUserProfiles.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.userProfilesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Stage query and add missing value', () => {
      const stageAttempt: IStageAttempt = { id: 30692 };
      const stage: IStage = { id: 30579 };
      stageAttempt.stage = stage;

      const stageCollection: IStage[] = [{ id: 30579 }];
      vitest.spyOn(stageService, 'query').mockReturnValue(of(new HttpResponse({ body: stageCollection })));
      const additionalStages = [stage];
      const expectedCollection: IStage[] = [...additionalStages, ...stageCollection];
      vitest.spyOn(stageService, 'addStageToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ stageAttempt });
      comp.ngOnInit();

      expect(stageService.query).toHaveBeenCalled();
      expect(stageService.addStageToCollectionIfMissing).toHaveBeenCalledWith(
        stageCollection,
        ...additionalStages.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.stagesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Answer query and add missing value', () => {
      const stageAttempt: IStageAttempt = { id: 30692 };
      const selectedAnswer: IAnswer = { id: 19540 };
      stageAttempt.selectedAnswer = selectedAnswer;

      const answerCollection: IAnswer[] = [{ id: 19540 }];
      vitest.spyOn(answerService, 'query').mockReturnValue(of(new HttpResponse({ body: answerCollection })));
      const additionalAnswers = [selectedAnswer];
      const expectedCollection: IAnswer[] = [...additionalAnswers, ...answerCollection];
      vitest.spyOn(answerService, 'addAnswerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ stageAttempt });
      comp.ngOnInit();

      expect(answerService.query).toHaveBeenCalled();
      expect(answerService.addAnswerToCollectionIfMissing).toHaveBeenCalledWith(
        answerCollection,
        ...additionalAnswers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.answersSharedCollection()).toEqual(expectedCollection);
    });

    it('should call GameSession query and add missing value', () => {
      const stageAttempt: IStageAttempt = { id: 30692 };
      const gameSession: IGameSession = { id: 30007 };
      stageAttempt.gameSession = gameSession;

      const gameSessionCollection: IGameSession[] = [{ id: 30007 }];
      vitest.spyOn(gameSessionService, 'query').mockReturnValue(of(new HttpResponse({ body: gameSessionCollection })));
      const additionalGameSessions = [gameSession];
      const expectedCollection: IGameSession[] = [...additionalGameSessions, ...gameSessionCollection];
      vitest.spyOn(gameSessionService, 'addGameSessionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ stageAttempt });
      comp.ngOnInit();

      expect(gameSessionService.query).toHaveBeenCalled();
      expect(gameSessionService.addGameSessionToCollectionIfMissing).toHaveBeenCalledWith(
        gameSessionCollection,
        ...additionalGameSessions.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.gameSessionsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const stageAttempt: IStageAttempt = { id: 30692 };
      const user: IUserProfile = { id: 22058 };
      stageAttempt.user = user;
      const stage: IStage = { id: 30579 };
      stageAttempt.stage = stage;
      const selectedAnswer: IAnswer = { id: 19540 };
      stageAttempt.selectedAnswer = selectedAnswer;
      const gameSession: IGameSession = { id: 30007 };
      stageAttempt.gameSession = gameSession;

      activatedRoute.data = of({ stageAttempt });
      comp.ngOnInit();

      expect(comp.userProfilesSharedCollection()).toContainEqual(user);
      expect(comp.stagesSharedCollection()).toContainEqual(stage);
      expect(comp.answersSharedCollection()).toContainEqual(selectedAnswer);
      expect(comp.gameSessionsSharedCollection()).toContainEqual(gameSession);
      expect(comp.stageAttempt).toEqual(stageAttempt);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IStageAttempt>();
      const stageAttempt = { id: 23225 };
      vitest.spyOn(stageAttemptFormService, 'getStageAttempt').mockReturnValue(stageAttempt);
      vitest.spyOn(stageAttemptService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stageAttempt });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(stageAttempt);
      saveSubject.complete();

      // THEN
      expect(stageAttemptFormService.getStageAttempt).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(stageAttemptService.update).toHaveBeenCalledWith(expect.objectContaining(stageAttempt));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IStageAttempt>();
      const stageAttempt = { id: 23225 };
      vitest.spyOn(stageAttemptFormService, 'getStageAttempt').mockReturnValue({ id: null });
      vitest.spyOn(stageAttemptService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stageAttempt: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(stageAttempt);
      saveSubject.complete();

      // THEN
      expect(stageAttemptFormService.getStageAttempt).toHaveBeenCalled();
      expect(stageAttemptService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IStageAttempt>();
      const stageAttempt = { id: 23225 };
      vitest.spyOn(stageAttemptService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stageAttempt });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(stageAttemptService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUserProfile', () => {
      it('should forward to userProfileService', () => {
        const entity = { id: 22058 };
        const entity2 = { id: 9009 };
        vitest.spyOn(userProfileService, 'compareUserProfile');
        comp.compareUserProfile(entity, entity2);
        expect(userProfileService.compareUserProfile).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareStage', () => {
      it('should forward to stageService', () => {
        const entity = { id: 30579 };
        const entity2 = { id: 6829 };
        vitest.spyOn(stageService, 'compareStage');
        comp.compareStage(entity, entity2);
        expect(stageService.compareStage).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareAnswer', () => {
      it('should forward to answerService', () => {
        const entity = { id: 19540 };
        const entity2 = { id: 25690 };
        vitest.spyOn(answerService, 'compareAnswer');
        comp.compareAnswer(entity, entity2);
        expect(answerService.compareAnswer).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareGameSession', () => {
      it('should forward to gameSessionService', () => {
        const entity = { id: 30007 };
        const entity2 = { id: 5692 };
        vitest.spyOn(gameSessionService, 'compareGameSession');
        comp.compareGameSession(entity, entity2);
        expect(gameSessionService.compareGameSession).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
