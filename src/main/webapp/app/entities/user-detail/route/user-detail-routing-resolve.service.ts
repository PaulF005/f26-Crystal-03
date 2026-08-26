import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { UserDetailService } from '../service/user-detail.service';
import { IUserDetail } from '../user-detail.model';

const userDetailResolve = (route: ActivatedRouteSnapshot): Observable<null | IUserDetail> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(UserDetailService);
    return service.find(id).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404) {
          router.navigate(['404']);
        } else {
          router.navigate(['error']);
        }
        return EMPTY;
      }),
    );
  }

  return of(null);
};

export default userDetailResolve;
