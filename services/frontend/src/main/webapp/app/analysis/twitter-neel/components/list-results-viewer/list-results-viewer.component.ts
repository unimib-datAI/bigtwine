import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { ResultsViewerComponent } from 'app/analysis/twitter-neel/components/results-viewer.component';
import { Observable, ReplaySubject } from 'rxjs';
import { select, Store } from '@ngrx/store';
import { selectAllTweets, TwitterNeelState, INeelProcessedTweet, ILinkedEntity } from 'app/analysis/twitter-neel';
import { map } from 'rxjs/operators';
import { IResultsFilterService, RESULTS_FILTER_SERVICE } from 'app/analysis/services/results-filter.service';
import { IResultsFilterQuery } from 'app/analysis/models/results-filter-query.model';

@Component({
    selector: 'btw-list-results-viewer',
    templateUrl: './list-results-viewer.component.html',
    styleUrls: ['./list-results-viewer.component.scss']
})
export class ListResultsViewerComponent extends ResultsViewerComponent implements OnInit, OnDestroy {

    private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

    private _tweets$: Observable<INeelProcessedTweet[]>;

    filterQuery: IResultsFilterQuery = null;

    filteredTweets$: Observable<INeelProcessedTweet[]>;
    paginatedTweets$: Observable<INeelProcessedTweet[]>;
    get tweets$(): Observable<INeelProcessedTweet[]> {
        if (this.isFilteringEnabled) {
            return this.filteredTweets$;
        } else {
            return this.paginatedTweets$;
        }
    }

    currentPage = 1;
    pageSize = 250;
    selectedEntity: ILinkedEntity;
    selectedEntityTweet: INeelProcessedTweet;

    get isFilteringEnabled() {
        return this.filterQuery !== null;
    }

    constructor(private tNeelStore: Store<TwitterNeelState>,
                @Inject(RESULTS_FILTER_SERVICE) private resultsFilterService: IResultsFilterService) {
        super();
    }

    ngOnInit() {
        this._tweets$ = this.tNeelStore.pipe(select(selectAllTweets));
        this.paginatedTweets$ = this._tweets$.pipe(
            map(tweets => tweets.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize))
        );

        this.resultsFilterService.currentQuery$.subscribe(q => {
            this.onTweetsFilterQueryChange(q);
        });
    }

    ngOnDestroy(): void {
        this.destroyed$.next(true);
        this.destroyed$.complete();
    }

    onTweetsFilterQueryChange(query: IResultsFilterQuery) {
        this.filterQuery = query;

        if (query) {
            this.filteredTweets$ = this.resultsFilterService.filteredResults$;
        }
    }

    onSelectedEntityChange(entity: ILinkedEntity, tweet: INeelProcessedTweet) {
        this.selectedEntityTweet = entity ? tweet : null;
    }

}
