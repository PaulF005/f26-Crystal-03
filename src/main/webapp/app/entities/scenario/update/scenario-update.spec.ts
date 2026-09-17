import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IGame } from 'app/entities/game/game.model';
import { GameService } from 'app/entities/game/service/game.service';
import { TopicService } from 'app/entities/topic/service/topic.service';
import { ITopic } from 'app/entities/topic/topic.model';
import { IScenario } from '../scenario.model';
import { ScenarioService } from '../service/scenario.service';

import { ScenarioFormService } from './scenario-form.service';
import { ScenarioUpdate } from './scenario-update';

describe('Scenario Management Update Component', () => {
  let comp: ScenarioUpdate;
  let fixture: ComponentFixture<ScenarioUpdate>;
  let activatedRoute: ActivatedRoute;
  let scenarioFormService: ScenarioFormService;
  let scenarioService: ScenarioService;
  let topicService: TopicService;
  let gameService: GameService;

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

    fixture = TestBed.createComponent(ScenarioUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    scenarioFormService = TestBed.inject(ScenarioFormService);
    scenarioService = TestBed.inject(ScenarioService);
    topicService = TestBed.inject(TopicService);
    gameService = TestBed.inject(GameService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Topic query and add missing value', () => {
      const scenario: IScenario = { id: 10024 };
      const topic: ITopic = { id: 29581 };
      scenario.topic = topic;

      const topicCollection: ITopic[] = [{ id: 29581 }];
      vitest.spyOn(topicService, 'query').mockReturnValue(of(new HttpResponse({ body: topicCollection })));
      const additionalTopics = [topic];
      const expectedCollection: ITopic[] = [...additionalTopics, ...topicCollection];
      vitest.spyOn(topicService, 'addTopicToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ scenario });
      comp.ngOnInit();

      expect(topicService.query).toHaveBeenCalled();
      expect(topicService.addTopicToCollectionIfMissing).toHaveBeenCalledWith(
        topicCollection,
        ...additionalTopics.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.topicsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Game query and add missing value', () => {
      const scenario: IScenario = { id: 10024 };
      const game: IGame = { id: 7137 };
      scenario.game = game;

      const gameCollection: IGame[] = [{ id: 7137 }];
      vitest.spyOn(gameService, 'query').mockReturnValue(of(new HttpResponse({ body: gameCollection })));
      const additionalGames = [game];
      const expectedCollection: IGame[] = [...additionalGames, ...gameCollection];
      vitest.spyOn(gameService, 'addGameToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ scenario });
      comp.ngOnInit();

      expect(gameService.query).toHaveBeenCalled();
      expect(gameService.addGameToCollectionIfMissing).toHaveBeenCalledWith(
        gameCollection,
        ...additionalGames.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.gamesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const scenario: IScenario = { id: 10024 };
      const topic: ITopic = { id: 29581 };
      scenario.topic = topic;
      const game: IGame = { id: 7137 };
      scenario.game = game;

      activatedRoute.data = of({ scenario });
      comp.ngOnInit();

      expect(comp.topicsSharedCollection()).toContainEqual(topic);
      expect(comp.gamesSharedCollection()).toContainEqual(game);
      expect(comp.scenario).toEqual(scenario);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IScenario>();
      const scenario = { id: 10879 };
      vitest.spyOn(scenarioFormService, 'getScenario').mockReturnValue(scenario);
      vitest.spyOn(scenarioService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ scenario });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(scenario);
      saveSubject.complete();

      // THEN
      expect(scenarioFormService.getScenario).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(scenarioService.update).toHaveBeenCalledWith(expect.objectContaining(scenario));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IScenario>();
      const scenario = { id: 10879 };
      vitest.spyOn(scenarioFormService, 'getScenario').mockReturnValue({ id: null });
      vitest.spyOn(scenarioService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ scenario: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(scenario);
      saveSubject.complete();

      // THEN
      expect(scenarioFormService.getScenario).toHaveBeenCalled();
      expect(scenarioService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IScenario>();
      const scenario = { id: 10879 };
      vitest.spyOn(scenarioService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ scenario });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(scenarioService.update).toHaveBeenCalled();
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

    describe('compareGame', () => {
      it('should forward to gameService', () => {
        const entity = { id: 7137 };
        const entity2 = { id: 5760 };
        vitest.spyOn(gameService, 'compareGame');
        comp.compareGame(entity, entity2);
        expect(gameService.compareGame).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
