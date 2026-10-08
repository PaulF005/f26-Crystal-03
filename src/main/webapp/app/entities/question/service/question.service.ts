import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IQuestion, NewQuestion } from '../question.model';

export type PartialUpdateQuestion = Partial<IQuestion> & Pick<IQuestion, 'id'>;

@Injectable()
export class QuestionsService {
  readonly questionsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly questionsResource = httpResource<IQuestion[]>(() => {
    const params = this.questionsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of question that have been fetched. It is updated when the questionsResource emits a new value.
   * In case of error while fetching the questions, the signal is set to an empty array.
   */
  readonly questions = computed(() => (this.questionsResource.hasValue() ? this.questionsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/questions');
}

@Injectable({ providedIn: 'root' })
export class QuestionService extends QuestionsService {
  protected readonly http = inject(HttpClient);

  create(question: NewQuestion): Observable<IQuestion> {
    return this.http.post<IQuestion>(this.resourceUrl, question);
  }

  update(question: IQuestion): Observable<IQuestion> {
    return this.http.put<IQuestion>(`${this.resourceUrl}/${encodeURIComponent(this.getQuestionIdentifier(question))}`, question);
  }

  partialUpdate(question: PartialUpdateQuestion): Observable<IQuestion> {
    return this.http.patch<IQuestion>(`${this.resourceUrl}/${encodeURIComponent(this.getQuestionIdentifier(question))}`, question);
  }

  find(id: number): Observable<IQuestion> {
    return this.http.get<IQuestion>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IQuestion[]>> {
    const options = createRequestOption(req);
    return this.http.get<IQuestion[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getQuestionIdentifier(question: Pick<IQuestion, 'id'>): number {
    return question.id;
  }

  compareQuestion(o1: Pick<IQuestion, 'id'> | null, o2: Pick<IQuestion, 'id'> | null): boolean {
    return o1 && o2 ? this.getQuestionIdentifier(o1) === this.getQuestionIdentifier(o2) : o1 === o2;
  }

  addQuestionToCollectionIfMissing<Type extends Pick<IQuestion, 'id'>>(
    questionCollection: Type[],
    ...questionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const questions: Type[] = questionsToCheck.filter(isPresent);
    if (questions.length > 0) {
      const questionCollectionIdentifiers = questionCollection.map(questionItem => this.getQuestionIdentifier(questionItem));
      const questionsToAdd = questions.filter(questionItem => {
        const questionIdentifier = this.getQuestionIdentifier(questionItem);
        if (questionCollectionIdentifiers.includes(questionIdentifier)) {
          return false;
        }
        questionCollectionIdentifiers.push(questionIdentifier);
        return true;
      });
      return [...questionsToAdd, ...questionCollection];
    }
    return questionCollection;
  }
}
