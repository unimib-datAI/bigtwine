import { select, Store } from '@ngrx/store';
import { AnalysisInputType, AnalysisState, AnalysisType, CreateAnalysis, IAnalysis, IDatasetAnalysisInput, selectCurrentAnalysis } from 'app/analysis';
import { Observable, ReplaySubject } from 'rxjs';
import { OnDestroy, OnInit } from '@angular/core';
import { skip, takeUntil } from 'rxjs/operators';
import { Router } from '@angular/router';

export abstract class AnalysisNewComponent implements OnInit, OnDestroy {
    protected destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
    currentAnalysis$: Observable<IAnalysis>;

    protected constructor(
        protected router: Router,
        protected analysisStore: Store<AnalysisState>) {}

    ngOnInit() {
        this.currentAnalysis$ = this.analysisStore.pipe(select(selectCurrentAnalysis));

        this.currentAnalysis$
            .pipe(skip(1))
            .pipe(takeUntil(this.destroyed$))
            .subscribe((analysis: IAnalysis) => {
                if (analysis) {
                    this.onCurrentAnalysisChange(analysis);
                }
            });
    }

    ngOnDestroy(): void {
        this.destroyed$.next(true);
        this.destroyed$.complete();
    }

    onCurrentAnalysisChange(analysis: IAnalysis) {
        this.router
            .navigate([`/analysis/${analysis.type}/${analysis.input.type}/view/${analysis.id}`])
            .catch(e => console.error(e));
    }

    abstract buildAnalysis(): IAnalysis;

    createAnalysis() {
        const analysis: IAnalysis = this.buildAnalysis();

        if (analysis) {
            this.analysisStore.dispatch(new CreateAnalysis(analysis));
        }
    }
}
