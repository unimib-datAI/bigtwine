import { IResultsExportService } from 'app/analysis/services/results-export.service';
import { IAnalysis, IAnalysisResultsExportFormat } from 'app/analysis';
import { Injectable } from '@angular/core';

@Injectable()
export class ResultsExportService implements  IResultsExportService {
    getSupportedExportFormats(analysis: IAnalysis): IAnalysisResultsExportFormat[] {
        return [
            {type: 'json', label: 'JSON'},
            {type: 'tsv', label: 'TSV'},
            {type: 'twitter-neel-challenge', label: 'NEEL Challenge'},
            {type: 'twitter-neel-dataset', label: 'Input dataset'}
        ];
    }

    getMissingExportFormats(analysis: IAnalysis): IAnalysisResultsExportFormat[] {
        const formats = (analysis.exports && analysis.exports.length) ? analysis.exports.map(e => e.format) : [];

        return this
            .getSupportedExportFormats(analysis)
            .filter(e => !formats.includes(e.type));
    }

    getExportFormatLabel(analysis: IAnalysis, formatType: string): string {
        const formats = this
            .getSupportedExportFormats(analysis)
            .filter(e => e.type === formatType)
            .map(e => e.label);

        return formats[0] || formatType;
    }
}
