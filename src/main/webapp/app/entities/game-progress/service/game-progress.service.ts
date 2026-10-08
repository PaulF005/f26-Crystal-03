import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IGameProgress, NewGameProgress } from '../game-progress.model';

export type PartialUpdateGameProgress = Partial<IGameProgress> & Pick<IGameProgress, 'id'>;

type RestOf<T extends IGameProgress | NewGameProgress> = Omit<T, 'lastPlayedAt'> & {
  lastPlayedAt?: string | null;
};

export type RestGameProgress = RestOf<IGameProgress>;

export type NewRestGameProgress = RestOf<NewGameProgress>;

export type PartialUpdateRestGameProgress = RestOf<PartialUpdateGameProgress>;

@Service()
export class GameProgressesService {
  readonly gameProgressesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly gameProgressesResource = httpResource<RestGameProgress[]>(() => {
    const params = this.gameProgressesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of gameProgress that have been fetched. It is updated when the gameProgressesResource emits a new value.
   * In case of error while fetching the gameProgresses, the signal is set to an empty array.
   */
  readonly gameProgresses = computed(() =>
    (this.gameProgressesResource.hasValue() ? this.gameProgressesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly resourceUrl = `${serverApiUrl}api/game-progresses`;

  protected convertValueFromServer(restGameProgress: RestGameProgress): IGameProgress {
    return {
      ...restGameProgress,
      lastPlayedAt: restGameProgress.lastPlayedAt ? dayjs(restGameProgress.lastPlayedAt) : undefined,
    };
  }
}

@Service()
export class GameProgressService extends GameProgressesService {
  protected readonly http = inject(HttpClient);

  create(gameProgress: NewGameProgress): Observable<IGameProgress> {
    const copy = this.convertValueFromClient(gameProgress);
    return this.http.post<RestGameProgress>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(gameProgress: IGameProgress): Observable<IGameProgress> {
    const copy = this.convertValueFromClient(gameProgress);
    return this.http
      .put<RestGameProgress>(`${this.resourceUrl}/${encodeURIComponent(this.getGameProgressIdentifier(gameProgress))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(gameProgress: PartialUpdateGameProgress): Observable<IGameProgress> {
    const copy = this.convertValueFromClient(gameProgress);
    return this.http
      .patch<RestGameProgress>(`${this.resourceUrl}/${encodeURIComponent(this.getGameProgressIdentifier(gameProgress))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IGameProgress> {
    return this.http
      .get<RestGameProgress>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IGameProgress[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestGameProgress[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getGameProgressIdentifier(gameProgress: Pick<IGameProgress, 'id'>): number {
    return gameProgress.id;
  }

  compareGameProgress(o1: Pick<IGameProgress, 'id'> | null, o2: Pick<IGameProgress, 'id'> | null): boolean {
    return o1 && o2 ? this.getGameProgressIdentifier(o1) === this.getGameProgressIdentifier(o2) : o1 === o2;
  }

  addGameProgressToCollectionIfMissing<Type extends Pick<IGameProgress, 'id'>>(
    gameProgressCollection: Type[],
    ...gameProgressesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const gameProgresses: Type[] = gameProgressesToCheck.filter(
      gameProgressItem => gameProgressItem !== null && gameProgressItem !== undefined,
    );
    if (gameProgresses.length > 0) {
      const gameProgressCollectionIdentifiers = gameProgressCollection.map(gameProgressItem =>
        this.getGameProgressIdentifier(gameProgressItem),
      );
      const gameProgressesToAdd = gameProgresses.filter(gameProgressItem => {
        const gameProgressIdentifier = this.getGameProgressIdentifier(gameProgressItem);
        if (gameProgressCollectionIdentifiers.includes(gameProgressIdentifier)) {
          return false;
        }
        gameProgressCollectionIdentifiers.push(gameProgressIdentifier);
        return true;
      });
      return [...gameProgressesToAdd, ...gameProgressCollection];
    }
    return gameProgressCollection;
  }

  protected convertValueFromClient<T extends IGameProgress | NewGameProgress | PartialUpdateGameProgress>(gameProgress: T): RestOf<T> {
    return {
      ...gameProgress,
      lastPlayedAt: gameProgress.lastPlayedAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestGameProgress): IGameProgress {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestGameProgress[]): IGameProgress[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
