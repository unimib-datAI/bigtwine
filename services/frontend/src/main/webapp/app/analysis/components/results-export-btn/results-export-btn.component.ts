import { Component, Inject, Input, OnInit } from '@angular/core';
import { AnalysisState, ExportAnalysisResults, IAnalysis, IAnalysisExport, IAnalysisResultsExportFormat } from 'app/analysis';
import { Action, Store } from '@ngrx/store';
import { AnalysisService } from 'app/analysis/services/analysis.service';
import { IResultsExportService, RESULTS_EXPORT_SERVICE } from 'app/analysis/services/results-export.service';

@Component({
    selector: 'btw-results-export-btn',
    templateUrl: './results-export-btn.component.html',
    styleUrls: ['./results-export-btn.component.scss']
})
export class ResultsExportBtnComponent implements OnInit {

    @Input() analysis: IAnalysis;

    constructor(
        private analysisStore: Store<AnalysisState>,
        private analysisService: AnalysisService,
        @Inject(RESULTS_EXPORT_SERVICE) private exportService: IResultsExportService
    ) { }

    ngOnInit() {
    }

    onExportBtnClick(formatType: string) {
        const action: Action = new ExportAnalysisResults(this.analysis.id, formatType);
        this.analysisStore.dispatch(action);
    }

    getExportFormats(): IAnalysisResultsExportFormat[] {
        return this.exportService.getMissingExportFormats(this.analysis);
    }

    getFormatLabel(formatType: string) {
        return this.exportService.getExportFormatLabel(this.analysis, formatType);
    }

    getExportLink(exp: IAnalysisExport): string {
        return this.analysisService
            .getDocumentDownloadLink(exp.documentId);
    }

    isExportRunning(exp: IAnalysisExport): boolean {
        return !exp.failed && !exp.completed;
    }
}
