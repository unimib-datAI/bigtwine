import { AnalysisViewComponent } from 'app/analysis/components/analysis-view.component';
import { AnalysisType, GetAnalysisResults, IAnalysis, IPaginationInfo, selectResultsPagination } from 'app/analysis';
import { StartListenTwitterNeelResults, StopListenTwitterNeelResults, TwitterNeelState } from 'app/analysis/twitter-neel';
import { take } from 'rxjs/operators';
import { Store } from '@ngrx/store';

export abstract class TwitterNeelAnalysisViewComponent extends AnalysisViewComponent {
    protected tNeelStore: Store<TwitterNeelState>;

    get analysisType(): AnalysisType {
        return AnalysisType.TwitterNeel;
    }

    get paginationInfo(): IPaginationInfo {
        let pagination = null;
        this.tNeelStore
            .select(selectResultsPagination)
            .pipe(take(1))
            .subscribe(p => pagination = p);

        return pagination;
    }

    fetchResultsPage(page: number) {
        const pageSize = this.paginationInfo.pageSize;
        const action = new GetAnalysisResults(this.currentAnalysis.id, {page, pageSize});

        this.analysisStore.dispatch(action);
    }
}
