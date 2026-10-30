import { Component, input } from '@angular/core';

@Component({
  selector: 'jhi-game-card',
  templateUrl: './game-card.html',
  styleUrl: './game-card.scss',
})
export class GameCard {
  readonly name = input.required<string>();
  readonly thumbnail = input.required<string>();
  readonly description = input.required<string>();
  readonly status = input.required<string>();
  readonly experience = input.required<string>();
}
