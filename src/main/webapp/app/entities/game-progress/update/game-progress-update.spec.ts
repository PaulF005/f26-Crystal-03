import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IGame } from 'app/entities/game/game.model';
import { GameService } from 'app/entities/game/service/game.service';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { IGameProgress } from '../game-progress.model';
import { GameProgressService } from '../service/game-progress.service';

import { GameProgressFormService } from './game-progress-form.service';
import { GameProgressUpdate } from './game-progress-update';

describe('GameProgress Management Update Component', () => {
  let comp: GameProgressUpdate;
  let fixture: ComponentFixture<GameProgressUpdate>;
  let activatedRoute: ActivatedRoute;
  let gameProgressFormService: GameProgressFormService;
  let gameProgressService: GameProgressService;
  let gameService: GameService;
  let userProfileService: UserProfileService;

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

    fixture = TestBed.createComponent(GameProgressUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    gameProgressFormService = TestBed.inject(GameProgressFormService);
    gameProgressService = TestBed.inject(GameProgressService);
    gameService = TestBed.inject(GameService);
    userProfileService = TestBed.inject(UserProfileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Game query and add missing value', () => {
      const gameProgress: IGameProgress = { id: 2611 };
      const game: IGame = { id: 7137 };
      gameProgress.game = game;

      const gameCollection: IGame[] = [{ id: 7137 }];
      vitest.spyOn(gameService, 'query').mockReturnValue(of(new HttpResponse({ body: gameCollection })));
      const additionalGames = [game];
      const expectedCollection: IGame[] = [...additionalGames, ...gameCollection];
      vitest.spyOn(gameService, 'addGameToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ gameProgress });
      comp.ngOnInit();

      expect(gameService.query).toHaveBeenCalled();
      expect(gameService.addGameToCollectionIfMissing).toHaveBeenCalledWith(
        gameCollection,
        ...additionalGames.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.gamesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call UserProfile query and add missing value', () => {
      const gameProgress: IGameProgress = { id: 2611 };
      const user: IUserProfile = { id: 22058 };
      gameProgress.user = user;
      const userProfile: IUserProfile = { id: 22058 };
      gameProgress.userProfile = userProfile;

      const userProfileCollection: IUserProfile[] = [{ id: 22058 }];
      vitest.spyOn(userProfileService, 'query').mockReturnValue(of(new HttpResponse({ body: userProfileCollection })));
      const additionalUserProfiles = [user, userProfile];
      const expectedCollection: IUserProfile[] = [...additionalUserProfiles, ...userProfileCollection];
      vitest.spyOn(userProfileService, 'addUserProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ gameProgress });
      comp.ngOnInit();

      expect(userProfileService.query).toHaveBeenCalled();
      expect(userProfileService.addUserProfileToCollectionIfMissing).toHaveBeenCalledWith(
        userProfileCollection,
        ...additionalUserProfiles.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.userProfilesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const gameProgress: IGameProgress = { id: 2611 };
      const game: IGame = { id: 7137 };
      gameProgress.game = game;
      const user: IUserProfile = { id: 22058 };
      gameProgress.user = user;
      const userProfile: IUserProfile = { id: 22058 };
      gameProgress.userProfile = userProfile;

      activatedRoute.data = of({ gameProgress });
      comp.ngOnInit();

      expect(comp.gamesSharedCollection()).toContainEqual(game);
      expect(comp.userProfilesSharedCollection()).toContainEqual(user);
      expect(comp.userProfilesSharedCollection()).toContainEqual(userProfile);
      expect(comp.gameProgress).toEqual(gameProgress);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IGameProgress>();
      const gameProgress = { id: 24479 };
      vitest.spyOn(gameProgressFormService, 'getGameProgress').mockReturnValue(gameProgress);
      vitest.spyOn(gameProgressService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gameProgress });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(gameProgress);
      saveSubject.complete();

      // THEN
      expect(gameProgressFormService.getGameProgress).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(gameProgressService.update).toHaveBeenCalledWith(expect.objectContaining(gameProgress));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IGameProgress>();
      const gameProgress = { id: 24479 };
      vitest.spyOn(gameProgressFormService, 'getGameProgress').mockReturnValue({ id: null });
      vitest.spyOn(gameProgressService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gameProgress: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(gameProgress);
      saveSubject.complete();

      // THEN
      expect(gameProgressFormService.getGameProgress).toHaveBeenCalled();
      expect(gameProgressService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IGameProgress>();
      const gameProgress = { id: 24479 };
      vitest.spyOn(gameProgressService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gameProgress });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(gameProgressService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareGame', () => {
      it('should forward to gameService', () => {
        const entity = { id: 7137 };
        const entity2 = { id: 5760 };
        vitest.spyOn(gameService, 'compareGame');
        comp.compareGame(entity, entity2);
        expect(gameService.compareGame).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareUserProfile', () => {
      it('should forward to userProfileService', () => {
        const entity = { id: 22058 };
        const entity2 = { id: 9009 };
        vitest.spyOn(userProfileService, 'compareUserProfile');
        comp.compareUserProfile(entity, entity2);
        expect(userProfileService.compareUserProfile).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
