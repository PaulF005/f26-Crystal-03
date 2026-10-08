import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IScenario, NewScenario } from '../scenario.model';

export type PartialUpdateScenario = Partial<IScenario> & Pick<IScenario, 'id'>;

@Injectable()
export class ScenariosService {
  readonly scenariosParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly scenariosResource = httpResource<IScenario[]>(() => {
    const params = this.scenariosParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of scenario that have been fetched. It is updated when the scenariosResource emits a new value.
   * In case of error while fetching the scenarios, the signal is set to an empty array.
   */
  readonly scenarios = computed(() => (this.scenariosResource.hasValue() ? this.scenariosResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/scenarios');
}

@Injectable({ providedIn: 'root' })
export class ScenarioService extends ScenariosService {
  protected readonly http = inject(HttpClient);

  create(scenario: NewScenario): Observable<IScenario> {
    return this.http.post<IScenario>(this.resourceUrl, scenario);
  }

  update(scenario: IScenario): Observable<IScenario> {
    return this.http.put<IScenario>(`${this.resourceUrl}/${encodeURIComponent(this.getScenarioIdentifier(scenario))}`, scenario);
  }

  partialUpdate(scenario: PartialUpdateScenario): Observable<IScenario> {
    return this.http.patch<IScenario>(`${this.resourceUrl}/${encodeURIComponent(this.getScenarioIdentifier(scenario))}`, scenario);
  }

  find(id: number): Observable<IScenario> {
    return this.http.get<IScenario>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IScenario[]>> {
    const options = createRequestOption(req);
    return this.http.get<IScenario[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getScenarioIdentifier(scenario: Pick<IScenario, 'id'>): number {
    return scenario.id;
  }

  compareScenario(o1: Pick<IScenario, 'id'> | null, o2: Pick<IScenario, 'id'> | null): boolean {
    return o1 && o2 ? this.getScenarioIdentifier(o1) === this.getScenarioIdentifier(o2) : o1 === o2;
  }

  addScenarioToCollectionIfMissing<Type extends Pick<IScenario, 'id'>>(
    scenarioCollection: Type[],
    ...scenariosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const scenarios: Type[] = scenariosToCheck.filter(isPresent);
    if (scenarios.length > 0) {
      const scenarioCollectionIdentifiers = scenarioCollection.map(scenarioItem => this.getScenarioIdentifier(scenarioItem));
      const scenariosToAdd = scenarios.filter(scenarioItem => {
        const scenarioIdentifier = this.getScenarioIdentifier(scenarioItem);
        if (scenarioCollectionIdentifiers.includes(scenarioIdentifier)) {
          return false;
        }
        scenarioCollectionIdentifiers.push(scenarioIdentifier);
        return true;
      });
      return [...scenariosToAdd, ...scenarioCollection];
    }
    return scenarioCollection;
  }
}
