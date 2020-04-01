import { IAnalysisSetting } from 'app/shared/model/analysis/analysis-setting.model';
import { AnalysisInputType, AnalysisType } from 'app/shared/model/analysis/analysis.model';

export interface IAnalysisSettingCollection {
    id?: string;
    analysisType?: AnalysisType;
    analysisInputType?: AnalysisInputType;
    settings?: IAnalysisSetting[];
}

export class AnalysisSettingCollection implements IAnalysisSettingCollection {
    constructor(
        public id?: string,
        public analysisType?: AnalysisType,
        public analysisInputType?: AnalysisInputType,
        public settings?: IAnalysisSetting[]
    ) {}
}
