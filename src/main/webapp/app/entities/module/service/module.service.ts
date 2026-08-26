import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IModule, NewModule } from '../module.model';

export type PartialUpdateModule = Partial<IModule> & Pick<IModule, 'id'>;

@Injectable()
export class ModulesService {
  readonly modulesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly modulesResource = httpResource<IModule[]>(() => {
    const params = this.modulesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of module that have been fetched. It is updated when the modulesResource emits a new value.
   * In case of error while fetching the modules, the signal is set to an empty array.
   */
  readonly modules = computed(() => (this.modulesResource.hasValue() ? this.modulesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/modules');
}

@Injectable({ providedIn: 'root' })
export class ModuleService extends ModulesService {
  protected readonly http = inject(HttpClient);

  create(module: NewModule): Observable<IModule> {
    return this.http.post<IModule>(this.resourceUrl, module);
  }

  update(module: IModule): Observable<IModule> {
    return this.http.put<IModule>(`${this.resourceUrl}/${encodeURIComponent(this.getModuleIdentifier(module))}`, module);
  }

  partialUpdate(module: PartialUpdateModule): Observable<IModule> {
    return this.http.patch<IModule>(`${this.resourceUrl}/${encodeURIComponent(this.getModuleIdentifier(module))}`, module);
  }

  find(id: number): Observable<IModule> {
    return this.http.get<IModule>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IModule[]>> {
    const options = createRequestOption(req);
    return this.http.get<IModule[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getModuleIdentifier(module: Pick<IModule, 'id'>): number {
    return module.id;
  }

  compareModule(o1: Pick<IModule, 'id'> | null, o2: Pick<IModule, 'id'> | null): boolean {
    return o1 && o2 ? this.getModuleIdentifier(o1) === this.getModuleIdentifier(o2) : o1 === o2;
  }

  addModuleToCollectionIfMissing<Type extends Pick<IModule, 'id'>>(
    moduleCollection: Type[],
    ...modulesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const modules: Type[] = modulesToCheck.filter(isPresent);
    if (modules.length > 0) {
      const moduleCollectionIdentifiers = moduleCollection.map(moduleItem => this.getModuleIdentifier(moduleItem));
      const modulesToAdd = modules.filter(moduleItem => {
        const moduleIdentifier = this.getModuleIdentifier(moduleItem);
        if (moduleCollectionIdentifiers.includes(moduleIdentifier)) {
          return false;
        }
        moduleCollectionIdentifiers.push(moduleIdentifier);
        return true;
      });
      return [...modulesToAdd, ...moduleCollection];
    }
    return moduleCollection;
  }
}
