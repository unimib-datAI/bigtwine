import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { AnalysisState, IAnalysis, selectCurrentAnalysis } from 'app/analysis';
import { take } from 'rxjs/operators';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'btw-analysis-status-history',
    templateUrl: './analysis-status-history.component.html',
    styleUrls: ['./analysis-status-history.component.scss']
})
export class AnalysisStatusHistoryComponent {
    get currentAnalysis(): IAnalysis {
        let currentAnalysis: IAnalysis = null;
        this.analysisStore
            .select(selectCurrentAnalysis)
            .pipe(take(1))
            .subscribe((analysis: IAnalysis) => currentAnalysis = analysis);

        return currentAnalysis;
    }

    constructor(
        protected analysisStore: Store<AnalysisState>,
        public activeModal: NgbActiveModal) {}
}
