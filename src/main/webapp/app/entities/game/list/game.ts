import { Component, computed, effect, inject, signal, untracked } from '@angular/core';
import { ScenarioService } from '../../scenario/service/scenario.service';
import { ITopic } from '../../topic/topic.model';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { combineLatest, filter, map, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config';
import { Alert, AlertError } from 'app/shared/alert';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { GameDeleteDialog } from '../delete/game-delete-dialog';
import { IGame } from '../game.model';
import { GameService } from '../service/game.service';

@Component({
  selector: 'jhi-game',
  templateUrl: './game.html',
  imports: [RouterLink, FontAwesomeModule, AlertError, Alert, SortDirective, SortByDirective],
  providers: [ScenarioService],
})
export class Game {
  readonly games = signal<IGame[]>([]);
  readonly selectedTopicId = signal<number | null>(null);
  readonly scenarioService = inject(ScenarioService);

  readonly topics = computed<ITopic[]>(() => {
    const topicsById = new Map<number, ITopic>();

    for (const scenario of this.scenarioService.scenarios()) {
      if (scenario.topic) {
        topicsById.set(scenario.topic.id, scenario.topic);
      }
    }

    return [...topicsById.values()].sort((a, b) => (a.name ?? '').localeCompare(b.name ?? ''));
  });

  readonly filteredGames = computed(() => {
    const topicId = this.selectedTopicId();

    if (topicId === null) {
      return this.games();
    }

    const gameIds = new Set(
      this.scenarioService
        .scenarios()
        .filter(scenario => scenario.topic?.id === topicId && scenario.game)
        .map(scenario => scenario.game!.id),
    );

    return this.games().filter(game => gameIds.has(game.id));
  });
  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly gameService = inject(GameService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.gameService.gamesResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly activatedRouteState = toSignal(
    combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data]).pipe(
      map(([queryParamMap, data]) => ({ queryParamMap, data })),
    ),
    { initialValue: { queryParamMap: this.activatedRoute.snapshot.queryParamMap, data: this.activatedRoute.snapshot.data } },
  );
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    this.scenarioService.scenariosParams.set({});
    effect(() => {
      this.games.set(this.fillComponentAttributesFromResponseBody([...this.gameService.games()]));
    });
    effect(() => {
      const activatedRouteState = this.activatedRouteState();
      untracked(() => {
        // Only watch for route changes. Other signals should be ignored.
        this.fillComponentAttributeFromRoute(activatedRouteState.queryParamMap, activatedRouteState.data);
        this.load();
      });
    });
  }

  trackId = (item: IGame): number => this.gameService.getGameIdentifier(item);
  onTopicChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.selectedTopicId.set(value ? Number(value) : null);
  }
  delete(game: IGame): void {
    const modalRef = this.modalService.open(GameDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.game = game;
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

  protected refineData(data: IGame[]): IGame[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IGame[]): IGame[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.gameService.gamesParams.set(queryObject);
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
