import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { IGame } from 'app/entities/game/game.model';
import { GameService } from 'app/entities/game/service/game.service';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { AlertError } from 'app/shared/alert';
import { IGameSession } from '../game-session.model';
import { GameSessionService } from '../service/game-session.service';

import { GameSessionFormGroup, GameSessionFormService } from './game-session-form.service';

@Component({
  selector: 'jhi-game-session-update',
  templateUrl: './game-session-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class GameSessionUpdate implements OnInit {
  readonly isSaving = signal(false);
  gameSession: IGameSession | null = null;

  userProfilesSharedCollection = signal<IUserProfile[]>([]);
  gamesSharedCollection = signal<IGame[]>([]);

  protected gameSessionService = inject(GameSessionService);
  protected gameSessionFormService = inject(GameSessionFormService);
  protected userProfileService = inject(UserProfileService);
  protected gameService = inject(GameService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: GameSessionFormGroup = this.gameSessionFormService.createGameSessionFormGroup();

  compareUserProfile = (o1: IUserProfile | null, o2: IUserProfile | null): boolean => this.userProfileService.compareUserProfile(o1, o2);

  compareGame = (o1: IGame | null, o2: IGame | null): boolean => this.gameService.compareGame(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ gameSession }) => {
      this.gameSession = gameSession;
      if (gameSession) {
        this.updateForm(gameSession);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const gameSession = this.gameSessionFormService.getGameSession(this.editForm);
    if (gameSession.id === null) {
      this.subscribeToSaveResponse(this.gameSessionService.create(gameSession));
    } else {
      this.subscribeToSaveResponse(this.gameSessionService.update(gameSession));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IGameSession | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(gameSession: IGameSession): void {
    this.gameSession = gameSession;
    this.gameSessionFormService.resetForm(this.editForm, gameSession);

    this.userProfilesSharedCollection.update(userProfiles =>
      this.userProfileService.addUserProfileToCollectionIfMissing<IUserProfile>(userProfiles, gameSession.user),
    );
    this.gamesSharedCollection.update(games => this.gameService.addGameToCollectionIfMissing<IGame>(games, gameSession.game));
  }

  protected loadRelationshipsOptions(): void {
    this.userProfileService
      .query()
      .pipe(map((res: HttpResponse<IUserProfile[]>) => res.body ?? []))
      .pipe(
        map((userProfiles: IUserProfile[]) =>
          this.userProfileService.addUserProfileToCollectionIfMissing<IUserProfile>(userProfiles, this.gameSession?.user),
        ),
      )
      .subscribe((userProfiles: IUserProfile[]) => this.userProfilesSharedCollection.set(userProfiles));

    this.gameService
      .query()
      .pipe(map((res: HttpResponse<IGame[]>) => res.body ?? []))
      .pipe(map((games: IGame[]) => this.gameService.addGameToCollectionIfMissing<IGame>(games, this.gameSession?.game)))
      .subscribe((games: IGame[]) => this.gamesSharedCollection.set(games));
  }
}
