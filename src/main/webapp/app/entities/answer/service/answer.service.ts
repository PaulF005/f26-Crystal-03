import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IAnswer, NewAnswer } from '../answer.model';

export type PartialUpdateAnswer = Partial<IAnswer> & Pick<IAnswer, 'id'>;

@Injectable()
export class AnswersService {
  readonly answersParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly answersResource = httpResource<IAnswer[]>(() => {
    const params = this.answersParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of answer that have been fetched. It is updated when the answersResource emits a new value.
   * In case of error while fetching the answers, the signal is set to an empty array.
   */
  readonly answers = computed(() => (this.answersResource.hasValue() ? this.answersResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/answers');
}

@Injectable({ providedIn: 'root' })
export class AnswerService extends AnswersService {
  protected readonly http = inject(HttpClient);

  create(answer: NewAnswer): Observable<IAnswer> {
    return this.http.post<IAnswer>(this.resourceUrl, answer);
  }

  update(answer: IAnswer): Observable<IAnswer> {
    return this.http.put<IAnswer>(`${this.resourceUrl}/${encodeURIComponent(this.getAnswerIdentifier(answer))}`, answer);
  }

  partialUpdate(answer: PartialUpdateAnswer): Observable<IAnswer> {
    return this.http.patch<IAnswer>(`${this.resourceUrl}/${encodeURIComponent(this.getAnswerIdentifier(answer))}`, answer);
  }

  find(id: number): Observable<IAnswer> {
    return this.http.get<IAnswer>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IAnswer[]>> {
    const options = createRequestOption(req);
    return this.http.get<IAnswer[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getAnswerIdentifier(answer: Pick<IAnswer, 'id'>): number {
    return answer.id;
  }

  compareAnswer(o1: Pick<IAnswer, 'id'> | null, o2: Pick<IAnswer, 'id'> | null): boolean {
    return o1 && o2 ? this.getAnswerIdentifier(o1) === this.getAnswerIdentifier(o2) : o1 === o2;
  }

  addAnswerToCollectionIfMissing<Type extends Pick<IAnswer, 'id'>>(
    answerCollection: Type[],
    ...answersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const answers: Type[] = answersToCheck.filter(isPresent);
    if (answers.length > 0) {
      const answerCollectionIdentifiers = answerCollection.map(answerItem => this.getAnswerIdentifier(answerItem));
      const answersToAdd = answers.filter(answerItem => {
        const answerIdentifier = this.getAnswerIdentifier(answerItem);
        if (answerCollectionIdentifiers.includes(answerIdentifier)) {
          return false;
        }
        answerCollectionIdentifiers.push(answerIdentifier);
        return true;
      });
      return [...answersToAdd, ...answerCollection];
    }
    return answerCollection;
  }
}
