import { Component, effect, inject, signal, untracked, computed } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { combineLatest, filter, map, tap } from 'rxjs';
import { HttpResponse } from '@angular/common/http';

import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config';
import { Alert, AlertError } from 'app/shared/alert';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { GameDeleteDialog } from '../delete/game-delete-dialog';
import { IGame } from '../game.model';
import { GameService } from '../service/game.service';

// --- CUSTOM MODIFICATION START: Added relational imports ---
import { TopicService } from 'app/entities/topic/service/topic.service';
import { ITopic } from 'app/entities/topic/topic.model';
import { ScenarioService } from 'app/entities/scenario/service/scenario.service';
import { IScenario } from 'app/entities/scenario/scenario.model';
// --- CUSTOM MODIFICATION END ---

@Component({
  selector: 'jhi-game',
  templateUrl: './game.html',
  imports: [RouterLink, FontAwesomeModule, AlertError, Alert, SortDirective, SortByDirective],
})
export class Game {
  readonly games = signal<IGame[]>([]);
  
  // --- CUSTOM MODIFICATION START: Added filter tracking signals ---
  readonly topics = signal<ITopic[]>([]);
  readonly scenarios = signal<IScenario[]>([]);
  readonly selectedTopicId = signal<string>('');
  // --- CUSTOM MODIFICATION END ---

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly gameService = inject(GameService);
  
  // --- CUSTOM MODIFICATION START: Injected custom relational services ---
  protected readonly topicService = inject(TopicService);
  protected readonly scenarioService = inject(ScenarioService);
  // --- CUSTOM MODIFICATION END ---

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

  // --- CUSTOM MODIFICATION START: Added reactive computed property for filtered list ---
  readonly filteredGames = computed(() => {
    const activeTopicId = this.selectedTopicId();
    const currentGamesList = this.games();
    const currentScenariosList = this.scenarios();

    if (!activeTopicId) {
      return currentGamesList;
    }

    const matchingGameIds = currentScenariosList
      .filter(scenario => scenario.topic?.id?.toString() === activeTopicId)
      .map(scenario => scenario.game?.id);

    return currentGamesList.filter(game => matchingGameIds.includes(game.id));
  });
  // --- CUSTOM MODIFICATION END ---

  constructor() {
    effect(() => {
      this.games.set(this.fillComponentAttributesFromResponseBody([...this.gameService.games()]));
    });
    effect(() => {
      const activatedRouteState = this.activatedRouteState();
      untracked(() => {
        this.fillComponentAttributeFromRoute(activatedRouteState.queryParamMap, activatedRouteState.data);
        this.load();
      });
    });
  }

  trackId = (item: IGame): number => this.gameService.getGameIdentifier(item);

  delete(game: IGame): void {
    const modalRef = this.modalService.open(GameDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.game = game;
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.queryBackend();
    // --- CUSTOM MODIFICATION START: Trigger database lookups for topics and scenarios ---
    this.loadFilterRelations();
    // --- CUSTOM MODIFICATION END ---
  }

  // --- CUSTOM MODIFICATION START: Added methods to query backend relations and handle change events ---
  protected loadFilterRelations(): void {
    this.topicService.query().subscribe((res: HttpResponse<ITopic[]>) => {
      this.topics.set(res.body ?? []);
    });

    this.scenarioService.query().subscribe((res: HttpResponse<IScenario[]>) => {
      this.scenarios.set(res.body ?? []);
    });
  }

  onTopicChange(event: Event): void {
    const element = event.target as HTMLSelectElement;
    this.selectedTopicId.set(element.value);
  }
  // --- CUSTOM MODIFICATION END ---

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
