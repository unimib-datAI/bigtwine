import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { Observable, ReplaySubject } from 'rxjs';
import {
    AnalysisState,
    AnalysisStatus,
    CancelAnalysis,
    CompleteAnalysis,
    IAnalysis,
    selectCurrentAnalysis,
    StartAnalysis,
    StopAnalysis
} from 'app/analysis';
import { take } from 'rxjs/operators';
import { select, Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AnalysisStatusHistoryComponent } from 'app/analysis/components/analysis-status-history/analysis-status-history.component';
import { AnalysisSettingsComponent } from 'app/analysis/components';

export enum AnalysisToolbarActionBtnType {
    Create = 'create',
    Start = 'start',
    Stop = 'stop',
    Cancel = 'cancel',
    Complete = 'complete',
}

@Component({
    selector: 'btw-analysis-toolbar',
    templateUrl: './analysis-toolbar.component.html',
    styleUrls: ['./analysis-toolbar.component.scss']
})
export class AnalysisToolbarComponent implements OnInit, OnDestroy {
    @Input() showCreateBtn = false;
    @Input() showStartBtn = false;
    @Input() showStopBtn = false;
    @Input() showCompleteBtn = false;
    @Input() showCancelBtn = false;

    @Output() actionBtnClick = new EventEmitter<AnalysisToolbarActionBtnType>();

    protected destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
    currentAnalysis$: Observable<IAnalysis>;

    get currentAnalysis(): IAnalysis {
        let currentAnalysis: IAnalysis = null;
        this.currentAnalysis$
            .pipe(take(1))
            .subscribe((analysis: IAnalysis) => currentAnalysis = analysis);

        return currentAnalysis;
    }

    constructor(
        protected router: Router,
        protected route: ActivatedRoute,
        protected analysisStore: Store<AnalysisState>,
        protected modal: NgbModal) { }

    ngOnInit() {
        this.currentAnalysis$ = this.analysisStore.pipe(select(selectCurrentAnalysis));
    }

    ngOnDestroy() {
        this.destroyed$.next(true);
        this.destroyed$.complete();
    }

    onStatusLabelClick() {
        if (!this.modal.hasOpenModals()) {
            this.modal.open(AnalysisStatusHistoryComponent, {size: 'lg'});
        }
    }

    openSettings() {
        if (!this.modal.hasOpenModals()) {
            this.modal.open(AnalysisSettingsComponent);
        }
    }

    createAnalysis() {
        this.actionBtnClick.emit(AnalysisToolbarActionBtnType.Create);
    }

    startAnalysis() {
        this.actionBtnClick.emit(AnalysisToolbarActionBtnType.Start);

        if (!this.currentAnalysis) {
            return;
        }

        this.analysisStore.dispatch(new StartAnalysis(this.currentAnalysis.id));
    }

    stopAnalysis() {
        this.actionBtnClick.emit(AnalysisToolbarActionBtnType.Stop);

        if (!this.currentAnalysis) {
            return;
        }

        this.analysisStore.dispatch(new StopAnalysis(this.currentAnalysis.id));
    }

    completeAnalysis() {
        this.actionBtnClick.emit(AnalysisToolbarActionBtnType.Complete);

        if (!this.currentAnalysis) {
            return;
        }

        this.analysisStore.dispatch(new CompleteAnalysis(this.currentAnalysis.id));
    }

    cancelAnalysis() {
        this.actionBtnClick.emit(AnalysisToolbarActionBtnType.Cancel);

        if (!this.currentAnalysis) {
            return;
        }

        this.analysisStore.dispatch(new CancelAnalysis(this.currentAnalysis.id));
    }
}
