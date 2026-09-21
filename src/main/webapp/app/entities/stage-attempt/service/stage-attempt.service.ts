import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IStageAttempt, NewStageAttempt } from '../stage-attempt.model';

export type PartialUpdateStageAttempt = Partial<IStageAttempt> & Pick<IStageAttempt, 'id'>;

type RestOf<T extends IStageAttempt | NewStageAttempt> = Omit<T, 'answeredAt'> & {
  answeredAt?: string | null;
};

export type RestStageAttempt = RestOf<IStageAttempt>;

export type NewRestStageAttempt = RestOf<NewStageAttempt>;

export type PartialUpdateRestStageAttempt = RestOf<PartialUpdateStageAttempt>;

@Injectable()
export class StageAttemptsService {
  readonly stageAttemptsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly stageAttemptsResource = httpResource<RestStageAttempt[]>(() => {
    const params = this.stageAttemptsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of stageAttempt that have been fetched. It is updated when the stageAttemptsResource emits a new value.
   * In case of error while fetching the stageAttempts, the signal is set to an empty array.
   */
  readonly stageAttempts = computed(() =>
    (this.stageAttemptsResource.hasValue() ? this.stageAttemptsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/stage-attempts');

  protected convertValueFromServer(restStageAttempt: RestStageAttempt): IStageAttempt {
    return {
      ...restStageAttempt,
      answeredAt: restStageAttempt.answeredAt ? dayjs(restStageAttempt.answeredAt) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class StageAttemptService extends StageAttemptsService {
  protected readonly http = inject(HttpClient);

  create(stageAttempt: NewStageAttempt): Observable<IStageAttempt> {
    const copy = this.convertValueFromClient(stageAttempt);
    return this.http.post<RestStageAttempt>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(stageAttempt: IStageAttempt): Observable<IStageAttempt> {
    const copy = this.convertValueFromClient(stageAttempt);
    return this.http
      .put<RestStageAttempt>(`${this.resourceUrl}/${encodeURIComponent(this.getStageAttemptIdentifier(stageAttempt))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(stageAttempt: PartialUpdateStageAttempt): Observable<IStageAttempt> {
    const copy = this.convertValueFromClient(stageAttempt);
    return this.http
      .patch<RestStageAttempt>(`${this.resourceUrl}/${encodeURIComponent(this.getStageAttemptIdentifier(stageAttempt))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IStageAttempt> {
    return this.http
      .get<RestStageAttempt>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IStageAttempt[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestStageAttempt[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getStageAttemptIdentifier(stageAttempt: Pick<IStageAttempt, 'id'>): number {
    return stageAttempt.id;
  }

  compareStageAttempt(o1: Pick<IStageAttempt, 'id'> | null, o2: Pick<IStageAttempt, 'id'> | null): boolean {
    return o1 && o2 ? this.getStageAttemptIdentifier(o1) === this.getStageAttemptIdentifier(o2) : o1 === o2;
  }

  addStageAttemptToCollectionIfMissing<Type extends Pick<IStageAttempt, 'id'>>(
    stageAttemptCollection: Type[],
    ...stageAttemptsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const stageAttempts: Type[] = stageAttemptsToCheck.filter(isPresent);
    if (stageAttempts.length > 0) {
      const stageAttemptCollectionIdentifiers = stageAttemptCollection.map(stageAttemptItem =>
        this.getStageAttemptIdentifier(stageAttemptItem),
      );
      const stageAttemptsToAdd = stageAttempts.filter(stageAttemptItem => {
        const stageAttemptIdentifier = this.getStageAttemptIdentifier(stageAttemptItem);
        if (stageAttemptCollectionIdentifiers.includes(stageAttemptIdentifier)) {
          return false;
        }
        stageAttemptCollectionIdentifiers.push(stageAttemptIdentifier);
        return true;
      });
      return [...stageAttemptsToAdd, ...stageAttemptCollection];
    }
    return stageAttemptCollection;
  }

  protected convertValueFromClient<T extends IStageAttempt | NewStageAttempt | PartialUpdateStageAttempt>(stageAttempt: T): RestOf<T> {
    return {
      ...stageAttempt,
      answeredAt: stageAttempt.answeredAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestStageAttempt): IStageAttempt {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestStageAttempt[]): IStageAttempt[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
