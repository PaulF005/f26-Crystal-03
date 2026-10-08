import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IConcept, NewConcept } from '../concept.model';

export type PartialUpdateConcept = Partial<IConcept> & Pick<IConcept, 'id'>;

@Service()
export class ConceptsService {
  readonly conceptsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly conceptsResource = httpResource<IConcept[]>(() => {
    const params = this.conceptsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of concept that have been fetched. It is updated when the conceptsResource emits a new value.
   * In case of error while fetching the concepts, the signal is set to an empty array.
   */
  readonly concepts = computed(() => (this.conceptsResource.hasValue() ? this.conceptsResource.value() : []));
  protected readonly resourceUrl = `${serverApiUrl}api/concepts`;
}

@Service()
export class ConceptService extends ConceptsService {
  protected readonly http = inject(HttpClient);

  create(concept: NewConcept): Observable<IConcept> {
    return this.http.post<IConcept>(this.resourceUrl, concept);
  }

  update(concept: IConcept): Observable<IConcept> {
    return this.http.put<IConcept>(`${this.resourceUrl}/${encodeURIComponent(this.getConceptIdentifier(concept))}`, concept);
  }

  partialUpdate(concept: PartialUpdateConcept): Observable<IConcept> {
    return this.http.patch<IConcept>(`${this.resourceUrl}/${encodeURIComponent(this.getConceptIdentifier(concept))}`, concept);
  }

  find(id: number): Observable<IConcept> {
    return this.http.get<IConcept>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IConcept[]>> {
    const options = createRequestOption(req);
    return this.http.get<IConcept[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getConceptIdentifier(concept: Pick<IConcept, 'id'>): number {
    return concept.id;
  }

  compareConcept(o1: Pick<IConcept, 'id'> | null, o2: Pick<IConcept, 'id'> | null): boolean {
    return o1 && o2 ? this.getConceptIdentifier(o1) === this.getConceptIdentifier(o2) : o1 === o2;
  }

  addConceptToCollectionIfMissing<Type extends Pick<IConcept, 'id'>>(
    conceptCollection: Type[],
    ...conceptsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const concepts: Type[] = conceptsToCheck.filter(conceptItem => conceptItem !== null && conceptItem !== undefined);
    if (concepts.length > 0) {
      const conceptCollectionIdentifiers = conceptCollection.map(conceptItem => this.getConceptIdentifier(conceptItem));
      const conceptsToAdd = concepts.filter(conceptItem => {
        const conceptIdentifier = this.getConceptIdentifier(conceptItem);
        if (conceptCollectionIdentifiers.includes(conceptIdentifier)) {
          return false;
        }
        conceptCollectionIdentifiers.push(conceptIdentifier);
        return true;
      });
      return [...conceptsToAdd, ...conceptCollection];
    }
    return conceptCollection;
  }
}
