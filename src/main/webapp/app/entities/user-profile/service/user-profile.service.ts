import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IUserProfile, NewUserProfile } from '../user-profile.model';

export type PartialUpdateUserProfile = Partial<IUserProfile> & Pick<IUserProfile, 'id'>;

@Injectable()
export class UserProfilesService {
  readonly userProfilesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly userProfilesResource = httpResource<IUserProfile[]>(() => {
    const params = this.userProfilesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of userProfile that have been fetched. It is updated when the userProfilesResource emits a new value.
   * In case of error while fetching the userProfiles, the signal is set to an empty array.
   */
  readonly userProfiles = computed(() => (this.userProfilesResource.hasValue() ? this.userProfilesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/user-profiles');
}

@Injectable({ providedIn: 'root' })
export class UserProfileService extends UserProfilesService {
  protected readonly http = inject(HttpClient);

  create(userProfile: NewUserProfile): Observable<IUserProfile> {
    return this.http.post<IUserProfile>(this.resourceUrl, userProfile);
  }

  update(userProfile: IUserProfile): Observable<IUserProfile> {
    return this.http.put<IUserProfile>(
      `${this.resourceUrl}/${encodeURIComponent(this.getUserProfileIdentifier(userProfile))}`,
      userProfile,
    );
  }

  partialUpdate(userProfile: PartialUpdateUserProfile): Observable<IUserProfile> {
    return this.http.patch<IUserProfile>(
      `${this.resourceUrl}/${encodeURIComponent(this.getUserProfileIdentifier(userProfile))}`,
      userProfile,
    );
  }

  find(id: number): Observable<IUserProfile> {
    return this.http.get<IUserProfile>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IUserProfile[]>> {
    const options = createRequestOption(req);
    return this.http.get<IUserProfile[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getUserProfileIdentifier(userProfile: Pick<IUserProfile, 'id'>): number {
    return userProfile.id;
  }

  compareUserProfile(o1: Pick<IUserProfile, 'id'> | null, o2: Pick<IUserProfile, 'id'> | null): boolean {
    return o1 && o2 ? this.getUserProfileIdentifier(o1) === this.getUserProfileIdentifier(o2) : o1 === o2;
  }

  addUserProfileToCollectionIfMissing<Type extends Pick<IUserProfile, 'id'>>(
    userProfileCollection: Type[],
    ...userProfilesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const userProfiles: Type[] = userProfilesToCheck.filter(isPresent);
    if (userProfiles.length > 0) {
      const userProfileCollectionIdentifiers = userProfileCollection.map(userProfileItem => this.getUserProfileIdentifier(userProfileItem));
      const userProfilesToAdd = userProfiles.filter(userProfileItem => {
        const userProfileIdentifier = this.getUserProfileIdentifier(userProfileItem);
        if (userProfileCollectionIdentifiers.includes(userProfileIdentifier)) {
          return false;
        }
        userProfileCollectionIdentifiers.push(userProfileIdentifier);
        return true;
      });
      return [...userProfilesToAdd, ...userProfileCollection];
    }
    return userProfileCollection;
  }
}
