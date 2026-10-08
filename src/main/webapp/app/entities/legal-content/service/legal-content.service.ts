import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { ILegalContent, NewLegalContent } from '../legal-content.model';

export type PartialUpdateLegalContent = Partial<ILegalContent> & Pick<ILegalContent, 'id'>;

@Service()
export class LegalContentsService {
  readonly legalContentsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly legalContentsResource = httpResource<ILegalContent[]>(() => {
    const params = this.legalContentsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of legalContent that have been fetched. It is updated when the legalContentsResource emits a new value.
   * In case of error while fetching the legalContents, the signal is set to an empty array.
   */
  readonly legalContents = computed(() => (this.legalContentsResource.hasValue() ? this.legalContentsResource.value() : []));
  protected readonly resourceUrl = `${serverApiUrl}api/legal-contents`;
}

@Service()
export class LegalContentService extends LegalContentsService {
  protected readonly http = inject(HttpClient);

  create(legalContent: NewLegalContent): Observable<ILegalContent> {
    return this.http.post<ILegalContent>(this.resourceUrl, legalContent);
  }

  update(legalContent: ILegalContent): Observable<ILegalContent> {
    return this.http.put<ILegalContent>(
      `${this.resourceUrl}/${encodeURIComponent(this.getLegalContentIdentifier(legalContent))}`,
      legalContent,
    );
  }

  partialUpdate(legalContent: PartialUpdateLegalContent): Observable<ILegalContent> {
    return this.http.patch<ILegalContent>(
      `${this.resourceUrl}/${encodeURIComponent(this.getLegalContentIdentifier(legalContent))}`,
      legalContent,
    );
  }

  find(id: number): Observable<ILegalContent> {
    return this.http.get<ILegalContent>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ILegalContent[]>> {
    const options = createRequestOption(req);
    return this.http.get<ILegalContent[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getLegalContentIdentifier(legalContent: Pick<ILegalContent, 'id'>): number {
    return legalContent.id;
  }

  compareLegalContent(o1: Pick<ILegalContent, 'id'> | null, o2: Pick<ILegalContent, 'id'> | null): boolean {
    return o1 && o2 ? this.getLegalContentIdentifier(o1) === this.getLegalContentIdentifier(o2) : o1 === o2;
  }

  addLegalContentToCollectionIfMissing<Type extends Pick<ILegalContent, 'id'>>(
    legalContentCollection: Type[],
    ...legalContentsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const legalContents: Type[] = legalContentsToCheck.filter(
      legalContentItem => legalContentItem !== null && legalContentItem !== undefined,
    );
    if (legalContents.length > 0) {
      const legalContentCollectionIdentifiers = legalContentCollection.map(legalContentItem =>
        this.getLegalContentIdentifier(legalContentItem),
      );
      const legalContentsToAdd = legalContents.filter(legalContentItem => {
        const legalContentIdentifier = this.getLegalContentIdentifier(legalContentItem);
        if (legalContentCollectionIdentifiers.includes(legalContentIdentifier)) {
          return false;
        }
        legalContentCollectionIdentifiers.push(legalContentIdentifier);
        return true;
      });
      return [...legalContentsToAdd, ...legalContentCollection];
    }
    return legalContentCollection;
  }
}
