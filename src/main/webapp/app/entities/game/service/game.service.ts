import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IGame, NewGame } from '../game.model';

export type PartialUpdateGame = Partial<IGame> & Pick<IGame, 'id'>;

@Service()
export class GamesService {
  readonly gamesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(undefined);
  readonly gamesResource = httpResource<IGame[]>(() => {
    const params = this.gamesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of game that have been fetched. It is updated when the gamesResource emits a new value.
   * In case of error while fetching the games, the signal is set to an empty array.
   */
  readonly games = computed(() => (this.gamesResource.hasValue() ? this.gamesResource.value() : []));
  protected readonly resourceUrl = `${serverApiUrl}api/games`;
}

@Service()
export class GameService extends GamesService {
  protected readonly http = inject(HttpClient);

  create(game: NewGame): Observable<IGame> {
    return this.http.post<IGame>(this.resourceUrl, game);
  }

  update(game: IGame): Observable<IGame> {
    return this.http.put<IGame>(`${this.resourceUrl}/${encodeURIComponent(this.getGameIdentifier(game))}`, game);
  }

  partialUpdate(game: PartialUpdateGame): Observable<IGame> {
    return this.http.patch<IGame>(`${this.resourceUrl}/${encodeURIComponent(this.getGameIdentifier(game))}`, game);
  }

  find(id: number): Observable<IGame> {
    return this.http.get<IGame>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IGame[]>> {
    const options = createRequestOption(req);
    return this.http.get<IGame[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getGameIdentifier(game: Pick<IGame, 'id'>): number {
    return game.id;
  }

  compareGame(o1: Pick<IGame, 'id'> | null, o2: Pick<IGame, 'id'> | null): boolean {
    return o1 && o2 ? this.getGameIdentifier(o1) === this.getGameIdentifier(o2) : o1 === o2;
  }

  addGameToCollectionIfMissing<Type extends Pick<IGame, 'id'>>(
    gameCollection: Type[],
    ...gamesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const games: Type[] = gamesToCheck.filter(gameItem => gameItem !== null && gameItem !== undefined);
    if (games.length > 0) {
      const gameCollectionIdentifiers = gameCollection.map(gameItem => this.getGameIdentifier(gameItem));
      const gamesToAdd = games.filter(gameItem => {
        const gameIdentifier = this.getGameIdentifier(gameItem);
        if (gameCollectionIdentifiers.includes(gameIdentifier)) {
          return false;
        }
        gameCollectionIdentifiers.push(gameIdentifier);
        return true;
      });
      return [...gamesToAdd, ...gameCollection];
    }
    return gameCollection;
  }
}
