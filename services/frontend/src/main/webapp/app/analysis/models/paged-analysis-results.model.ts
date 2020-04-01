import { IPagedResponse } from 'app/analysis/models/paged-response.model';
import { IAnalysisResult } from 'app/analysis/models/analysis-result.model';

export interface IPagedAnalysisResults extends IPagedResponse {
    objects: IAnalysisResult[];
}
