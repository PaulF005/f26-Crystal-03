import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IFeedback, NewFeedback } from '../feedback.model';

export type PartialUpdateFeedback = Partial<IFeedback> & Pick<IFeedback, 'id'>;

@Injectable()
export class FeedbacksService {
  readonly feedbacksParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly feedbacksResource = httpResource<IFeedback[]>(() => {
    const params = this.feedbacksParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of feedback that have been fetched. It is updated when the feedbacksResource emits a new value.
   * In case of error while fetching the feedbacks, the signal is set to an empty array.
   */
  readonly feedbacks = computed(() => (this.feedbacksResource.hasValue() ? this.feedbacksResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/feedbacks');
}

@Injectable({ providedIn: 'root' })
export class FeedbackService extends FeedbacksService {
  protected readonly http = inject(HttpClient);

  create(feedback: NewFeedback): Observable<IFeedback> {
    return this.http.post<IFeedback>(this.resourceUrl, feedback);
  }

  update(feedback: IFeedback): Observable<IFeedback> {
    return this.http.put<IFeedback>(`${this.resourceUrl}/${encodeURIComponent(this.getFeedbackIdentifier(feedback))}`, feedback);
  }

  partialUpdate(feedback: PartialUpdateFeedback): Observable<IFeedback> {
    return this.http.patch<IFeedback>(`${this.resourceUrl}/${encodeURIComponent(this.getFeedbackIdentifier(feedback))}`, feedback);
  }

  find(id: number): Observable<IFeedback> {
    return this.http.get<IFeedback>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IFeedback[]>> {
    const options = createRequestOption(req);
    return this.http.get<IFeedback[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getFeedbackIdentifier(feedback: Pick<IFeedback, 'id'>): number {
    return feedback.id;
  }

  compareFeedback(o1: Pick<IFeedback, 'id'> | null, o2: Pick<IFeedback, 'id'> | null): boolean {
    return o1 && o2 ? this.getFeedbackIdentifier(o1) === this.getFeedbackIdentifier(o2) : o1 === o2;
  }

  addFeedbackToCollectionIfMissing<Type extends Pick<IFeedback, 'id'>>(
    feedbackCollection: Type[],
    ...feedbacksToCheck: (Type | null | undefined)[]
  ): Type[] {
    const feedbacks: Type[] = feedbacksToCheck.filter(isPresent);
    if (feedbacks.length > 0) {
      const feedbackCollectionIdentifiers = feedbackCollection.map(feedbackItem => this.getFeedbackIdentifier(feedbackItem));
      const feedbacksToAdd = feedbacks.filter(feedbackItem => {
        const feedbackIdentifier = this.getFeedbackIdentifier(feedbackItem);
        if (feedbackCollectionIdentifiers.includes(feedbackIdentifier)) {
          return false;
        }
        feedbackCollectionIdentifiers.push(feedbackIdentifier);
        return true;
      });
      return [...feedbacksToAdd, ...feedbackCollection];
    }
    return feedbackCollection;
  }
}
