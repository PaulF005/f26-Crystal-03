import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { StageService } from '../service/stage.service';
import { IStage } from '../stage.model';

const stageResolve = (route: ActivatedRouteSnapshot): Observable<null | IStage> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(StageService);
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

export default stageResolve;
