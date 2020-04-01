export const enum AnalysisSettingType {
    NUMBER = 'NUMBER',
    STRING = 'STRING',
    TEXT = 'TEXT',
    BOOLEAN = 'BOOLEAN',
    SINGLE_CHOICE = 'SINGLE_CHOICE',
    MULTIPLE_CHOICES = 'MULTIPLE_CHOICES'
}

export const enum AnalysisSettingVisibility {
    GLOBAL = 'GLOBAL',
    HIDDEN = 'HIDDEN',
    USER_VISIBLE = 'USER_VISIBLE'
}

export interface IAnalysisSetting {
    id?: string;
    name?: string;
    label?: string;
    description?: any;
    type?: AnalysisSettingType;
    visibility?: AnalysisSettingVisibility;
    options?: any;
}

export class AnalysisSetting implements IAnalysisSetting {
    constructor(
        public id?: string,
        public name?: string,
        public label?: string,
        public description?: any,
        public type?: AnalysisSettingType,
        public visibility?: AnalysisSettingVisibility,
        public options?: any
    ) {}
}
