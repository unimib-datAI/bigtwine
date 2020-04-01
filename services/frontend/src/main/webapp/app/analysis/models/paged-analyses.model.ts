import { IPagedResponse } from 'app/analysis/models/paged-response.model';
import { IAnalysis } from 'app/analysis';

export interface IPagedAnalyses extends IPagedResponse {
    objects: IAnalysis[];
}
