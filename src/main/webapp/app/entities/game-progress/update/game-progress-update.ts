import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { IGame } from 'app/entities/game/game.model';
import { GameService } from 'app/entities/game/service/game.service';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { IGameProgress } from '../game-progress.model';
import { GameProgressService } from '../service/game-progress.service';

import { GameProgressFormGroup, GameProgressFormService } from './game-progress-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-game-progress-update',
  templateUrl: './game-progress-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class GameProgressUpdate implements OnInit {
  readonly isSaving = signal(false);
  gameProgress: IGameProgress | null = null;

  gamesSharedCollection = signal<IGame[]>([]);
  userProfilesSharedCollection = signal<IUserProfile[]>([]);

  protected gameProgressService = inject(GameProgressService);
  protected gameProgressFormService = inject(GameProgressFormService);
  protected gameService = inject(GameService);
  protected userProfileService = inject(UserProfileService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: GameProgressFormGroup = this.gameProgressFormService.createGameProgressFormGroup();

  compareGame = (o1: IGame | null, o2: IGame | null): boolean => this.gameService.compareGame(o1, o2);

  compareUserProfile = (o1: IUserProfile | null, o2: IUserProfile | null): boolean => this.userProfileService.compareUserProfile(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ gameProgress }) => {
      this.gameProgress = gameProgress;
      if (gameProgress) {
        this.updateForm(gameProgress);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const gameProgress = this.gameProgressFormService.getGameProgress(this.editForm);
    if (gameProgress.id === null) {
      this.subscribeToSaveResponse(this.gameProgressService.create(gameProgress));
    } else {
      this.subscribeToSaveResponse(this.gameProgressService.update(gameProgress));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IGameProgress | null>): void {
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

  protected updateForm(gameProgress: IGameProgress): void {
    this.gameProgress = gameProgress;
    this.gameProgressFormService.resetForm(this.editForm, gameProgress);

    this.gamesSharedCollection.update(games => this.gameService.addGameToCollectionIfMissing<IGame>(games, gameProgress.game));
    this.userProfilesSharedCollection.update(userProfiles =>
      this.userProfileService.addUserProfileToCollectionIfMissing<IUserProfile>(userProfiles, gameProgress.user, gameProgress.userProfile),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.gameService
      .query()
      .pipe(map((res: HttpResponse<IGame[]>) => res.body ?? []))
      .pipe(map((games: IGame[]) => this.gameService.addGameToCollectionIfMissing<IGame>(games, this.gameProgress?.game)))
      .subscribe((games: IGame[]) => this.gamesSharedCollection.set(games));

    this.userProfileService
      .query()
      .pipe(map((res: HttpResponse<IUserProfile[]>) => res.body ?? []))
      .pipe(
        map((userProfiles: IUserProfile[]) =>
          this.userProfileService.addUserProfileToCollectionIfMissing<IUserProfile>(
            userProfiles,
            this.gameProgress?.user,
            this.gameProgress?.userProfile,
          ),
        ),
      )
      .subscribe((userProfiles: IUserProfile[]) => this.userProfilesSharedCollection.set(userProfiles));
  }
}
