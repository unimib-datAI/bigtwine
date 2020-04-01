import { OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, ReplaySubject } from 'rxjs';
import { first, skip, take, takeUntil } from 'rxjs/operators';
import { select, Store } from '@ngrx/store';
import {
    ActionTypes,
    AnalysisState,
    AnalysisStatus,
    ClearAnalysisResults,
    GetAnalysis,
    GetAnalysisResults,
    IAnalysis,
    selectChangesListeningAnalysisId,
    selectCurrentAnalysis,
    selectLastError,
    selectResultsListeningAnalysisId,
    selectResultsPagination,
    StartListenAnalysisChanges,
    StartListenAnalysisResults,
    StopListenAnalysisChanges,
    StopListenAnalysisResults,
    IPaginationInfo, isAnalysisTerminated, isEndStatus
} from 'app/analysis';
import { AccountService } from 'app/core';

export abstract class AnalysisViewComponent implements OnInit, OnDestroy {
    protected destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

    currentAnalysis$: Observable<IAnalysis>;
    lastError$: Observable<any>;
    currentAnalysis: IAnalysis;

    get paginationInfo(): IPaginationInfo {
        let pagination = null;
        this.analysisStore
            .select(selectResultsPagination)
            .pipe(take(1))
            .subscribe(p => pagination = p);

        return pagination;
    }

    get changesListeningAnalysisId(): string {
        let analysisId = null;
        this.analysisStore
            .select(selectChangesListeningAnalysisId)
            .pipe(take(1))
            .subscribe(aid => analysisId = aid);

        return analysisId;
    }

    get resultsListeningAnalysisId(): string {
        let analysisId = null;
        this.analysisStore
            .select(selectResultsListeningAnalysisId)
            .pipe(take(1))
            .subscribe(aid => analysisId = aid);

        return analysisId;
    }

    abstract get showCreateBtn(): boolean;
    abstract get showStartBtn(): boolean;
    abstract get showStopBtn(): boolean;
    abstract get showCancelBtn(): boolean;
    abstract get showCompleteBtn(): boolean;

    protected constructor(
        protected router: Router,
        protected route: ActivatedRoute,
        protected analysisStore: Store<AnalysisState>,
        protected accountService: AccountService
    ) { }

    ngOnInit() {
        this.currentAnalysis$ = this.analysisStore.select(selectCurrentAnalysis);
        this.lastError$ = this.analysisStore.pipe(select(selectLastError));

        this.currentAnalysis$
            .pipe(takeUntil(this.destroyed$))
            .pipe(skip(1))
            .subscribe((analysis: IAnalysis) => {
                this.onCurrentAnalysisUpdate(analysis);
            });

        this.lastError$
            .pipe(first(e => e && e.type === ActionTypes.GetAnalysisError))
            .pipe(takeUntil(this.destroyed$))
            .subscribe(() => {
                this.handleAnalysisNotFound();
            });

        this.route.paramMap
            .pipe(takeUntil(this.destroyed$))
            .subscribe(() => {
                this.onRouteChange();
            });
    }

    ngOnDestroy(): void {
        this.destroyed$.next(true);
        this.destroyed$.complete();
        this.stopListenAnalysisChanges();
        this.stopListenAnalysisResults();
    }

    onRouteChange() {
        const analysisId = this.route.snapshot.paramMap.get('analysisId');
        if (!analysisId) {
            this.handleAnalysisNotFound();
        } else if (!this.currentAnalysis || this.currentAnalysis.id !== analysisId) {
            this.fetchAnalysis(analysisId);
        }
    }

    onCurrentAnalysisUpdate(updatedAnalysis: IAnalysis) {
        if (!updatedAnalysis) {
            this.handleAnalysisNotFound();
        } else {
            const previousAnalysis = this.currentAnalysis;
            this.currentAnalysis = updatedAnalysis;

            if ((!previousAnalysis && updatedAnalysis) ||
                (previousAnalysis && updatedAnalysis && previousAnalysis.id !== updatedAnalysis.id)) {
                // Different analysis id or we haven't a previous analysis
                this.onCurrentAnalysisChange(this.currentAnalysis, previousAnalysis);
            } else if (previousAnalysis && updatedAnalysis && previousAnalysis.status !== updatedAnalysis.status) {
                // Same analysis id, different status
                this.onAnalysisStatusChange(updatedAnalysis.status as AnalysisStatus, previousAnalysis.status as AnalysisStatus);
            }
        }
    }

    onCurrentAnalysisChange(currentAnalysis: IAnalysis, previousAnalysis: IAnalysis) {
        const path = `/analysis/${currentAnalysis.type}/${currentAnalysis.input.type}/view/${currentAnalysis.id}`;
        if (!this.router.url.startsWith(path)) {
            // Navigate to the correct url
            this.router
                .navigate([path])
                .catch(err => console.error(err));
        }

        this.clearAnalysisResults();
        if (previousAnalysis) {
            this.stopListenAnalysisChanges(previousAnalysis.id);
            this.stopListenAnalysisResults(previousAnalysis.id);
        }

        this.startListenAnalysisChanges(currentAnalysis.id);
        if (isAnalysisTerminated(currentAnalysis)) {
            this.fetchFirstResultsPage();
        } else if (currentAnalysis.status === AnalysisStatus.Started) {
            this.startListenAnalysisResults(currentAnalysis.id);
        }
    }

    onAnalysisStatusChange(newStatus: AnalysisStatus, oldStatus: AnalysisStatus) {
        if (oldStatus === AnalysisStatus.Started) {
            this.stopListenAnalysisResults(this.currentAnalysis.id);
        }

        if (newStatus === AnalysisStatus.Started) {
            this.startListenAnalysisResults(this.currentAnalysis.id);
        } else if (isEndStatus(newStatus)) {
            this.fetchFirstResultsPage();
        }
    }

    handleAnalysisNotFound() {
        this.router
            .navigate([`/analysis/not-found`])
            .catch(err => console.error(err));
    }

    fetchFirstResultsPage() {
        this.fetchResultsPage(0);
    }

    fetchResultsPage(page: number) {
        const pageSize = this.paginationInfo.pageSize;
        const action = new GetAnalysisResults(this.currentAnalysis.id, {page, pageSize});

        this.analysisStore.dispatch(action);
    }

    fetchAnalysis(analysisId: string) {
        this.analysisStore.dispatch(new GetAnalysis(analysisId));
    }

    startListenAnalysisResults(analysisId: string) {
        if (analysisId && this.resultsListeningAnalysisId !== analysisId) {
            this.analysisStore.dispatch(new StartListenAnalysisResults(analysisId));
        }
    }

    stopListenAnalysisResults(analysisId?: string) {
        this.analysisStore.dispatch(new StopListenAnalysisResults(analysisId));
    }

    startListenAnalysisChanges(analysisId: string) {
        if (analysisId && this.changesListeningAnalysisId !== analysisId) {
            this.analysisStore.dispatch(new StartListenAnalysisChanges(analysisId));
        }
    }

    stopListenAnalysisChanges(analysisId?: string) {
        this.analysisStore.dispatch(new StopListenAnalysisChanges(analysisId));
    }

    clearAnalysisResults() {
        this.analysisStore.dispatch(new ClearAnalysisResults());
    }

    protected _checkAnalysisStatus(...statuses: AnalysisStatus[]): boolean {
        return this.currentAnalysis && statuses.indexOf(this.currentAnalysis.status as AnalysisStatus) >= 0;
    }
}
