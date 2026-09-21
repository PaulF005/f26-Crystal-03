import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { StageAttemptService } from '../service/stage-attempt.service';
import { IStageAttempt } from '../stage-attempt.model';

const stageAttemptResolve = (route: ActivatedRouteSnapshot): Observable<null | IStageAttempt> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(StageAttemptService);
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

export default stageAttemptResolve;
