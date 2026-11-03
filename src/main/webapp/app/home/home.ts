import { DatePipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, effect, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { AccountService } from 'app/core/auth';
import { serverApiUrl } from 'app/config';
import { UI_TEXT } from 'app/shared/ui/ui-text';

interface RecentActivity {
  id: number;
  gameName: string;
  completedAt: string;
}

interface DashboardSummary {
  gamesPlayed: number;
  averagePerformance: number | null;
  currentStreak: number;
  recentActivity: RecentActivity[];
}

@Component({
  selector: 'jhi-home',
  templateUrl: './home.html',
  styleUrl: './home.scss',
  imports: [RouterLink, DatePipe],
})
export default class Home {
  readonly uiText = UI_TEXT;
  readonly account = inject(AccountService).account;
  readonly summary = signal<DashboardSummary | null>(null);
  readonly loading = signal(false);
  readonly loadError = signal(false);

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
}
