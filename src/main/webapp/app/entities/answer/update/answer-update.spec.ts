import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IFeedback } from 'app/entities/feedback/feedback.model';
import { FeedbackService } from 'app/entities/feedback/service/feedback.service';
import { IQuestion } from 'app/entities/question/question.model';
import { QuestionService } from 'app/entities/question/service/question.service';
import { StageService } from 'app/entities/stage/service/stage.service';
import { IStage } from 'app/entities/stage/stage.model';
import { IAnswer } from '../answer.model';
import { AnswerService } from '../service/answer.service';

import { AnswerFormService } from './answer-form.service';
import { AnswerUpdate } from './answer-update';

describe('Answer Management Update Component', () => {
  let comp: AnswerUpdate;
  let fixture: ComponentFixture<AnswerUpdate>;
  let activatedRoute: ActivatedRoute;
  let answerFormService: AnswerFormService;
  let answerService: AnswerService;
  let stageService: StageService;
  let feedbackService: FeedbackService;
  let questionService: QuestionService;

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

    fixture = TestBed.createComponent(AnswerUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    answerFormService = TestBed.inject(AnswerFormService);
    answerService = TestBed.inject(AnswerService);
    stageService = TestBed.inject(StageService);
    feedbackService = TestBed.inject(FeedbackService);
    questionService = TestBed.inject(QuestionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Stage query and add missing value', () => {
      const answer: IAnswer = { id: 25690 };
      const nextStage: IStage = { id: 30579 };
      answer.nextStage = nextStage;

      const stageCollection: IStage[] = [{ id: 30579 }];
      vitest.spyOn(stageService, 'query').mockReturnValue(of(new HttpResponse({ body: stageCollection })));
      const additionalStages = [nextStage];
      const expectedCollection: IStage[] = [...additionalStages, ...stageCollection];
      vitest.spyOn(stageService, 'addStageToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ answer });
      comp.ngOnInit();

      expect(stageService.query).toHaveBeenCalled();
      expect(stageService.addStageToCollectionIfMissing).toHaveBeenCalledWith(
        stageCollection,
        ...additionalStages.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.stagesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Feedback query and add missing value', () => {
      const answer: IAnswer = { id: 25690 };
      const feedback: IFeedback = { id: 10592 };
      answer.feedback = feedback;

      const feedbackCollection: IFeedback[] = [{ id: 10592 }];
      vitest.spyOn(feedbackService, 'query').mockReturnValue(of(new HttpResponse({ body: feedbackCollection })));
      const additionalFeedbacks = [feedback];
      const expectedCollection: IFeedback[] = [...additionalFeedbacks, ...feedbackCollection];
      vitest.spyOn(feedbackService, 'addFeedbackToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ answer });
      comp.ngOnInit();

      expect(feedbackService.query).toHaveBeenCalled();
      expect(feedbackService.addFeedbackToCollectionIfMissing).toHaveBeenCalledWith(
        feedbackCollection,
        ...additionalFeedbacks.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.feedbacksSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Question query and add missing value', () => {
      const answer: IAnswer = { id: 25690 };
      const question: IQuestion = { id: 16375 };
      answer.question = question;

      const questionCollection: IQuestion[] = [{ id: 16375 }];
      vitest.spyOn(questionService, 'query').mockReturnValue(of(new HttpResponse({ body: questionCollection })));
      const additionalQuestions = [question];
      const expectedCollection: IQuestion[] = [...additionalQuestions, ...questionCollection];
      vitest.spyOn(questionService, 'addQuestionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ answer });
      comp.ngOnInit();

      expect(questionService.query).toHaveBeenCalled();
      expect(questionService.addQuestionToCollectionIfMissing).toHaveBeenCalledWith(
        questionCollection,
        ...additionalQuestions.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.questionsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const answer: IAnswer = { id: 25690 };
      const nextStage: IStage = { id: 30579 };
      answer.nextStage = nextStage;
      const feedback: IFeedback = { id: 10592 };
      answer.feedback = feedback;
      const question: IQuestion = { id: 16375 };
      answer.question = question;

      activatedRoute.data = of({ answer });
      comp.ngOnInit();

      expect(comp.stagesSharedCollection()).toContainEqual(nextStage);
      expect(comp.feedbacksSharedCollection()).toContainEqual(feedback);
      expect(comp.questionsSharedCollection()).toContainEqual(question);
      expect(comp.answer).toEqual(answer);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAnswer>();
      const answer = { id: 19540 };
      vitest.spyOn(answerFormService, 'getAnswer').mockReturnValue(answer);
      vitest.spyOn(answerService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ answer });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(answer);
      saveSubject.complete();

      // THEN
      expect(answerFormService.getAnswer).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(answerService.update).toHaveBeenCalledWith(expect.objectContaining(answer));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAnswer>();
      const answer = { id: 19540 };
      vitest.spyOn(answerFormService, 'getAnswer').mockReturnValue({ id: null });
      vitest.spyOn(answerService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ answer: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(answer);
      saveSubject.complete();

      // THEN
      expect(answerFormService.getAnswer).toHaveBeenCalled();
      expect(answerService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IAnswer>();
      const answer = { id: 19540 };
      vitest.spyOn(answerService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ answer });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(answerService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareStage', () => {
      it('should forward to stageService', () => {
        const entity = { id: 30579 };
        const entity2 = { id: 6829 };
        vitest.spyOn(stageService, 'compareStage');
        comp.compareStage(entity, entity2);
        expect(stageService.compareStage).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareFeedback', () => {
      it('should forward to feedbackService', () => {
        const entity = { id: 10592 };
        const entity2 = { id: 1452 };
        vitest.spyOn(feedbackService, 'compareFeedback');
        comp.compareFeedback(entity, entity2);
        expect(feedbackService.compareFeedback).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareQuestion', () => {
      it('should forward to questionService', () => {
        const entity = { id: 16375 };
        const entity2 = { id: 15287 };
        vitest.spyOn(questionService, 'compareQuestion');
        comp.compareQuestion(entity, entity2);
        expect(questionService.compareQuestion).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
