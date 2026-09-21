import { ChangeDetectionStrategy, Component, OnInit, effect, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { Subscription, combineLatest, filter, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { GameProgressDeleteDialog } from '../delete/game-progress-delete-dialog';
import { IGameProgress } from '../game-progress.model';
import { GameProgressService } from '../service/game-progress.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-game-progress',
  templateUrl: './game-progress.html',
  imports: [RouterLink, FormsModule, FontAwesomeModule, AlertError, Alert, SortDirective, SortByDirective, FormatMediumDatetimePipe],
})
export class GameProgress implements OnInit {
  subscription: Subscription | null = null;
  readonly gameProgresses = signal<IGameProgress[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly gameProgressService = inject(GameProgressService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.gameProgressService.gameProgressesResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.gameProgresses.set(this.fillComponentAttributesFromResponseBody([...this.gameProgressService.gameProgresses()]));
    });
  }

  trackId = (item: IGameProgress): number => this.gameProgressService.getGameProgressIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (this.gameProgresses().length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(gameProgress: IGameProgress): void {
    const modalRef = this.modalService.open(GameProgressDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.gameProgress = gameProgress;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.queryBackend();
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(event);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
  }

  protected refineData(data: IGameProgress[]): IGameProgress[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IGameProgress[]): IGameProgress[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.gameProgressService.gameProgressesParams.set(queryObject);
  }

  protected handleNavigation(sortState: SortState): void {
    const queryParamsObj = {
      sort: this.sortService.buildSortParam(sortState),
    };

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }
}
