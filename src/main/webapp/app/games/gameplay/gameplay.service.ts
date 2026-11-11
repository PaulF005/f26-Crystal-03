import { HttpClient } from '@angular/common/http';
import { Service, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { serverApiUrl } from 'app/config';
import { ITopic } from 'app/entities/topic/topic.model';
export interface GameAnswer {
  answerId: number;
  text: string;
}

export interface GameStage {
  stageId: number;
  prompt: string;
  answers: GameAnswer[];
}

export interface GameSession {
  id: string;
  gameTitle: string;
  currentScenario: {
    scenarioId: number;
    currentStage: GameStage;
    stageNumber: number;
    stageCount: number;
  } | null;
}

export interface GameAnswerResult {
  nextStage: GameStage | null;
  result: {
    feedbackText: string | null;
    outcomeText: string | null;
  } | null;
}

export interface GameContinuation {
  session: GameSession | null;
  complete: boolean;
}

@Service()
export class GameplayService {
  private readonly http = inject(HttpClient);
  private readonly resourceUrl = `${serverApiUrl}api/game`;

  getTopics(gameId: number): Observable<ITopic[]> {
    return this.http.get<ITopic[]>(`${this.resourceUrl}/${gameId}/topics`);
  }

  start(gameId: number, topicId: number): Observable<GameSession> {
    return this.http.post<GameSession>(`${this.resourceUrl}/start`, {
      gameID: gameId,
      topicID: topicId,
    });
  }

  answer(sessionId: string, answerId: number): Observable<GameAnswerResult> {
    return this.http.post<GameAnswerResult>(`${this.resourceUrl}/${encodeURIComponent(sessionId)}/answer/${answerId}`, {});
  }

  continueSession(sessionId: string): Observable<GameContinuation> {
    return this.http.post<GameContinuation>(`${this.resourceUrl}/${encodeURIComponent(sessionId)}/continue`, {});
  }

  abandon(sessionId: string): Observable<undefined> {
    return this.http.post<undefined>(`${this.resourceUrl}/${encodeURIComponent(sessionId)}/abandon`, {});
  }
}
