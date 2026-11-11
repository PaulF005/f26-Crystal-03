import { Component, OnDestroy, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { EMPTY, catchError, finalize } from 'rxjs';
import { GameplayService, GameSession } from './gameplay.service';
import { ITopic } from 'app/entities/topic/topic.model';

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

interface ChooseOutcomeGame {
  type: 'choose-the-outcome';
  title: string;
  module: string;
  questions: ChooseOutcomeQuestion[];
}

// Placeholder content copied from the mockups to see what it looks like. Replace with the real questions.
const GAMES: Record<string, ChooseOutcomeGame | undefined> = {
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
  imports: [RouterLink],
})
export default class Gameplay implements OnDestroy, OnInit {
  protected readonly isFactOrFiction = inject(ActivatedRoute).snapshot.paramMap.get('id') === 'fact-or-fiction';
  protected readonly content = GAMES[inject(ActivatedRoute).snapshot.paramMap.get('id') ?? ''];

  protected readonly session = signal<GameSession | null>(null);
  protected readonly busy = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly completed = signal(false);
  protected readonly answered = signal(false);
  protected readonly questionNumber = signal(1);
  protected readonly feedback = signal<string | null>(null);
  protected readonly currentStage = computed(() => this.session()?.currentScenario?.currentStage);

  protected readonly topics = signal<ITopic[]>([]);
  protected readonly selectedTopic = signal<ITopic | null>(null);
  protected readonly questionIndex = signal(0);
  protected readonly explanation = signal<string | null>(null);
  protected readonly selectedEvidence = signal<EvidenceItem | null>(null);
  protected readonly outcomeQuestion = computed(() => this.content?.questions[this.questionIndex()]);
  protected readonly finished = computed(() => !this.outcomeQuestion());

  private readonly gameplay = inject(GameplayService);
  private destroyed = false;

  // Load the available topics for Fact or Fiction game
  ngOnInit(): void {
    if (!this.isFactOrFiction) return;

    this.busy.set(true);

    this.gameplay
      .getTopics(10)
      .pipe(finalize(() => this.busy.set(false)))
      .subscribe({
        next: topics => {
          if (!this.destroyed) {
            this.topics.set(topics);
          }
        },
        error: () => this.error.set('Unable to load topics. Make sure you are signed in and reload the page.'),
      });
  }

  ngOnDestroy(): void {
    this.destroyed = true;
    const session = this.session();
    if (session && !this.completed()) {
      this.abandonInBackground(session.id);
    }
  }
  // Start FOF based on topic selection. Backend curation selects the appropriate scenario for the chosen topic.
  protected startFactOrFiction(topic: ITopic): void {
    if (this.busy() || this.session()) return;
    this.error.set(null);
    this.busy.set(true);
    this.gameplay
      .start(10, topic.id)
      .pipe(finalize(() => this.busy.set(false)))
      .subscribe({
        next: session => {
          if (this.destroyed) {
            this.abandonInBackground(session.id);
            return;
          }
          this.selectedTopic.set(topic);
          this.session.set(session);
          this.completed.set(false);
          this.answered.set(false);
          this.feedback.set(null);
          this.questionNumber.set(1);
        },
        error: () => this.error.set('Unable to start the game. Make sure you are signed in and try again.'),
      });
  }

  protected answerFactOrFiction(answerId: number): void {
    const session = this.session();
    if (!session || this.busy() || this.answered() || this.error()) return;
    this.busy.set(true);
    this.gameplay
      .answer(session.id, answerId)
      .pipe(finalize(() => this.busy.set(false)))
      .subscribe({
        next: response => {
          if (this.destroyed) return;
          this.answered.set(true);
          this.feedback.set(response.result?.feedbackText ?? response.result?.outcomeText ?? 'Answer recorded.');
        },
        error: () => this.error.set('The answer could not be confirmed. Return to Games and start a new session.'),
      });
  }

  protected nextFactOrFiction(): void {
    const session = this.session();
    if (!session || this.busy() || !this.answered() || this.error()) return;
    this.busy.set(true);
    this.gameplay
      .continueSession(session.id)
      .pipe(finalize(() => this.busy.set(false)))
      .subscribe({
        next: response => {
          if (this.destroyed) return;
          if (response.complete) {
            this.completed.set(true);
            this.session.set(null);
            return;
          }
          if (!response.session?.currentScenario) {
            this.error.set('The next question was unavailable. Return to Games and start a new session.');
            return;
          }
          this.session.set(response.session);
          this.questionNumber.update(number => number + 1);
          this.feedback.set(null);
          this.answered.set(false);
        },
        error: () => this.error.set('Unable to continue or save the session. Return to Games and try again.'),
      });
  }

  protected reveal(explanation: string): void {
    this.explanation.set(explanation);
  }

  protected selectEvidence(item: EvidenceItem): void {
    this.selectedEvidence.set(item);
  }

  protected next(): void {
    // TODO: Connect branching narrative progression to its story flow.
  }

  private abandonInBackground(sessionId: string): void {
    this.gameplay
      .abandon(sessionId)
      .pipe(catchError(() => EMPTY))
      .subscribe();
  }
}
