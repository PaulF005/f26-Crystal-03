import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IProgress, NewProgress } from '../progress.model';

export type PartialUpdateProgress = Partial<IProgress> & Pick<IProgress, 'id'>;

@Service()
export class ProgressesService {
  readonly progressesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly progressesResource = httpResource<IProgress[]>(() => {
    const params = this.progressesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of progress that have been fetched. It is updated when the progressesResource emits a new value.
   * In case of error while fetching the progresses, the signal is set to an empty array.
   */
  readonly progresses = computed(() => (this.progressesResource.hasValue() ? this.progressesResource.value() : []));
  protected readonly resourceUrl = `${serverApiUrl}api/progresses`;
}

@Service()
export class ProgressService extends ProgressesService {
  protected readonly http = inject(HttpClient);

  create(progress: NewProgress): Observable<IProgress> {
    return this.http.post<IProgress>(this.resourceUrl, progress);
  }

  update(progress: IProgress): Observable<IProgress> {
    return this.http.put<IProgress>(`${this.resourceUrl}/${encodeURIComponent(this.getProgressIdentifier(progress))}`, progress);
  }

  partialUpdate(progress: PartialUpdateProgress): Observable<IProgress> {
    return this.http.patch<IProgress>(`${this.resourceUrl}/${encodeURIComponent(this.getProgressIdentifier(progress))}`, progress);
  }

  find(id: number): Observable<IProgress> {
    return this.http.get<IProgress>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IProgress[]>> {
    const options = createRequestOption(req);
    return this.http.get<IProgress[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getProgressIdentifier(progress: Pick<IProgress, 'id'>): number {
    return progress.id;
  }

  compareProgress(o1: Pick<IProgress, 'id'> | null, o2: Pick<IProgress, 'id'> | null): boolean {
    return o1 && o2 ? this.getProgressIdentifier(o1) === this.getProgressIdentifier(o2) : o1 === o2;
  }

  addProgressToCollectionIfMissing<Type extends Pick<IProgress, 'id'>>(
    progressCollection: Type[],
    ...progressesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const progresses: Type[] = progressesToCheck.filter(progressItem => progressItem !== null && progressItem !== undefined);
    if (progresses.length > 0) {
      const progressCollectionIdentifiers = progressCollection.map(progressItem => this.getProgressIdentifier(progressItem));
      const progressesToAdd = progresses.filter(progressItem => {
        const progressIdentifier = this.getProgressIdentifier(progressItem);
        if (progressCollectionIdentifiers.includes(progressIdentifier)) {
          return false;
        }
        progressCollectionIdentifiers.push(progressIdentifier);
        return true;
      });
      return [...progressesToAdd, ...progressCollection];
    }
    return progressCollection;
  }
}
