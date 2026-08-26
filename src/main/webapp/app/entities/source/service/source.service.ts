import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ISource, NewSource } from '../source.model';

export type PartialUpdateSource = Partial<ISource> & Pick<ISource, 'id'>;

@Injectable()
export class SourcesService {
  readonly sourcesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly sourcesResource = httpResource<ISource[]>(() => {
    const params = this.sourcesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of source that have been fetched. It is updated when the sourcesResource emits a new value.
   * In case of error while fetching the sources, the signal is set to an empty array.
   */
  readonly sources = computed(() => (this.sourcesResource.hasValue() ? this.sourcesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/sources');
}

@Injectable({ providedIn: 'root' })
export class SourceService extends SourcesService {
  protected readonly http = inject(HttpClient);

  create(source: NewSource): Observable<ISource> {
    return this.http.post<ISource>(this.resourceUrl, source);
  }

  update(source: ISource): Observable<ISource> {
    return this.http.put<ISource>(`${this.resourceUrl}/${encodeURIComponent(this.getSourceIdentifier(source))}`, source);
  }

  partialUpdate(source: PartialUpdateSource): Observable<ISource> {
    return this.http.patch<ISource>(`${this.resourceUrl}/${encodeURIComponent(this.getSourceIdentifier(source))}`, source);
  }

  find(id: number): Observable<ISource> {
    return this.http.get<ISource>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ISource[]>> {
    const options = createRequestOption(req);
    return this.http.get<ISource[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getSourceIdentifier(source: Pick<ISource, 'id'>): number {
    return source.id;
  }

  compareSource(o1: Pick<ISource, 'id'> | null, o2: Pick<ISource, 'id'> | null): boolean {
    return o1 && o2 ? this.getSourceIdentifier(o1) === this.getSourceIdentifier(o2) : o1 === o2;
  }

  addSourceToCollectionIfMissing<Type extends Pick<ISource, 'id'>>(
    sourceCollection: Type[],
    ...sourcesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const sources: Type[] = sourcesToCheck.filter(isPresent);
    if (sources.length > 0) {
      const sourceCollectionIdentifiers = sourceCollection.map(sourceItem => this.getSourceIdentifier(sourceItem));
      const sourcesToAdd = sources.filter(sourceItem => {
        const sourceIdentifier = this.getSourceIdentifier(sourceItem);
        if (sourceCollectionIdentifiers.includes(sourceIdentifier)) {
          return false;
        }
        sourceCollectionIdentifiers.push(sourceIdentifier);
        return true;
      });
      return [...sourcesToAdd, ...sourceCollection];
    }
    return sourceCollection;
  }
}
