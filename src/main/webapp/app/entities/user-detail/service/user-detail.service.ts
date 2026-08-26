import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IUserDetail, NewUserDetail } from '../user-detail.model';

export type PartialUpdateUserDetail = Partial<IUserDetail> & Pick<IUserDetail, 'id'>;

@Injectable()
export class UserDetailsService {
  readonly userDetailsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly userDetailsResource = httpResource<IUserDetail[]>(() => {
    const params = this.userDetailsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of userDetail that have been fetched. It is updated when the userDetailsResource emits a new value.
   * In case of error while fetching the userDetails, the signal is set to an empty array.
   */
  readonly userDetails = computed(() => (this.userDetailsResource.hasValue() ? this.userDetailsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/user-details');
}

@Injectable({ providedIn: 'root' })
export class UserDetailService extends UserDetailsService {
  protected readonly http = inject(HttpClient);

  create(userDetail: NewUserDetail): Observable<IUserDetail> {
    return this.http.post<IUserDetail>(this.resourceUrl, userDetail);
  }

  update(userDetail: IUserDetail): Observable<IUserDetail> {
    return this.http.put<IUserDetail>(`${this.resourceUrl}/${encodeURIComponent(this.getUserDetailIdentifier(userDetail))}`, userDetail);
  }

  partialUpdate(userDetail: PartialUpdateUserDetail): Observable<IUserDetail> {
    return this.http.patch<IUserDetail>(`${this.resourceUrl}/${encodeURIComponent(this.getUserDetailIdentifier(userDetail))}`, userDetail);
  }

  find(id: number): Observable<IUserDetail> {
    return this.http.get<IUserDetail>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IUserDetail[]>> {
    const options = createRequestOption(req);
    return this.http.get<IUserDetail[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getUserDetailIdentifier(userDetail: Pick<IUserDetail, 'id'>): number {
    return userDetail.id;
  }

  compareUserDetail(o1: Pick<IUserDetail, 'id'> | null, o2: Pick<IUserDetail, 'id'> | null): boolean {
    return o1 && o2 ? this.getUserDetailIdentifier(o1) === this.getUserDetailIdentifier(o2) : o1 === o2;
  }

  addUserDetailToCollectionIfMissing<Type extends Pick<IUserDetail, 'id'>>(
    userDetailCollection: Type[],
    ...userDetailsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const userDetails: Type[] = userDetailsToCheck.filter(isPresent);
    if (userDetails.length > 0) {
      const userDetailCollectionIdentifiers = userDetailCollection.map(userDetailItem => this.getUserDetailIdentifier(userDetailItem));
      const userDetailsToAdd = userDetails.filter(userDetailItem => {
        const userDetailIdentifier = this.getUserDetailIdentifier(userDetailItem);
        if (userDetailCollectionIdentifiers.includes(userDetailIdentifier)) {
          return false;
        }
        userDetailCollectionIdentifiers.push(userDetailIdentifier);
        return true;
      });
      return [...userDetailsToAdd, ...userDetailCollection];
    }
    return userDetailCollection;
  }
}
