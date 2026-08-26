import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { TopicService } from 'app/entities/topic/service/topic.service';
import { ITopic } from 'app/entities/topic/topic.model';
import { IQuestion } from '../question.model';
import { QuestionService } from '../service/question.service';

import { QuestionFormService } from './question-form.service';
import { QuestionUpdate } from './question-update';

describe('Question Management Update Component', () => {
  let comp: QuestionUpdate;
  let fixture: ComponentFixture<QuestionUpdate>;
  let activatedRoute: ActivatedRoute;
  let questionFormService: QuestionFormService;
  let questionService: QuestionService;
  let topicService: TopicService;

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

    fixture = TestBed.createComponent(QuestionUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    questionFormService = TestBed.inject(QuestionFormService);
    questionService = TestBed.inject(QuestionService);
    topicService = TestBed.inject(TopicService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Topic query and add missing value', () => {
      const question: IQuestion = { id: 15287 };
      const topic: ITopic = { id: 29581 };
      question.topic = topic;

      const topicCollection: ITopic[] = [{ id: 29581 }];
      vitest.spyOn(topicService, 'query').mockReturnValue(of(new HttpResponse({ body: topicCollection })));
      const additionalTopics = [topic];
      const expectedCollection: ITopic[] = [...additionalTopics, ...topicCollection];
      vitest.spyOn(topicService, 'addTopicToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ question });
      comp.ngOnInit();

      expect(topicService.query).toHaveBeenCalled();
      expect(topicService.addTopicToCollectionIfMissing).toHaveBeenCalledWith(
        topicCollection,
        ...additionalTopics.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.topicsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const question: IQuestion = { id: 15287 };
      const topic: ITopic = { id: 29581 };
      question.topic = topic;

      activatedRoute.data = of({ question });
      comp.ngOnInit();

      expect(comp.topicsSharedCollection()).toContainEqual(topic);
      expect(comp.question).toEqual(question);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IQuestion>();
      const question = { id: 16375 };
      vitest.spyOn(questionFormService, 'getQuestion').mockReturnValue(question);
      vitest.spyOn(questionService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ question });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(question);
      saveSubject.complete();

      // THEN
      expect(questionFormService.getQuestion).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(questionService.update).toHaveBeenCalledWith(expect.objectContaining(question));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IQuestion>();
      const question = { id: 16375 };
      vitest.spyOn(questionFormService, 'getQuestion').mockReturnValue({ id: null });
      vitest.spyOn(questionService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ question: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(question);
      saveSubject.complete();

      // THEN
      expect(questionFormService.getQuestion).toHaveBeenCalled();
      expect(questionService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IQuestion>();
      const question = { id: 16375 };
      vitest.spyOn(questionService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ question });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(questionService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTopic', () => {
      it('should forward to topicService', () => {
        const entity = { id: 29581 };
        const entity2 = { id: 14122 };
        vitest.spyOn(topicService, 'compareTopic');
        comp.compareTopic(entity, entity2);
        expect(topicService.compareTopic).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
