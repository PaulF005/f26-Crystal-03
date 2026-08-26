import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IModule } from '../module.model';
import { ModuleService } from '../service/module.service';

const moduleResolve = (route: ActivatedRouteSnapshot): Observable<null | IModule> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(ModuleService);
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

export default moduleResolve;
