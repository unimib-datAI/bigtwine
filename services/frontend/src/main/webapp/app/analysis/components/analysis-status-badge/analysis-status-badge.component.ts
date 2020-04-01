import { Component, Input } from '@angular/core';
import { AnalysisStatus } from 'app/analysis';

@Component({
    selector: 'btw-analysis-status-badge',
    templateUrl: './analysis-status-badge.component.html',
    styleUrls: ['./analysis-status-badge.component.scss']
})
export class AnalysisStatusBadgeComponent {
    @Input() analysisStatus: AnalysisStatus | string;

    get labelClasses() {
        const classes = ['badge'];
        const status = this.analysisStatus;

        if (status === AnalysisStatus.Ready || status === AnalysisStatus.Stopped) {
            classes.push('badge-secondary');
        } else if (status === AnalysisStatus.Started) {
            classes.push('badge-primary');
        } else if (status === AnalysisStatus.Completed) {
            classes.push('badge-success');
        } else if (status === AnalysisStatus.Cancelled) {
            classes.push('badge-warning');
        } else if (status === AnalysisStatus.Failed) {
            classes.push('badge-danger');
        }

        return classes;
    }
}
