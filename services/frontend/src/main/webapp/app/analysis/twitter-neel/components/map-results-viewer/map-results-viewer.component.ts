import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { Observable, ReplaySubject } from 'rxjs';
import { map, take, takeUntil, throttleTime } from 'rxjs/operators';
import { select, Store } from '@ngrx/store';
import {
    selectCurrentAnalysis,
    selectResultsPagination,
    IAnalysis,
    AnalysisState,
    IPaginationInfo,
} from 'app/analysis';
import {
    buildNilEntityIdentifier,
    selectAllResources,
    selectAllTweets,
    selectLocationsBySource,
    selectNilEntities,
    selectNilEntitiesTweetsCount,
    selectResourcesTweetsCount,
    TwitterNeelState,
    ILinkedEntity,
    INeelProcessedTweet,
    INilEntity,
    IResource,
    ILocation,
    LocationSource,
} from 'app/analysis/twitter-neel';
import { ResultsViewerComponent } from 'app/analysis/twitter-neel/components/results-viewer.component';
import { IResultsFilterService, RESULTS_FILTER_SERVICE } from 'app/analysis/services/results-filter.service';
import { IResultsFilterQuery } from 'app/analysis/models/results-filter-query.model';
import { DEFAULT_MAP_STYLES, DEFAULT_MAP_BG } from 'app/shared/gmap-styles';
import { ClusterStyle } from '@agm/js-marker-clusterer/services/google-clusterer-types';

@Component({
    templateUrl: './map-results-viewer.component.html',
    styleUrls: ['./map-results-viewer.component.scss'],
    selector: 'btw-map-results-viewer',
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MapResultsViewerComponent extends ResultsViewerComponent implements OnInit, OnDestroy {

    private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

    currentAnalysis$: Observable<IAnalysis>;
    tweets$: Observable<INeelProcessedTweet[]>;
    nilEntities$: Observable<INilEntity[]>;
    linkedEntities$: Observable<ILinkedEntity[]>;
    resources$: Observable<IResource[]>;
    statusLocations$: Observable<ILocation[]>;
    userLocations$: Observable<ILocation[]>;
    resourceLocations$: Observable<ILocation[]>;

    resourcesCounter: {[key: string]: number} = {};
    nilEntitiesCounter: {[key: string]: number} = {};

    selectedTweet: INeelProcessedTweet = null;
    selectedResource: IResource = null;
    selectedNilEntity: INilEntity = null;
    filterQuery: IResultsFilterQuery = null;

    filteredTweets$: Observable<INeelProcessedTweet[]>;
    paginatedTweets$: Observable<INeelProcessedTweet[]>;

    currentPage = 1;
    pageSize = 250;

    mapStyles = DEFAULT_MAP_STYLES;
    mapBg = DEFAULT_MAP_BG;

    get currentAnalysis(): IAnalysis {
        let currentAnalysis: IAnalysis = null;
        this.currentAnalysis$
            .pipe(take(1))
            .subscribe((analysis: IAnalysis) => currentAnalysis = analysis);

        return currentAnalysis;
    }

    get isFilteringEnabled() {
        return this.selectedNilEntity !== null || this.selectedResource !== null || this.filterQuery !== null;
    }

    get paginationInfo(): IPaginationInfo {
        let pagination = null;
        this.tNeelStore
            .select(selectResultsPagination)
            .pipe(take(1))
            .subscribe(p => pagination = p);

        return pagination;
    }

    constructor(private changeDetector: ChangeDetectorRef,
                private analysisStore:  Store<AnalysisState>,
                private tNeelStore: Store<TwitterNeelState>,
                @Inject(RESULTS_FILTER_SERVICE) private resultsFilterService: IResultsFilterService) {
        super();
    }

    ngOnInit(): void {
        this.currentAnalysis$ = this.analysisStore.pipe(select(selectCurrentAnalysis));

        this.tweets$ = this.tNeelStore.pipe(select(selectAllTweets));
        this.nilEntities$ = this.tNeelStore.pipe(select(selectNilEntities));
        this.resources$ = this.tNeelStore.pipe(select(selectAllResources));
        this.paginatedTweets$ = this.tweets$.pipe(
            map(tweets => tweets.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize))
        );

        this.statusLocations$ = this.tNeelStore.pipe(select(selectLocationsBySource(LocationSource.Status)));
        this.resourceLocations$ = this.tNeelStore.pipe(select(selectLocationsBySource(LocationSource.Resource)));
        this.userLocations$ = this.tNeelStore.pipe(select(selectLocationsBySource(LocationSource.TwitterUser)));

        this.tNeelStore.pipe(
            select(selectResourcesTweetsCount),
            takeUntil(this.destroyed$),
        ).subscribe(counters => {
            this.resourcesCounter = counters;
        });

        this.tNeelStore.pipe(
            select(selectNilEntitiesTweetsCount),
            takeUntil(this.destroyed$),
        ).subscribe(counters => {
            this.nilEntitiesCounter = counters;
        });

        this.resultsFilterService.currentQuery$.subscribe(q => {
            this.onTweetsFilterQueryChange(q);
        });
    }

    ngOnDestroy(): void {
        this.destroyed$.next(true);
        this.destroyed$.complete();
    }

    stringifyTweetEntities(entities: ILinkedEntity[]) {
        return entities.map(e => e.value).join(', ');
    }

    nilEntityTweetsCount(entity: INilEntity) {
        return this.nilEntitiesCounter[buildNilEntityIdentifier(entity)];
    }

    onTweetClick(tweet: INeelProcessedTweet) {
        if (this.selectedTweet !== null && this.selectedTweet.status.id === tweet.status.id) {
            this.selectedTweet = null;
        } else {
            this.selectedTweet = tweet;
            this.selectedNilEntity = null;
            this.selectedResource = null;
        }
    }

    onResourceClick(resource: IResource) {
        if (this.selectedResource !== null && this.selectedResource.url === resource.url) {
            this.selectedResource = null;
        } else {
            this.selectedTweet = null;
            this.selectedNilEntity = null;
            this.selectedResource = resource;

            this.filteredTweets$ = this.tweets$
                .pipe(
                    throttleTime(5000),
                    map(allTweets => allTweets.filter(t => t.entities && t.entities
                        .some(e => e.resource && e.resource.url === resource.url))),
                    map(tweets => tweets.slice(0, this.pageSize)),
                );
        }
    }

    onNilEntityClick(entity: INilEntity) {
        if (this.selectedNilEntity !== null &&
            this.selectedNilEntity.value === entity.value &&
            this.selectedNilEntity.nilCluster === entity.nilCluster) {
            this.selectedNilEntity = null;
        } else {
            this.selectedTweet = null;
            this.selectedNilEntity = entity;
            this.selectedResource = null;

            this.filteredTweets$ = this.tweets$
                .pipe(
                    throttleTime(5000),
                    map(allTweets => allTweets.filter(t => t.entities && t.entities
                        .some(e => e.isNil && e.value === entity.value && e.nilCluster === entity.nilCluster))),
                    map(tweets => tweets.slice(0, this.pageSize)),
                );
        }
    }

    onMarkerClick(l: ILocation) {
        if (l.source === LocationSource.Status || l.source === LocationSource.TwitterUser) {
            this.selectedTweet = l.object;
        } else if (l.source === LocationSource.Resource) {
            this.selectedResource = l.object;
        }
    }

    onTweetsFilterQueryChange(query: IResultsFilterQuery) {
        this.filterQuery = query;

        if (query) {
            this.selectedTweet = null;
            this.selectedNilEntity = null;
            this.selectedResource = null;

            this.filteredTweets$ = this.resultsFilterService.filteredResults$;
        }
    }

    tweetCssClass(tweet: INeelProcessedTweet) {
        if (this.selectedTweet != null) {
            return (this.selectedTweet.status.id === tweet.status.id) ? 'item-list__item active' : 'item-list__item inactive';
        } else {
            return 'item-list__item';
        }
    }

    resourceCssClass(resource: IResource) {
        if (this.selectedResource != null) {
            return (this.selectedResource.url === resource.url) ? 'item-list__item active' : 'item-list__item inactive';
        } else {
            return 'item-list__item';
        }
    }

    nilEntityCssClass(entity: INilEntity) {
        if (this.selectedNilEntity != null) {
            return (this.selectedNilEntity.value === entity.value && this.selectedNilEntity.nilCluster === entity.nilCluster) ?
                'item-list__item active' : 'item-list__item inactive';
        } else {
            return 'item-list__item';
        }
    }

    selectedTweetLinkedEntities() {
        return this.selectedTweet.entities.filter(e => !e.isNil);
    }

    selectedTweetNilEntities() {
        return this.selectedTweet.entities.filter(e => e.isNil);
    }

    getClusterStyles(group: string): ClusterStyle[] {
        return [
            {
                url: `content/images/markers/${group}-m1.svg`,
                height: 48,
                width: 48
            },
            {
                url: `content/images/markers/${group}-m2.svg`,
                height: 56,
                width: 56
            },
            {
                url: `content/images/markers/${group}-m3.svg`,
                height: 64,
                width: 64
            },
            {
                url: `content/images/markers/${group}-m4.svg`,
                height: 72,
                width: 72
            },
            {
                url: `content/images/markers/${group}-m5.svg`,
                height: 80,
                width: 80
            }
        ];
    }
}
