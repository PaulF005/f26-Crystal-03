import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IConcept } from 'app/entities/concept/concept.model';
import { ConceptService } from 'app/entities/concept/service/concept.service';
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
  let conceptService: ConceptService;

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
    conceptService = TestBed.inject(ConceptService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Concept query and add missing value', () => {
      const question: IQuestion = { id: 15287 };
      const concept: IConcept = { id: 29097 };
      question.concept = concept;

      const conceptCollection: IConcept[] = [{ id: 29097 }];
      vi.spyOn(conceptService, 'query').mockReturnValue(of(new HttpResponse({ body: conceptCollection })));
      const additionalConcepts = [concept];
      const expectedCollection: IConcept[] = [...additionalConcepts, ...conceptCollection];
      vi.spyOn(conceptService, 'addConceptToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ question });
      comp.ngOnInit();

      expect(conceptService.query).toHaveBeenCalled();
      expect(conceptService.addConceptToCollectionIfMissing).toHaveBeenCalledWith(
        conceptCollection,
        ...additionalConcepts.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.conceptsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const question: IQuestion = { id: 15287 };
      const concept: IConcept = { id: 29097 };
      question.concept = concept;

      activatedRoute.data = of({ question });
      comp.ngOnInit();

      expect(comp.conceptsSharedCollection()).toContainEqual(concept);
      expect(comp.question).toEqual(question);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IQuestion>();
      const question = { id: 16375 };
      vi.spyOn(questionFormService, 'getQuestion').mockReturnValue(question);
      vi.spyOn(questionService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
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
      vi.spyOn(questionFormService, 'getQuestion').mockReturnValue({ id: null });
      vi.spyOn(questionService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
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
      vi.spyOn(questionService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
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
    describe('compareConcept', () => {
      it('should forward to conceptService', () => {
        const entity = { id: 29097 };
        const entity2 = { id: 14426 };
        vi.spyOn(conceptService, 'compareConcept');
        comp.compareConcept(entity, entity2);
        expect(conceptService.compareConcept).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
