import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

interface FactOrFictionQuestion {
  claim: string;
  answer: 'Fact' | 'Fiction' | 'Situational';
  explanation: string;
}

interface EvidenceItem {
  name: string;
  detail: string;
}

interface OutcomeChoice {
  label: string;
  explanation: string;
}

interface ChooseOutcomeQuestion {
  situation: string;
  evidence: EvidenceItem[];
  choices: OutcomeChoice[];
}

interface FactOrFictionGame {
  type: 'fact-or-fiction';
  title: string;
  module: string;
  questions: FactOrFictionQuestion[];
}

interface ChooseOutcomeGame {
  type: 'choose-the-outcome';
  title: string;
  module: string;
  questions: ChooseOutcomeQuestion[];
}

type GameContent = FactOrFictionGame | ChooseOutcomeGame;

// Placeholder content copied from the mockups to see what it looks like. Replace with the real questions.
const GAMES: Record<string, GameContent> = {
  'fact-or-fiction': {
    type: 'fact-or-fiction',
    title: 'Fact or Fiction?',
    module: 'On the Road',
    questions: [
      {
        claim:
          'When approaching an intersection, if the traffic light is yellow, you are always allowed to speed up in order to pass through.',
        answer: 'Situational',
        explanation:
          "A yellow light doesn't give drivers permission to speed up. You may continue only if it is safe to do so. Speeding up beyond the speed limit or driving too fast for conditions may be illegal (reckless).",
      },
    ],
  },
  'choose-the-outcome': {
    type: 'choose-the-outcome',
    title: 'Choose the Outcome',
    module: 'Creative Rights',
    questions: [
      {
        situation: "A student has used another student's artwork in a public advertisement.",
        evidence: [
          { name: 'The media room', detail: 'Placeholder detail.' },
          { name: 'The original artwork', detail: 'Placeholder detail.' },
          { name: 'The editing files', detail: 'Placeholder detail.' },
          { name: 'The text messages', detail: 'The texts show the artist permitting use of their art in a classroom presentation.' },
        ],
        choices: [
          { label: 'Publish advertisement?', explanation: 'Placeholder explanation.' },
          { label: 'Edit advertisement?', explanation: 'Placeholder explanation.' },
          { label: 'Ask permission for art?', explanation: 'Placeholder explanation.' },
        ],
      },
    ],
  },
};

@Component({
  selector: 'jhi-gameplay',
  templateUrl: './gameplay.html',
  styleUrl: './gameplay.scss',
})
export default class Gameplay {
  protected readonly content: GameContent | undefined = GAMES[inject(ActivatedRoute).snapshot.paramMap.get('id') ?? ''];

  // Fact or Fiction always has the same answers
  protected readonly factAnswers = ['Fact', 'Fiction', 'Situational'] as const;

  protected readonly questionIndex = signal(0);
  protected readonly explanation = signal<string | null>(null);
  protected readonly selectedEvidence = signal<EvidenceItem | null>(null);

  protected readonly factQuestion = computed(() =>
    this.content?.type === 'fact-or-fiction' ? this.content.questions[this.questionIndex()] : undefined,
  );

  protected readonly outcomeQuestion = computed(() =>
    this.content?.type === 'choose-the-outcome' ? this.content.questions[this.questionIndex()] : undefined,
  );

  protected readonly finished = computed(() => !this.factQuestion() && !this.outcomeQuestion());

  protected reveal(explanation: string): void {
    this.explanation.set(explanation);
  }

  protected selectEvidence(item: EvidenceItem): void {
    this.selectedEvidence.set(item);
  }

  protected next(): void {
    // TODO: Make it go to the next question or finish
  }
}
