import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { debounceTime, take, takeUntil } from 'rxjs/operators';
import { Observable, ReplaySubject } from 'rxjs';
import {
    AnalysisState,
    AnalysisStatus,
    GetAnalysisResults,
    IAnalysis,
    IPaginationInfo,
    SearchAnalysisResults,
    selectCurrentAnalysis,
    selectResultsPagination,
    selectSearchPagination,
    isAnalysisTerminated,
} from 'app/analysis';
import { select, Store, Action } from '@ngrx/store';
import { FormControl } from '@angular/forms';
import { IResultsFilterService, RESULTS_FILTER_SERVICE } from 'app/analysis/services/results-filter.service';
import { IResultsFilterQuery } from 'app/analysis/models/results-filter-query.model';
import { IResultsFilterType } from 'app/analysis/models/results-filter-type.model';

@Component({
    selector: 'btw-results-toolbar',
    templateUrl: './results-toolbar.component.html',
    styleUrls: ['./results-toolbar.component.scss']
})
export class ResultsToolbarComponent implements OnInit, OnDestroy {
    private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
    private currentAnalysis$: Observable<IAnalysis>;

    availableSearchTypes: IResultsFilterType[] = [];
    activeSearchType: IResultsFilterType;
    searchQueryControl = new FormControl('');
    searchQuery: string;
    gotoPageNumber: number;

    get currentAnalysis(): IAnalysis {
        let currentAnalysis: IAnalysis = null;
        this.currentAnalysis$
            .pipe(take(1))
            .subscribe((analysis: IAnalysis) => currentAnalysis = analysis);

        return currentAnalysis;
    }

    get liveSearchEnabled() {
        if (!this.currentAnalysis) {
            return true;
        }

        return !isAnalysisTerminated(this.currentAnalysis);
    }

    get shouldSearch() {
        return (this.searchQuery && this.searchQuery.trim().length >= 3);
    }

    get resultsPagination(): IPaginationInfo {
        let pagination = null;
        this.analysisStore
            .select(selectResultsPagination)
            .pipe(take(1))
            .subscribe(p => pagination = p);

        return pagination;
    }

    get searchPagination(): IPaginationInfo {
        let pagination = null;
        this.analysisStore
            .select(selectSearchPagination)
            .pipe(take(1))
            .subscribe(p => pagination = p);

        return pagination;
    }

    get pagination(): IPaginationInfo {
        if (this.searchPagination.enabled) {
            return this.searchPagination;
        } else if (this.resultsPagination.enabled) {
            return this.resultsPagination;
        } else {
            return null;
        }
    }

    get paginationEnabled(): boolean {
        return this.pagination !== null;
    }

    get canExport(): boolean {
        return true;
    }

    get shouldShowExportBtn(): boolean {
        return this.canExport && this.currentAnalysis && isAnalysisTerminated(this.currentAnalysis);
    }

    constructor(
        private analysisStore: Store<AnalysisState>,
        @Inject(RESULTS_FILTER_SERVICE) private resultsFilterService: IResultsFilterService) {}

    ngOnInit(): void {
        this.currentAnalysis$ = this.analysisStore.pipe(select(selectCurrentAnalysis));

        this.searchQueryControl.valueChanges
            .pipe(
                debounceTime(500),
                takeUntil(this.destroyed$),
            )
            .subscribe(() => this.onSearchQueryChange());

        this.currentAnalysis$.pipe(takeUntil(this.destroyed$)).subscribe(() => {
            let searchTypes: IResultsFilterType[];
            if (this.liveSearchEnabled) {
                searchTypes = this.resultsFilterService.localSearchSupportedTypes();
            } else {
                searchTypes = this.resultsFilterService.fullSearchSupportedTypes();
            }

            if (this.availableSearchTypes !== searchTypes) {
                this.availableSearchTypes = searchTypes;
                this.onSearchTypeSelect(this.availableSearchTypes[0]);
            }
        });
    }

    ngOnDestroy(): void {
        this.destroyed$.next(true);
        this.destroyed$.complete();
    }

    onSearchQueryChange() {
        if (!this.currentAnalysis) {
            return;
        }

        if (!this.liveSearchEnabled) {
            return;
        }

        this.performLiveSearch();
    }

    onSearchBtnCLick() {
        if (!this.currentAnalysis) {
            return;
        }

        this.performFullSearch();
    }

    onSearchResetBtnCLick() {
        this.searchQuery = null;
        this.fetchFirstPage();
    }

    onSearchTypeSelect(type: IResultsFilterType) {
        this.activeSearchType = type;
        this.searchQuery = null;
    }

    onChangePageBtnClick(move: number) {
        if (!this.paginationEnabled) {
            return;
        }

        const page = this.pagination.currentPage + move;
        this.gotoPage(page);
    }

    onGoToPageBtnClick() {
        if (!this.paginationEnabled) {
            return;
        }

        this.gotoPage(this.gotoPageNumber - 1);
    }

    gotoPage(page) {
        if (page < 0 || page >= this.pagination.pagesCount) {
            return;
        }

        if (this.searchPagination.enabled) {
            this.performFullSearch(page);
        } else if (this.resultsPagination.enabled) {
            this.fetchPage(page);
        }
    }

    performLiveSearch() {
        if (this.shouldSearch) {
            const query = this.buildQuery();
            const page = {
                page: 0,
                pageSize: this.searchPagination.pageSize
            };

            this.resultsFilterService.localSearch(query, page);
        } else {
            this.resultsFilterService.clear();
        }
    }

    performFullSearch(pageNum = 0) {
        if (this.shouldSearch) {
            const analysisId = this.currentAnalysis.id;
            const query = this.buildQuery();
            const page = {
                page: pageNum,
                pageSize: this.searchPagination.pageSize
            };

            const action: Action = new SearchAnalysisResults(analysisId, query, page);
            this.analysisStore.dispatch(action);
        }
    }

    private buildQuery(): IResultsFilterQuery {
        if (!this.searchQuery) {
            return null;
        }

        return this.resultsFilterService.buildFullSearchQuery(this.activeSearchType.type, this.searchQuery);
    }

    private fetchFirstPage() {
        this.fetchPage(0);
    }

    private fetchPrevPage() {
        this.fetchPage(this.resultsPagination.currentPage - 1);
    }

    private fetchNextPage() {
        this.fetchPage(this.resultsPagination.currentPage + 1);
    }

    private fetchPage(page: number) {
        const pageSize = this.resultsPagination.pageSize;
        const action = new GetAnalysisResults(this.currentAnalysis.id, {page, pageSize});

        this.analysisStore.dispatch(action);
    }
}
