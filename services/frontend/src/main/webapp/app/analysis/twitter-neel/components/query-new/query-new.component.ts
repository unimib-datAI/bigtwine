import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { AnalysisState } from 'app/analysis/store';
import { ClearTwitterNeelResults, TwitterNeelState } from 'app/analysis/twitter-neel';
import { AnalysisInputType, AnalysisType, IAnalysis } from 'app/analysis';
import { AnalysisNewComponent, AnalysisToolbarActionBtnType } from 'app/analysis/components';

@Component({
    selector: 'btw-query-new',
    templateUrl: './query-new.component.html',
    styleUrls: ['./query-new.component.scss']
})
export class QueryNewComponent extends AnalysisNewComponent implements OnInit {

    query: any;

    get analysis(): IAnalysis {
        return this.buildAnalysis();
    }

    constructor(
        protected router: Router,
        protected analysisStore: Store<AnalysisState>,
        protected tNeelStore: Store<TwitterNeelState>) {
        super(router, analysisStore);
    }

    ngOnInit(): void {
        super.ngOnInit();
        this.tNeelStore.dispatch(new ClearTwitterNeelResults());
    }

    onToolbarActionBtnClick(btn: AnalysisToolbarActionBtnType) {
        if (btn === AnalysisToolbarActionBtnType.Create) {
            this.createAnalysis();
        }
    }

    buildAnalysis(): IAnalysis {
        if (!this.query) {
            return null;
        }

        return {
            type: AnalysisType.TwitterNeel,
            input: {
                type: AnalysisInputType.Query,
                ...this.query
            }
        };
    }

}
