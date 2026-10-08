import { Component } from '@angular/core';

interface Game {
  id: string;
  name: string;
  description: string;
  status: string;
  experience: string;
}

@Component({
  selector: 'jhi-games',
  templateUrl: './games.html',
  styleUrl: './games.scss',
})
export default class Games {
  protected readonly games: Game[] = [
    {
      id: 'fact-or-fiction',
      name: 'Fact or Fiction?',
      description: 'Decide whether legal claims are fact, fiction, or context-dependent and learn why',
      status: 'New',
      experience: 'Unfamiliar',
    },
    {
      id: 'choose-the-outcome',
      name: 'Choose the Outcome',
      description: 'Choose what happens next and discover how legal decisions shape the outcome.',
      status: 'Optional',
      experience: 'Familiar',
    },
  ];
}
