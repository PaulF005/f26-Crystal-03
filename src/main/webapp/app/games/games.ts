import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { GameCard } from './game-card/game-card';

interface Game {
  id: string;
  name: string;
  thumbnail: string;
  description: string;
  status: string;
  experience: string;
}

@Component({
  selector: 'jhi-games',
  templateUrl: './games.html',
  styleUrl: './games.scss',
  imports: [GameCard],
})
export default class Games {
  private readonly router = inject(Router);

  protected readonly games: Game[] = [
    {
      id: 'fact-or-fiction',
      name: 'Fact or Fiction?',
      thumbnail: '/content/images/ui/thumb_fof.png',
      description: 'Decide whether legal claims are fact, fiction, or context-dependent and learn why',
      status: 'New',
      experience: 'Unfamiliar',
    },
    {
      id: 'choose-the-outcome',
      name: 'Choose the Outcome',
      thumbnail: '/content/images/ui/thumb_bn.png',
      description: 'Choose what happens next and discover how legal decisions shape the outcome.',
      status: 'Optional',
      experience: 'Familiar',
    },
  ];

  protected navigateToGame(gameId: string): void {
    this.router.navigate(['/games', gameId]);
  }
}
