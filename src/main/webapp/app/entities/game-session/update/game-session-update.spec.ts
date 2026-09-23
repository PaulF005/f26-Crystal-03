import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IGame } from 'app/entities/game/game.model';
import { GameService } from 'app/entities/game/service/game.service';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { IGameSession } from '../game-session.model';
import { GameSessionService } from '../service/game-session.service';

import { GameSessionFormService } from './game-session-form.service';
import { GameSessionUpdate } from './game-session-update';

describe('GameSession Management Update Component', () => {
  let comp: GameSessionUpdate;
  let fixture: ComponentFixture<GameSessionUpdate>;
  let activatedRoute: ActivatedRoute;
  let gameSessionFormService: GameSessionFormService;
  let gameSessionService: GameSessionService;
  let userProfileService: UserProfileService;
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

    fixture = TestBed.createComponent(GameSessionUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    gameSessionFormService = TestBed.inject(GameSessionFormService);
    gameSessionService = TestBed.inject(GameSessionService);
    userProfileService = TestBed.inject(UserProfileService);
    gameService = TestBed.inject(GameService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call UserProfile query and add missing value', () => {
      const gameSession: IGameSession = { id: 5692 };
      const user: IUserProfile = { id: 22058 };
      gameSession.user = user;

      const userProfileCollection: IUserProfile[] = [{ id: 22058 }];
      vi.spyOn(userProfileService, 'query').mockReturnValue(of(new HttpResponse({ body: userProfileCollection })));
      const additionalUserProfiles = [user];
      const expectedCollection: IUserProfile[] = [...additionalUserProfiles, ...userProfileCollection];
      vi.spyOn(userProfileService, 'addUserProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ gameSession });
      comp.ngOnInit();

      expect(userProfileService.query).toHaveBeenCalled();
      expect(userProfileService.addUserProfileToCollectionIfMissing).toHaveBeenCalledWith(
        userProfileCollection,
        ...additionalUserProfiles.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.userProfilesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Game query and add missing value', () => {
      const gameSession: IGameSession = { id: 5692 };
      const game: IGame = { id: 7137 };
      gameSession.game = game;

      const gameCollection: IGame[] = [{ id: 7137 }];
      vi.spyOn(gameService, 'query').mockReturnValue(of(new HttpResponse({ body: gameCollection })));
      const additionalGames = [game];
      const expectedCollection: IGame[] = [...additionalGames, ...gameCollection];
      vi.spyOn(gameService, 'addGameToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ gameSession });
      comp.ngOnInit();

      expect(gameService.query).toHaveBeenCalled();
      expect(gameService.addGameToCollectionIfMissing).toHaveBeenCalledWith(
        gameCollection,
        ...additionalGames.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.gamesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const gameSession: IGameSession = { id: 5692 };
      const user: IUserProfile = { id: 22058 };
      gameSession.user = user;
      const game: IGame = { id: 7137 };
      gameSession.game = game;

      activatedRoute.data = of({ gameSession });
      comp.ngOnInit();

      expect(comp.userProfilesSharedCollection()).toContainEqual(user);
      expect(comp.gamesSharedCollection()).toContainEqual(game);
      expect(comp.gameSession).toEqual(gameSession);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IGameSession>();
      const gameSession = { id: 30007 };
      vi.spyOn(gameSessionFormService, 'getGameSession').mockReturnValue(gameSession);
      vi.spyOn(gameSessionService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gameSession });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(gameSession);
      saveSubject.complete();

      // THEN
      expect(gameSessionFormService.getGameSession).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(gameSessionService.update).toHaveBeenCalledWith(expect.objectContaining(gameSession));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IGameSession>();
      const gameSession = { id: 30007 };
      vi.spyOn(gameSessionFormService, 'getGameSession').mockReturnValue({ id: null });
      vi.spyOn(gameSessionService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gameSession: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(gameSession);
      saveSubject.complete();

      // THEN
      expect(gameSessionFormService.getGameSession).toHaveBeenCalled();
      expect(gameSessionService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IGameSession>();
      const gameSession = { id: 30007 };
      vi.spyOn(gameSessionService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gameSession });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(gameSessionService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUserProfile', () => {
      it('should forward to userProfileService', () => {
        const entity = { id: 22058 };
        const entity2 = { id: 9009 };
        vi.spyOn(userProfileService, 'compareUserProfile');
        comp.compareUserProfile(entity, entity2);
        expect(userProfileService.compareUserProfile).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareGame', () => {
      it('should forward to gameService', () => {
        const entity = { id: 7137 };
        const entity2 = { id: 5760 };
        vi.spyOn(gameService, 'compareGame');
        comp.compareGame(entity, entity2);
        expect(gameService.compareGame).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
