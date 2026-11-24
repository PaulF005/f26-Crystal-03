import { DatePipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, computed, effect, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { AccountService } from 'app/core/auth';
import { serverApiUrl } from 'app/config';
import { GuiButton } from 'app/shared/ui/gui-button/gui-button';
import { GuiWindow } from 'app/shared/ui/gui-window/gui-window';
import { UI_TEXT } from 'app/shared/ui/ui-text';
import { GAMES } from 'app/games/games';
import { GameCard } from 'app/games/game-card/game-card';

interface RecentActivity {
  id: number;
  gameName: string;
  completedAt: string;
}

interface DashboardSummary {
  gamesPlayed: number;
  averagePerformance: number | null;
  currentStreak: number;
  weeklyPlays: number;
  favoriteGame: string | null;
  recentActivity: RecentActivity[];
}

@Component({
  selector: 'jhi-home',
  templateUrl: './home.html',
  styleUrl: './home.scss',
  imports: [RouterLink, DatePipe, GuiButton, GuiWindow],
})
export default class Home {
  readonly uiText = UI_TEXT;
  readonly account = inject(AccountService).account;
  readonly summary = signal<DashboardSummary | null>(null);
  readonly loading = signal(false);
  readonly loadError = signal(false);

  readonly recommendedGame = computed(() => {
    const clean = (name: string): string => name.toLowerCase().replace(/[^a-z0-9]/g, '');
    const lastPlayed = new Map<string, number>();

    for (const activity of this.summary()?.recentActivity ?? []) {
      const key = clean(activity.gameName);
      const time = new Date(activity.completedAt).getTime();
      lastPlayed.set(key, Math.max(time, lastPlayed.get(key) ?? 0));
    }

    const unplayed = GAMES.find(game => !lastPlayed.has(clean(game.name)));
    if (unplayed) {
      return unplayed;
    }
    return GAMES.reduce((oldest, game) => (lastPlayed.get(clean(game.name))! < lastPlayed.get(clean(oldest.name))! ? game : oldest));
  });

  private readonly router = inject(Router);
  private readonly http = inject(HttpClient);

  constructor() {
    effect(onCleanup => {
      this.summary.set(null);
      this.loadError.set(false);

      if (this.account() === null) {
        this.loading.set(false);
        return;
      }

      this.loading.set(true);
      const request = this.http.get<DashboardSummary>(`${serverApiUrl}api/dashboard/summary`).subscribe({
        next: data => {
          this.summary.set(data);
          this.loading.set(false);
        },
        error: () => {
          this.loadError.set(true);
          this.loading.set(false);
        },
      });

      // Prevent an old user's response appearing after logout/account changes.
      onCleanup(() => request.unsubscribe());
    });
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  browseGames(): void {
    this.router.navigate(['/games']);
  }

  navigateToGame(gameId: string): void {
    this.router.navigate(['/games', gameId]);
  }
}
