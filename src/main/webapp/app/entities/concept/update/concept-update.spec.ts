import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { ILegalContent } from 'app/entities/legal-content/legal-content.model';
import { LegalContentService } from 'app/entities/legal-content/service/legal-content.service';
import { TopicService } from 'app/entities/topic/service/topic.service';
import { ITopic } from 'app/entities/topic/topic.model';
import { IConcept } from '../concept.model';
import { ConceptService } from '../service/concept.service';

import { ConceptFormService } from './concept-form.service';
import { ConceptUpdate } from './concept-update';

describe('Concept Management Update Component', () => {
  let comp: ConceptUpdate;
  let fixture: ComponentFixture<ConceptUpdate>;
  let activatedRoute: ActivatedRoute;
  let conceptFormService: ConceptFormService;
  let conceptService: ConceptService;
  let legalContentService: LegalContentService;
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

    fixture = TestBed.createComponent(ConceptUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    conceptFormService = TestBed.inject(ConceptFormService);
    conceptService = TestBed.inject(ConceptService);
    legalContentService = TestBed.inject(LegalContentService);
    topicService = TestBed.inject(TopicService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call LegalContent query and add missing value', () => {
      const concept: IConcept = { id: 14426 };
      const legalContent: ILegalContent = { id: 7620 };
      concept.legalContent = legalContent;

      const legalContentCollection: ILegalContent[] = [{ id: 7620 }];
      vi.spyOn(legalContentService, 'query').mockReturnValue(of(new HttpResponse({ body: legalContentCollection })));
      const additionalLegalContents = [legalContent];
      const expectedCollection: ILegalContent[] = [...additionalLegalContents, ...legalContentCollection];
      vi.spyOn(legalContentService, 'addLegalContentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ concept });
      comp.ngOnInit();

      expect(legalContentService.query).toHaveBeenCalled();
      expect(legalContentService.addLegalContentToCollectionIfMissing).toHaveBeenCalledWith(
        legalContentCollection,
        ...additionalLegalContents.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.legalContentsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Topic query and add missing value', () => {
      const concept: IConcept = { id: 14426 };
      const topic: ITopic = { id: 29581 };
      concept.topic = topic;

      const topicCollection: ITopic[] = [{ id: 29581 }];
      vi.spyOn(topicService, 'query').mockReturnValue(of(new HttpResponse({ body: topicCollection })));
      const additionalTopics = [topic];
      const expectedCollection: ITopic[] = [...additionalTopics, ...topicCollection];
      vi.spyOn(topicService, 'addTopicToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ concept });
      comp.ngOnInit();

      expect(topicService.query).toHaveBeenCalled();
      expect(topicService.addTopicToCollectionIfMissing).toHaveBeenCalledWith(
        topicCollection,
        ...additionalTopics.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.topicsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const concept: IConcept = { id: 14426 };
      const legalContent: ILegalContent = { id: 7620 };
      concept.legalContent = legalContent;
      const topic: ITopic = { id: 29581 };
      concept.topic = topic;

      activatedRoute.data = of({ concept });
      comp.ngOnInit();

      expect(comp.legalContentsSharedCollection()).toContainEqual(legalContent);
      expect(comp.topicsSharedCollection()).toContainEqual(topic);
      expect(comp.concept).toEqual(concept);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IConcept>();
      const concept = { id: 29097 };
      vi.spyOn(conceptFormService, 'getConcept').mockReturnValue(concept);
      vi.spyOn(conceptService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ concept });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(concept);
      saveSubject.complete();

      // THEN
      expect(conceptFormService.getConcept).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(conceptService.update).toHaveBeenCalledWith(expect.objectContaining(concept));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IConcept>();
      const concept = { id: 29097 };
      vi.spyOn(conceptFormService, 'getConcept').mockReturnValue({ id: null });
      vi.spyOn(conceptService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ concept: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(concept);
      saveSubject.complete();

      // THEN
      expect(conceptFormService.getConcept).toHaveBeenCalled();
      expect(conceptService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IConcept>();
      const concept = { id: 29097 };
      vi.spyOn(conceptService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ concept });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(conceptService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareLegalContent', () => {
      it('should forward to legalContentService', () => {
        const entity = { id: 7620 };
        const entity2 = { id: 29225 };
        vi.spyOn(legalContentService, 'compareLegalContent');
        comp.compareLegalContent(entity, entity2);
        expect(legalContentService.compareLegalContent).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareTopic', () => {
      it('should forward to topicService', () => {
        const entity = { id: 29581 };
        const entity2 = { id: 14122 };
        vi.spyOn(topicService, 'compareTopic');
        comp.compareTopic(entity, entity2);
        expect(topicService.compareTopic).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
