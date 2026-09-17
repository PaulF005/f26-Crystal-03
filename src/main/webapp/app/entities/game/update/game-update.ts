import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize } from 'rxjs';

import { AlertError } from 'app/shared/alert/alert-error';
import { IGame } from '../game.model';
import { GameService } from '../service/game.service';

import { GameFormGroup, GameFormService } from './game-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-game-update',
  templateUrl: './game-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class GameUpdate implements OnInit {
  readonly isSaving = signal(false);
  game: IGame | null = null;

  protected gameService = inject(GameService);
  protected gameFormService = inject(GameFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: GameFormGroup = this.gameFormService.createGameFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ game }) => {
      this.game = game;
      if (game) {
        this.updateForm(game);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const game = this.gameFormService.getGame(this.editForm);
    if (game.id === null) {
      this.subscribeToSaveResponse(this.gameService.create(game));
    } else {
      this.subscribeToSaveResponse(this.gameService.update(game));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IGame | null>): void {
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

  protected updateForm(game: IGame): void {
    this.game = game;
    this.gameFormService.resetForm(this.editForm, game);
  }
}
