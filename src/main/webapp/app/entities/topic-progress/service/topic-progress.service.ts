import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { ITopicProgress, NewTopicProgress } from '../topic-progress.model';

export type PartialUpdateTopicProgress = Partial<ITopicProgress> & Pick<ITopicProgress, 'id'>;

type RestOf<T extends ITopicProgress | NewTopicProgress> = Omit<T, 'lastPracticedAt'> & {
  lastPracticedAt?: string | null;
};

export type RestTopicProgress = RestOf<ITopicProgress>;

export type NewRestTopicProgress = RestOf<NewTopicProgress>;

export type PartialUpdateRestTopicProgress = RestOf<PartialUpdateTopicProgress>;

@Service()
export class TopicProgressesService {
  readonly topicProgressesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly topicProgressesResource = httpResource<RestTopicProgress[]>(() => {
    const params = this.topicProgressesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of topicProgress that have been fetched. It is updated when the topicProgressesResource emits a new value.
   * In case of error while fetching the topicProgresses, the signal is set to an empty array.
   */
  readonly topicProgresses = computed(() =>
    (this.topicProgressesResource.hasValue() ? this.topicProgressesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly resourceUrl = `${serverApiUrl}api/topic-progresses`;

  protected convertValueFromServer(restTopicProgress: RestTopicProgress): ITopicProgress {
    return {
      ...restTopicProgress,
      lastPracticedAt: restTopicProgress.lastPracticedAt ? dayjs(restTopicProgress.lastPracticedAt) : undefined,
    };
  }
}

@Service()
export class TopicProgressService extends TopicProgressesService {
  protected readonly http = inject(HttpClient);

  create(topicProgress: NewTopicProgress): Observable<ITopicProgress> {
    const copy = this.convertValueFromClient(topicProgress);
    return this.http.post<RestTopicProgress>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(topicProgress: ITopicProgress): Observable<ITopicProgress> {
    const copy = this.convertValueFromClient(topicProgress);
    return this.http
      .put<RestTopicProgress>(`${this.resourceUrl}/${encodeURIComponent(this.getTopicProgressIdentifier(topicProgress))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(topicProgress: PartialUpdateTopicProgress): Observable<ITopicProgress> {
    const copy = this.convertValueFromClient(topicProgress);
    return this.http
      .patch<RestTopicProgress>(`${this.resourceUrl}/${encodeURIComponent(this.getTopicProgressIdentifier(topicProgress))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ITopicProgress> {
    return this.http
      .get<RestTopicProgress>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ITopicProgress[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTopicProgress[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getTopicProgressIdentifier(topicProgress: Pick<ITopicProgress, 'id'>): number {
    return topicProgress.id;
  }

  compareTopicProgress(o1: Pick<ITopicProgress, 'id'> | null, o2: Pick<ITopicProgress, 'id'> | null): boolean {
    return o1 && o2 ? this.getTopicProgressIdentifier(o1) === this.getTopicProgressIdentifier(o2) : o1 === o2;
  }

  addTopicProgressToCollectionIfMissing<Type extends Pick<ITopicProgress, 'id'>>(
    topicProgressCollection: Type[],
    ...topicProgressesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const topicProgresses: Type[] = topicProgressesToCheck.filter(
      topicProgressItem => topicProgressItem !== null && topicProgressItem !== undefined,
    );
    if (topicProgresses.length > 0) {
      const topicProgressCollectionIdentifiers = topicProgressCollection.map(topicProgressItem =>
        this.getTopicProgressIdentifier(topicProgressItem),
      );
      const topicProgressesToAdd = topicProgresses.filter(topicProgressItem => {
        const topicProgressIdentifier = this.getTopicProgressIdentifier(topicProgressItem);
        if (topicProgressCollectionIdentifiers.includes(topicProgressIdentifier)) {
          return false;
        }
        topicProgressCollectionIdentifiers.push(topicProgressIdentifier);
        return true;
      });
      return [...topicProgressesToAdd, ...topicProgressCollection];
    }
    return topicProgressCollection;
  }

  protected convertValueFromClient<T extends ITopicProgress | NewTopicProgress | PartialUpdateTopicProgress>(topicProgress: T): RestOf<T> {
    return {
      ...topicProgress,
      lastPracticedAt: topicProgress.lastPracticedAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestTopicProgress): ITopicProgress {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestTopicProgress[]): ITopicProgress[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
