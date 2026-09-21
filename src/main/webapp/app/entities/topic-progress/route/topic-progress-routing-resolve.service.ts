import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { TopicProgressService } from '../service/topic-progress.service';
import { ITopicProgress } from '../topic-progress.model';

const topicProgressResolve = (route: ActivatedRouteSnapshot): Observable<null | ITopicProgress> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(TopicProgressService);
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

export default topicProgressResolve;
