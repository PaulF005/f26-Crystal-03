import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IGameSession, NewGameSession } from '../game-session.model';

export type PartialUpdateGameSession = Partial<IGameSession> & Pick<IGameSession, 'id'>;

type RestOf<T extends IGameSession | NewGameSession> = Omit<T, 'startedAt' | 'completedAt'> & {
  startedAt?: string | null;
  completedAt?: string | null;
};

export type RestGameSession = RestOf<IGameSession>;

export type NewRestGameSession = RestOf<NewGameSession>;

export type PartialUpdateRestGameSession = RestOf<PartialUpdateGameSession>;

@Injectable()
export class GameSessionsService {
  readonly gameSessionsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly gameSessionsResource = httpResource<RestGameSession[]>(() => {
    const params = this.gameSessionsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of gameSession that have been fetched. It is updated when the gameSessionsResource emits a new value.
   * In case of error while fetching the gameSessions, the signal is set to an empty array.
   */
  readonly gameSessions = computed(() =>
    (this.gameSessionsResource.hasValue() ? this.gameSessionsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/game-sessions');

  protected convertValueFromServer(restGameSession: RestGameSession): IGameSession {
    return {
      ...restGameSession,
      startedAt: restGameSession.startedAt ? dayjs(restGameSession.startedAt) : undefined,
      completedAt: restGameSession.completedAt ? dayjs(restGameSession.completedAt) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class GameSessionService extends GameSessionsService {
  protected readonly http = inject(HttpClient);

  create(gameSession: NewGameSession): Observable<IGameSession> {
    const copy = this.convertValueFromClient(gameSession);
    return this.http.post<RestGameSession>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(gameSession: IGameSession): Observable<IGameSession> {
    const copy = this.convertValueFromClient(gameSession);
    return this.http
      .put<RestGameSession>(`${this.resourceUrl}/${encodeURIComponent(this.getGameSessionIdentifier(gameSession))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(gameSession: PartialUpdateGameSession): Observable<IGameSession> {
    const copy = this.convertValueFromClient(gameSession);
    return this.http
      .patch<RestGameSession>(`${this.resourceUrl}/${encodeURIComponent(this.getGameSessionIdentifier(gameSession))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IGameSession> {
    return this.http
      .get<RestGameSession>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IGameSession[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestGameSession[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getGameSessionIdentifier(gameSession: Pick<IGameSession, 'id'>): number {
    return gameSession.id;
  }

  compareGameSession(o1: Pick<IGameSession, 'id'> | null, o2: Pick<IGameSession, 'id'> | null): boolean {
    return o1 && o2 ? this.getGameSessionIdentifier(o1) === this.getGameSessionIdentifier(o2) : o1 === o2;
  }

  addGameSessionToCollectionIfMissing<Type extends Pick<IGameSession, 'id'>>(
    gameSessionCollection: Type[],
    ...gameSessionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const gameSessions: Type[] = gameSessionsToCheck.filter(isPresent);
    if (gameSessions.length > 0) {
      const gameSessionCollectionIdentifiers = gameSessionCollection.map(gameSessionItem => this.getGameSessionIdentifier(gameSessionItem));
      const gameSessionsToAdd = gameSessions.filter(gameSessionItem => {
        const gameSessionIdentifier = this.getGameSessionIdentifier(gameSessionItem);
        if (gameSessionCollectionIdentifiers.includes(gameSessionIdentifier)) {
          return false;
        }
        gameSessionCollectionIdentifiers.push(gameSessionIdentifier);
        return true;
      });
      return [...gameSessionsToAdd, ...gameSessionCollection];
    }
    return gameSessionCollection;
  }

  protected convertValueFromClient<T extends IGameSession | NewGameSession | PartialUpdateGameSession>(gameSession: T): RestOf<T> {
    return {
      ...gameSession,
      startedAt: gameSession.startedAt?.toJSON() ?? null,
      completedAt: gameSession.completedAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestGameSession): IGameSession {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestGameSession[]): IGameSession[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
