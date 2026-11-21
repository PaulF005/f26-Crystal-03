import { HttpClient } from '@angular/common/http';
import { Component, effect, inject, signal } from '@angular/core';
import { Router } from '@angular/router';

import { serverApiUrl } from 'app/config';
import { AccountService } from 'app/core/auth';
import { GameCard } from './game-card/game-card';

interface Game {
  id: string;
  name: string;
  thumbnail: string;
  description: string;
}

interface GameStat {
  gameId: number;
  gameName: string;
  sessionsPlayed: number | null;
  performance: number | null;
  lastPlayedAt: string | null;
}

interface DashboardSummary {
  gameStats: GameStat[];
}

@Component({
  selector: 'jhi-games',
  templateUrl: './games.html',
  styleUrl: './games.scss',
  imports: [GameCard],
})
export default class Games {
  protected readonly account = inject(AccountService).account;
  protected readonly gameStats = signal<GameStat[]>([]);

  private readonly router = inject(Router);
  private readonly http = inject(HttpClient);

  protected readonly games: Game[] = [
    {
      id: 'fact-or-fiction',
      name: 'Fact or Fiction?',
      thumbnail: '/content/images/ui/thumb_fof.png',
      description: 'Decide whether legal claims are fact, fiction, or context-dependent and learn why',
    },
    {
      id: 'choose-the-outcome',
      name: 'Choose the Outcome',
      thumbnail: '/content/images/ui/thumb_bn.png',
      description: 'Choose what happens next and discover how legal decisions shape the outcome.',
    },
  ];

  constructor() {
    effect(onCleanup => {
      this.gameStats.set([]);

      if (this.account() === null) {
        return;
      }

      const request = this.http.get<DashboardSummary>(`${serverApiUrl}api/dashboard/summary`).subscribe({
        next: summary => this.gameStats.set(summary.gameStats ?? []),
        error: () => this.gameStats.set([]),
      });

      onCleanup(() => request.unsubscribe());
    });
  }

  protected historyFor(game: Game): string {
    if (this.account() === null) {
      return 'Sign in to track history';
    }

    const stat = this.findStat(game);

    if (stat?.lastPlayedAt == null) {
      return 'Never played';
    }

    return `Last played ${new Date(stat.lastPlayedAt).toLocaleDateString()}`;
  }

  protected experienceFor(game: Game): string {
    if (this.account() === null) {
      return 'Sign in to track progress';
    }

    const stat = this.findStat(game);

    if (stat?.performance == null) {
      return 'No performance recorded';
    }

    const sessions = stat.sessionsPlayed ?? 0;
    const sessionText = sessions === 1 ? 'session' : 'sessions';

    return `${stat.performance}% average (${sessions} ${sessionText})`;
  }

  protected navigateToGame(gameId: string): void {
    this.router.navigate(['/games', gameId]);
  }

  private findStat(game: Game): GameStat | undefined {
    const gameName = this.normalizeName(game.name);

    return this.gameStats().find(stat => this.normalizeName(stat.gameName) === gameName);
  }

  private normalizeName(name: string): string {
    return name.toLowerCase().replace(/[^a-z0-9]/g, '');
  }
}
