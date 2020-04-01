import { IAnalysisSetting } from 'app/shared/model/analysis/analysis-setting.model';
import { AnalysisInputType, AnalysisType } from 'app/shared/model/analysis/analysis.model';

export interface IAnalysisDefaultSetting {
    id?: string;
    defaultValue?: string;
    analysisType?: AnalysisType;
    analysisInputTypes?: AnalysisInputType;
    userRoles?: string;
    userCanOverride?: boolean;
    priority?: number;
    setting?: IAnalysisSetting;
}

export class AnalysisDefaultSetting implements IAnalysisDefaultSetting {
    constructor(
        public id?: string,
        public defaultValue?: string,
        public analysisType?: AnalysisType,
        public analysisInputTypes?: AnalysisInputType,
        public userRoles?: string,
        public userCanOverride?: boolean,
        public priority?: number,
        public setting?: IAnalysisSetting
    ) {
        this.userCanOverride = this.userCanOverride || false;
    }
}
