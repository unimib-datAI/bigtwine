export const enum AnalysisSettingType {
    Number = 'number',
    String = 'string',
    Text = 'text',
    Boolean = 'boolean',
    SingleChoice = 'single-choice',
    MultipleChoices = 'multiple-choices'
}

export interface IAnalysisSettingChoice {
    name: string;
    value: any;
}

export interface IAnalysisSetting {
    name: string;
    label: string;
    type: AnalysisSettingType;
    editable: boolean;
    description?: string;
    choices: IAnalysisSettingChoice[];
    defaultValue: any;
    currentValue: any;
}
