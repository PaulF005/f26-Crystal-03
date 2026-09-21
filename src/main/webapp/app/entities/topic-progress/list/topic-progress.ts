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
import { TopicProgressDeleteDialog } from '../delete/topic-progress-delete-dialog';
import { TopicProgressService } from '../service/topic-progress.service';
import { ITopicProgress } from '../topic-progress.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-topic-progress',
  templateUrl: './topic-progress.html',
  imports: [RouterLink, FormsModule, FontAwesomeModule, AlertError, Alert, SortDirective, SortByDirective, FormatMediumDatetimePipe],
})
export class TopicProgress implements OnInit {
  subscription: Subscription | null = null;
  readonly topicProgresses = signal<ITopicProgress[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly topicProgressService = inject(TopicProgressService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.topicProgressService.topicProgressesResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.topicProgresses.set(this.fillComponentAttributesFromResponseBody([...this.topicProgressService.topicProgresses()]));
    });
  }

  trackId = (item: ITopicProgress): number => this.topicProgressService.getTopicProgressIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (this.topicProgresses().length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(topicProgress: ITopicProgress): void {
    const modalRef = this.modalService.open(TopicProgressDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.topicProgress = topicProgress;
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

  protected refineData(data: ITopicProgress[]): ITopicProgress[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: ITopicProgress[]): ITopicProgress[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.topicProgressService.topicProgressesParams.set(queryObject);
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
