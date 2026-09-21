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
import { IConceptProgress } from '../concept-progress.model';
import { ConceptProgressDeleteDialog } from '../delete/concept-progress-delete-dialog';
import { ConceptProgressService } from '../service/concept-progress.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-concept-progress',
  templateUrl: './concept-progress.html',
  imports: [RouterLink, FormsModule, FontAwesomeModule, AlertError, Alert, SortDirective, SortByDirective, FormatMediumDatetimePipe],
})
export class ConceptProgress implements OnInit {
  subscription: Subscription | null = null;
  readonly conceptProgresses = signal<IConceptProgress[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly conceptProgressService = inject(ConceptProgressService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.conceptProgressService.conceptProgressesResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.conceptProgresses.set(this.fillComponentAttributesFromResponseBody([...this.conceptProgressService.conceptProgresses()]));
    });
  }

  trackId = (item: IConceptProgress): number => this.conceptProgressService.getConceptProgressIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (this.conceptProgresses().length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(conceptProgress: IConceptProgress): void {
    const modalRef = this.modalService.open(ConceptProgressDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.conceptProgress = conceptProgress;
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

  protected refineData(data: IConceptProgress[]): IConceptProgress[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IConceptProgress[]): IConceptProgress[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.conceptProgressService.conceptProgressesParams.set(queryObject);
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
