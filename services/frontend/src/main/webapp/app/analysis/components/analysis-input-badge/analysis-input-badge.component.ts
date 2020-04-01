import { Component, Input } from '@angular/core';
import { AnalysisStatus, IAnalysisInput, IDatasetAnalysisInput, IGeoAreaAnalysisInput, IQueryAnalysisInput } from 'app/analysis';

@Component({
    selector: 'btw-analysis-input-badge',
    templateUrl: './analysis-input-badge.component.html',
    styleUrls: ['./analysis-input-badge.component.scss']
})
export class AnalysisInputBadgeComponent {
    @Input() analysisInput: IAnalysisInput;

    get queryAnalysisInput(): IQueryAnalysisInput {
        return this.analysisInput as IQueryAnalysisInput;
    }

    get datasetAnalysisInput(): IDatasetAnalysisInput {
        return this.analysisInput as IDatasetAnalysisInput;
    }

    get geoAreaAnalysisInput(): IGeoAreaAnalysisInput {
        return this.analysisInput as IGeoAreaAnalysisInput;
    }
}
