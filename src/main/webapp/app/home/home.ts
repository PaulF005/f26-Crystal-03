import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { AccountService } from 'app/core/auth';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.html',
  styleUrl: './home.scss',
  imports: [RouterLink],
})
export default class Home {
  public readonly account = inject(AccountService).account;

  private readonly router = inject(Router);

  // TODO: Connect it to backend for real info
  // Hardcoded to see how it looks in UI
  public readonly recentActivity = [
    { title: 'Choose the Outcome', topic: 'Ownership', recency: '10 mins ago', score: 45 },
    { title: 'Choose the Outcome', topic: 'Ownership', recency: 'Yesterday', score: 22 },
    { title: 'Fact or Fiction?', topic: 'Creative Rights', recency: '2 days ago', score: 95 },
    { title: 'Choose the Outcome', topic: 'On the Road', recency: '2 days ago', score: 75 },
  ];

  login(): void {
    this.router.navigate(['/login']);
  }

  // Red 0-25, orange 26-50, yellow 51-75, green 76-100
  scoreColor(score: number): string {
    if (score <= 25) {
      return '#d32f2f'; // red
    }
    if (score <= 50) {
      return '#ef6c00'; // orange
    }
    if (score <= 75) {
      return '#e0a800'; // yellow
    }
    return '#2e7d32'; // green
  }
}
