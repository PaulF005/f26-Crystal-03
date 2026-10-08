import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IQuestion } from 'app/entities/question/question.model';
import { QuestionService } from 'app/entities/question/service/question.service';
import { IScenario } from 'app/entities/scenario/scenario.model';
import { ScenarioService } from 'app/entities/scenario/service/scenario.service';
import { StageService } from '../service/stage.service';
import { IStage } from '../stage.model';

import { StageFormService } from './stage-form.service';
import { StageUpdate } from './stage-update';

describe('Stage Management Update Component', () => {
  let comp: StageUpdate;
  let fixture: ComponentFixture<StageUpdate>;
  let activatedRoute: ActivatedRoute;
  let stageFormService: StageFormService;
  let stageService: StageService;
  let questionService: QuestionService;
  let scenarioService: ScenarioService;

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

    fixture = TestBed.createComponent(StageUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    stageFormService = TestBed.inject(StageFormService);
    stageService = TestBed.inject(StageService);
    questionService = TestBed.inject(QuestionService);
    scenarioService = TestBed.inject(ScenarioService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Question query and add missing value', () => {
      const stage: IStage = { id: 6829 };
      const question: IQuestion = { id: 16375 };
      stage.question = question;

      const questionCollection: IQuestion[] = [{ id: 16375 }];
      vitest.spyOn(questionService, 'query').mockReturnValue(of(new HttpResponse({ body: questionCollection })));
      const additionalQuestions = [question];
      const expectedCollection: IQuestion[] = [...additionalQuestions, ...questionCollection];
      vitest.spyOn(questionService, 'addQuestionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ stage });
      comp.ngOnInit();

      expect(questionService.query).toHaveBeenCalled();
      expect(questionService.addQuestionToCollectionIfMissing).toHaveBeenCalledWith(
        questionCollection,
        ...additionalQuestions.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.questionsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Scenario query and add missing value', () => {
      const stage: IStage = { id: 6829 };
      const scenario: IScenario = { id: 10879 };
      stage.scenario = scenario;

      const scenarioCollection: IScenario[] = [{ id: 10879 }];
      vitest.spyOn(scenarioService, 'query').mockReturnValue(of(new HttpResponse({ body: scenarioCollection })));
      const additionalScenarios = [scenario];
      const expectedCollection: IScenario[] = [...additionalScenarios, ...scenarioCollection];
      vitest.spyOn(scenarioService, 'addScenarioToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ stage });
      comp.ngOnInit();

      expect(scenarioService.query).toHaveBeenCalled();
      expect(scenarioService.addScenarioToCollectionIfMissing).toHaveBeenCalledWith(
        scenarioCollection,
        ...additionalScenarios.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.scenariosSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const stage: IStage = { id: 6829 };
      const question: IQuestion = { id: 16375 };
      stage.question = question;
      const scenario: IScenario = { id: 10879 };
      stage.scenario = scenario;

      activatedRoute.data = of({ stage });
      comp.ngOnInit();

      expect(comp.questionsSharedCollection()).toContainEqual(question);
      expect(comp.scenariosSharedCollection()).toContainEqual(scenario);
      expect(comp.stage).toEqual(stage);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IStage>();
      const stage = { id: 30579 };
      vitest.spyOn(stageFormService, 'getStage').mockReturnValue(stage);
      vitest.spyOn(stageService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stage });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(stage);
      saveSubject.complete();

      // THEN
      expect(stageFormService.getStage).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(stageService.update).toHaveBeenCalledWith(expect.objectContaining(stage));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IStage>();
      const stage = { id: 30579 };
      vitest.spyOn(stageFormService, 'getStage').mockReturnValue({ id: null });
      vitest.spyOn(stageService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stage: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(stage);
      saveSubject.complete();

      // THEN
      expect(stageFormService.getStage).toHaveBeenCalled();
      expect(stageService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IStage>();
      const stage = { id: 30579 };
      vitest.spyOn(stageService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stage });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(stageService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareQuestion', () => {
      it('should forward to questionService', () => {
        const entity = { id: 16375 };
        const entity2 = { id: 15287 };
        vitest.spyOn(questionService, 'compareQuestion');
        comp.compareQuestion(entity, entity2);
        expect(questionService.compareQuestion).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareScenario', () => {
      it('should forward to scenarioService', () => {
        const entity = { id: 10879 };
        const entity2 = { id: 10024 };
        vitest.spyOn(scenarioService, 'compareScenario');
        comp.compareScenario(entity, entity2);
        expect(scenarioService.compareScenario).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
