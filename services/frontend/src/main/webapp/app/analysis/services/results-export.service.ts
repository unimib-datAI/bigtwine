import { InjectionToken } from '@angular/core';
import { IAnalysisResultsExportFormat } from 'app/analysis/models/analysis-results-export-format.model';
import { IAnalysis } from 'app/analysis';

export interface IResultsExportService {

    getSupportedExportFormats(analysis: IAnalysis): IAnalysisResultsExportFormat[];
    getMissingExportFormats(analysis: IAnalysis): IAnalysisResultsExportFormat[];
    getExportFormatLabel(analysis: IAnalysis, formatType: string): string;
}

export const RESULTS_EXPORT_SERVICE = new InjectionToken<IResultsExportService>('results-export.service');
