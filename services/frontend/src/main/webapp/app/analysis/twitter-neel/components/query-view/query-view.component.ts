import { Component } from '@angular/core';
import { AnalysisState, CreateAnalysis } from 'app/analysis/store';
import { AnalysisToolbarActionBtnType, StreamAnalysisViewComponent } from 'app/analysis/components';
import { AnalysisInputType, AnalysisType, IAnalysis, IQueryAnalysisInput } from 'app/analysis';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { AccountService } from 'app/core';

@Component({
  selector: 'btw-query-view',
  templateUrl: './query-view.component.html',
  styleUrls: ['./query-view.component.scss']
})
export class QueryViewComponent extends StreamAnalysisViewComponent {

    query: any;
    isQueryChanged = false;

    get currentAnalysisQuery(): IQueryAnalysisInput {
        return this.currentAnalysis ? this.currentAnalysis.input as IQueryAnalysisInput : null;
    }

    get showCreateBtn(): boolean {
        if (this.accountService.hasAnyAuthority(['ROLE_DEMO'])) {
            return false;
        }

        return this.isQueryChanged;
    }

    constructor(
        protected router: Router,
        protected route: ActivatedRoute,
        protected analysisStore: Store<AnalysisState>,
        protected accountService: AccountService) {
        super(router, route, analysisStore, accountService);
    }

    onToolbarActionBtnClick(btn: AnalysisToolbarActionBtnType) {
        if (btn === AnalysisToolbarActionBtnType.Create) {
            this.createAnalysis();
        }
    }

    onQueryChange(newQuery) {
        this.query = newQuery;
        this.isQueryChanged = !this.isSameQueryInput(newQuery, this.currentAnalysisQuery);
    }

    onCurrentAnalysisChange(currentAnalysis: IAnalysis, previousAnalysis: IAnalysis) {
        super.onCurrentAnalysisChange(currentAnalysis, previousAnalysis);
        this.isQueryChanged = this.query && !this.isSameQueryInput(this.query, this.currentAnalysisQuery);
    }

    createAnalysis() {
        if (this.query) {
            const analysis: IAnalysis = {
                type: AnalysisType.TwitterNeel,
                input: {
                    type: AnalysisInputType.Query,
                    ...this.query
                }
            };

            this.analysisStore.dispatch(new CreateAnalysis(analysis));
        }
    }

    isSameQueryInput(q1, q2): boolean {
        if (!q1 || !q2) {
            return false;
        }

        if (q1.joinOperator !== q2.joinOperator) {
            return false;
        }

        const tokens1 = new Set(q1.tokens || []);
        const tokens2 = new Set(q2.tokens || []);

        if (tokens1.size !== tokens2.size) {
            return false;
        }

        for (const t of tokens1) {
            if (!tokens2.has(t)) {
                return false;
            }
        }

        return true;
    }
}
