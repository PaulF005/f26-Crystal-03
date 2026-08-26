import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { ILegalContent } from 'app/entities/legal-content/legal-content.model';
import { LegalContentService } from 'app/entities/legal-content/service/legal-content.service';
import { IModule } from 'app/entities/module/module.model';
import { ModuleService } from 'app/entities/module/service/module.service';
import { TopicService } from '../service/topic.service';
import { ITopic } from '../topic.model';

import { TopicFormService } from './topic-form.service';
import { TopicUpdate } from './topic-update';

describe('Topic Management Update Component', () => {
  let comp: TopicUpdate;
  let fixture: ComponentFixture<TopicUpdate>;
  let activatedRoute: ActivatedRoute;
  let topicFormService: TopicFormService;
  let topicService: TopicService;
  let legalContentService: LegalContentService;
  let moduleService: ModuleService;

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

    fixture = TestBed.createComponent(TopicUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    topicFormService = TestBed.inject(TopicFormService);
    topicService = TestBed.inject(TopicService);
    legalContentService = TestBed.inject(LegalContentService);
    moduleService = TestBed.inject(ModuleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call LegalContent query and add missing value', () => {
      const topic: ITopic = { id: 14122 };
      const legalContent: ILegalContent = { id: 7620 };
      topic.legalContent = legalContent;

      const legalContentCollection: ILegalContent[] = [{ id: 7620 }];
      vitest.spyOn(legalContentService, 'query').mockReturnValue(of(new HttpResponse({ body: legalContentCollection })));
      const additionalLegalContents = [legalContent];
      const expectedCollection: ILegalContent[] = [...additionalLegalContents, ...legalContentCollection];
      vitest.spyOn(legalContentService, 'addLegalContentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ topic });
      comp.ngOnInit();

      expect(legalContentService.query).toHaveBeenCalled();
      expect(legalContentService.addLegalContentToCollectionIfMissing).toHaveBeenCalledWith(
        legalContentCollection,
        ...additionalLegalContents.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.legalContentsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Module query and add missing value', () => {
      const topic: ITopic = { id: 14122 };
      const module: IModule = { id: 9460 };
      topic.module = module;

      const moduleCollection: IModule[] = [{ id: 9460 }];
      vitest.spyOn(moduleService, 'query').mockReturnValue(of(new HttpResponse({ body: moduleCollection })));
      const additionalModules = [module];
      const expectedCollection: IModule[] = [...additionalModules, ...moduleCollection];
      vitest.spyOn(moduleService, 'addModuleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ topic });
      comp.ngOnInit();

      expect(moduleService.query).toHaveBeenCalled();
      expect(moduleService.addModuleToCollectionIfMissing).toHaveBeenCalledWith(
        moduleCollection,
        ...additionalModules.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.modulesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const topic: ITopic = { id: 14122 };
      const legalContent: ILegalContent = { id: 7620 };
      topic.legalContent = legalContent;
      const module: IModule = { id: 9460 };
      topic.module = module;

      activatedRoute.data = of({ topic });
      comp.ngOnInit();

      expect(comp.legalContentsSharedCollection()).toContainEqual(legalContent);
      expect(comp.modulesSharedCollection()).toContainEqual(module);
      expect(comp.topic).toEqual(topic);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITopic>();
      const topic = { id: 29581 };
      vitest.spyOn(topicFormService, 'getTopic').mockReturnValue(topic);
      vitest.spyOn(topicService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ topic });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(topic);
      saveSubject.complete();

      // THEN
      expect(topicFormService.getTopic).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(topicService.update).toHaveBeenCalledWith(expect.objectContaining(topic));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITopic>();
      const topic = { id: 29581 };
      vitest.spyOn(topicFormService, 'getTopic').mockReturnValue({ id: null });
      vitest.spyOn(topicService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ topic: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(topic);
      saveSubject.complete();

      // THEN
      expect(topicFormService.getTopic).toHaveBeenCalled();
      expect(topicService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ITopic>();
      const topic = { id: 29581 };
      vitest.spyOn(topicService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ topic });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(topicService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareLegalContent', () => {
      it('should forward to legalContentService', () => {
        const entity = { id: 7620 };
        const entity2 = { id: 29225 };
        vitest.spyOn(legalContentService, 'compareLegalContent');
        comp.compareLegalContent(entity, entity2);
        expect(legalContentService.compareLegalContent).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareModule', () => {
      it('should forward to moduleService', () => {
        const entity = { id: 9460 };
        const entity2 = { id: 10579 };
        vitest.spyOn(moduleService, 'compareModule');
        comp.compareModule(entity, entity2);
        expect(moduleService.compareModule).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
