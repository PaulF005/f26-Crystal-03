import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ITopic, NewTopic } from '../topic.model';

export type PartialUpdateTopic = Partial<ITopic> & Pick<ITopic, 'id'>;

@Injectable()
export class TopicsService {
  readonly topicsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(undefined);
  readonly topicsResource = httpResource<ITopic[]>(() => {
    const params = this.topicsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of topic that have been fetched. It is updated when the topicsResource emits a new value.
   * In case of error while fetching the topics, the signal is set to an empty array.
   */
  readonly topics = computed(() => (this.topicsResource.hasValue() ? this.topicsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/topics');
}

@Injectable({ providedIn: 'root' })
export class TopicService extends TopicsService {
  protected readonly http = inject(HttpClient);

  create(topic: NewTopic): Observable<ITopic> {
    return this.http.post<ITopic>(this.resourceUrl, topic);
  }

  update(topic: ITopic): Observable<ITopic> {
    return this.http.put<ITopic>(`${this.resourceUrl}/${encodeURIComponent(this.getTopicIdentifier(topic))}`, topic);
  }

  partialUpdate(topic: PartialUpdateTopic): Observable<ITopic> {
    return this.http.patch<ITopic>(`${this.resourceUrl}/${encodeURIComponent(this.getTopicIdentifier(topic))}`, topic);
  }

  find(id: number): Observable<ITopic> {
    return this.http.get<ITopic>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ITopic[]>> {
    const options = createRequestOption(req);
    return this.http.get<ITopic[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getTopicIdentifier(topic: Pick<ITopic, 'id'>): number {
    return topic.id;
  }

  compareTopic(o1: Pick<ITopic, 'id'> | null, o2: Pick<ITopic, 'id'> | null): boolean {
    return o1 && o2 ? this.getTopicIdentifier(o1) === this.getTopicIdentifier(o2) : o1 === o2;
  }

  addTopicToCollectionIfMissing<Type extends Pick<ITopic, 'id'>>(
    topicCollection: Type[],
    ...topicsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const topics: Type[] = topicsToCheck.filter(isPresent);
    if (topics.length > 0) {
      const topicCollectionIdentifiers = topicCollection.map(topicItem => this.getTopicIdentifier(topicItem));
      const topicsToAdd = topics.filter(topicItem => {
        const topicIdentifier = this.getTopicIdentifier(topicItem);
        if (topicCollectionIdentifiers.includes(topicIdentifier)) {
          return false;
        }
        topicCollectionIdentifiers.push(topicIdentifier);
        return true;
      });
      return [...topicsToAdd, ...topicCollection];
    }
    return topicCollection;
  }
}
