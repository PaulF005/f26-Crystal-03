import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IConceptProgress, NewConceptProgress } from '../concept-progress.model';

export type PartialUpdateConceptProgress = Partial<IConceptProgress> & Pick<IConceptProgress, 'id'>;

type RestOf<T extends IConceptProgress | NewConceptProgress> = Omit<T, 'lastPracticedAt'> & {
  lastPracticedAt?: string | null;
};

export type RestConceptProgress = RestOf<IConceptProgress>;

export type NewRestConceptProgress = RestOf<NewConceptProgress>;

export type PartialUpdateRestConceptProgress = RestOf<PartialUpdateConceptProgress>;

@Injectable()
export class ConceptProgressesService {
  readonly conceptProgressesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly conceptProgressesResource = httpResource<RestConceptProgress[]>(() => {
    const params = this.conceptProgressesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of conceptProgress that have been fetched. It is updated when the conceptProgressesResource emits a new value.
   * In case of error while fetching the conceptProgresses, the signal is set to an empty array.
   */
  readonly conceptProgresses = computed(() =>
    (this.conceptProgressesResource.hasValue() ? this.conceptProgressesResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/concept-progresses');

  protected convertValueFromServer(restConceptProgress: RestConceptProgress): IConceptProgress {
    return {
      ...restConceptProgress,
      lastPracticedAt: restConceptProgress.lastPracticedAt ? dayjs(restConceptProgress.lastPracticedAt) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class ConceptProgressService extends ConceptProgressesService {
  protected readonly http = inject(HttpClient);

  create(conceptProgress: NewConceptProgress): Observable<IConceptProgress> {
    const copy = this.convertValueFromClient(conceptProgress);
    return this.http.post<RestConceptProgress>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(conceptProgress: IConceptProgress): Observable<IConceptProgress> {
    const copy = this.convertValueFromClient(conceptProgress);
    return this.http
      .put<RestConceptProgress>(`${this.resourceUrl}/${encodeURIComponent(this.getConceptProgressIdentifier(conceptProgress))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(conceptProgress: PartialUpdateConceptProgress): Observable<IConceptProgress> {
    const copy = this.convertValueFromClient(conceptProgress);
    return this.http
      .patch<RestConceptProgress>(`${this.resourceUrl}/${encodeURIComponent(this.getConceptProgressIdentifier(conceptProgress))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IConceptProgress> {
    return this.http
      .get<RestConceptProgress>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IConceptProgress[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestConceptProgress[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getConceptProgressIdentifier(conceptProgress: Pick<IConceptProgress, 'id'>): number {
    return conceptProgress.id;
  }

  compareConceptProgress(o1: Pick<IConceptProgress, 'id'> | null, o2: Pick<IConceptProgress, 'id'> | null): boolean {
    return o1 && o2 ? this.getConceptProgressIdentifier(o1) === this.getConceptProgressIdentifier(o2) : o1 === o2;
  }

  addConceptProgressToCollectionIfMissing<Type extends Pick<IConceptProgress, 'id'>>(
    conceptProgressCollection: Type[],
    ...conceptProgressesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const conceptProgresses: Type[] = conceptProgressesToCheck.filter(isPresent);
    if (conceptProgresses.length > 0) {
      const conceptProgressCollectionIdentifiers = conceptProgressCollection.map(conceptProgressItem =>
        this.getConceptProgressIdentifier(conceptProgressItem),
      );
      const conceptProgressesToAdd = conceptProgresses.filter(conceptProgressItem => {
        const conceptProgressIdentifier = this.getConceptProgressIdentifier(conceptProgressItem);
        if (conceptProgressCollectionIdentifiers.includes(conceptProgressIdentifier)) {
          return false;
        }
        conceptProgressCollectionIdentifiers.push(conceptProgressIdentifier);
        return true;
      });
      return [...conceptProgressesToAdd, ...conceptProgressCollection];
    }
    return conceptProgressCollection;
  }

  protected convertValueFromClient<T extends IConceptProgress | NewConceptProgress | PartialUpdateConceptProgress>(
    conceptProgress: T,
  ): RestOf<T> {
    return {
      ...conceptProgress,
      lastPracticedAt: conceptProgress.lastPracticedAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestConceptProgress): IConceptProgress {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestConceptProgress[]): IConceptProgress[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
