import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IStage, NewStage } from '../stage.model';

export type PartialUpdateStage = Partial<IStage> & Pick<IStage, 'id'>;

@Injectable()
export class StagesService {
  readonly stagesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(undefined);
  readonly stagesResource = httpResource<IStage[]>(() => {
    const params = this.stagesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of stage that have been fetched. It is updated when the stagesResource emits a new value.
   * In case of error while fetching the stages, the signal is set to an empty array.
   */
  readonly stages = computed(() => (this.stagesResource.hasValue() ? this.stagesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/stages');
}

@Injectable({ providedIn: 'root' })
export class StageService extends StagesService {
  protected readonly http = inject(HttpClient);

  create(stage: NewStage): Observable<IStage> {
    return this.http.post<IStage>(this.resourceUrl, stage);
  }

  update(stage: IStage): Observable<IStage> {
    return this.http.put<IStage>(`${this.resourceUrl}/${encodeURIComponent(this.getStageIdentifier(stage))}`, stage);
  }

  partialUpdate(stage: PartialUpdateStage): Observable<IStage> {
    return this.http.patch<IStage>(`${this.resourceUrl}/${encodeURIComponent(this.getStageIdentifier(stage))}`, stage);
  }

  find(id: number): Observable<IStage> {
    return this.http.get<IStage>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IStage[]>> {
    const options = createRequestOption(req);
    return this.http.get<IStage[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getStageIdentifier(stage: Pick<IStage, 'id'>): number {
    return stage.id;
  }

  compareStage(o1: Pick<IStage, 'id'> | null, o2: Pick<IStage, 'id'> | null): boolean {
    return o1 && o2 ? this.getStageIdentifier(o1) === this.getStageIdentifier(o2) : o1 === o2;
  }

  addStageToCollectionIfMissing<Type extends Pick<IStage, 'id'>>(
    stageCollection: Type[],
    ...stagesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const stages: Type[] = stagesToCheck.filter(isPresent);
    if (stages.length > 0) {
      const stageCollectionIdentifiers = stageCollection.map(stageItem => this.getStageIdentifier(stageItem));
      const stagesToAdd = stages.filter(stageItem => {
        const stageIdentifier = this.getStageIdentifier(stageItem);
        if (stageCollectionIdentifiers.includes(stageIdentifier)) {
          return false;
        }
        stageCollectionIdentifiers.push(stageIdentifier);
        return true;
      });
      return [...stagesToAdd, ...stageCollection];
    }
    return stageCollection;
  }
}
