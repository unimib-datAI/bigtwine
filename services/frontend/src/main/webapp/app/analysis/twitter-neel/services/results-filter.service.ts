import { AnalysisState, IAnalysis, IPage, selectCurrentAnalysis } from 'app/analysis';
import { filter, map, take, takeUntil, throttleTime } from 'rxjs/operators';
import { BehaviorSubject, Observable } from 'rxjs';
import { select, Store } from '@ngrx/store';
import { INeelProcessedTweet, selectAllTweets, TwitterNeelState, SPARQL_NS_PREFIXES } from 'app/analysis/twitter-neel';
import { DEFAULT_RESULTS_FILTER_THROTTLE, IResultsFilterService } from 'app/analysis/services/results-filter.service';
import { Injectable } from '@angular/core';
import { IResultsFilterQuery } from 'app/analysis/models/results-filter-query.model';
import { IResultsFilterType } from 'app/analysis/models/results-filter-type.model';

@Injectable()
export class ResultsFilterService implements IResultsFilterService {
    private currentAnalysis$: Observable<IAnalysis>;
    private tweets$: Observable<INeelProcessedTweet[]>;
    private _filteredResults$ = new BehaviorSubject<INeelProcessedTweet[]>(null);
    private _currentQuery: IResultsFilterQuery;
    private _currentQuery$ = new BehaviorSubject<IResultsFilterQuery>(null);

    get filteredResults$() {
        return this._filteredResults$;
    }

    get currentQuery() {
        return this._currentQuery;
    }

    get currentQuery$() {
        return this._currentQuery$;
    }

    private get currentAnalysis(): IAnalysis {
        let currentAnalysis: IAnalysis = null;
        this.currentAnalysis$
            .pipe(take(1))
            .subscribe((analysis: IAnalysis) => currentAnalysis = analysis);

        return currentAnalysis;
    }

    constructor(private analysisStore: Store<AnalysisState>, private tNeelStore: Store<TwitterNeelState>) {
        this.currentAnalysis$ = this.analysisStore.pipe(select(selectCurrentAnalysis));
        this.tweets$ = this.tNeelStore.pipe(select(selectAllTweets));
    }

    /**
     * @inheritDoc
     */
    localSearch(query: IResultsFilterQuery, page: IPage, throttleDuration = DEFAULT_RESULTS_FILTER_THROTTLE) {
        if (!query) {
            return;
        }

        this._currentQuery = query;
        this._currentQuery$.next(query);
        this.tweets$
            .pipe(
                throttleTime(throttleDuration),
                map(allTweets => allTweets.filter(t => t.status.text.indexOf(query.value) >= 0)),
                map(tweets => tweets.slice(0, page.pageSize)),
                takeUntil(this._currentQuery$.pipe(filter(q => q !== query))),
            )
            .subscribe(tweets => {
                this._filteredResults$.next(tweets);
            });
    }

    clear() {
        this._currentQuery = null;
        this._currentQuery$.next(null);
        this._filteredResults$.next(null);
    }

    fullSearchSupportedTypes(): IResultsFilterType[] {
        return [
            {type: 'text', label: 'Text'},
            {type: 'category', label: 'Category', options: [
                {value: 'Character', label: 'Character'},
                {value: 'Event', label: 'Event'},
                {value: 'Location', label: 'Location'},
                {value: 'Organization', label: 'Organization'},
                {value: 'Person', label: 'Person'},
                {value: 'Product', label: 'Product'},
                {value: 'Thing', label: 'Thing'},
            ]},
            {type: 'rdf:type', label: 'rdf:Type'},
        ];
    }

    localSearchSupportedTypes(): IResultsFilterType[] {
        return [
            {type: 'text', label: 'Text'}
        ];
    }

    buildFullSearchQuery(type: string, value: any): IResultsFilterQuery {
        let compiledQuery;
        switch (type) {
            case 'category':
                compiledQuery = `
                    {"payload.entities.category": ${JSON.stringify(value)}}
                `;
                break;
            case 'rdf:type':
                const parts = value.split(':');
                if (parts.length > 0 && SPARQL_NS_PREFIXES[parts[0]]) {
                    value = SPARQL_NS_PREFIXES[parts[0]] + parts.slice(1).join(':');
                }
                compiledQuery = `
                    {"payload.entities.resource.extra.rdfType": {$in: [${JSON.stringify(value)}]}}
                `;
                break;
            default:
                break;
        }

        return {
            type,
            value,
            compiledQuery
        };
    }
}
