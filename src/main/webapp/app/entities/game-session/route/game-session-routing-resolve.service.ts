import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IGameSession } from '../game-session.model';
import { GameSessionService } from '../service/game-session.service';

const gameSessionResolve = (route: ActivatedRouteSnapshot): Observable<null | IGameSession> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(GameSessionService);
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

export default gameSessionResolve;
