export interface IResultsFilterOption {
    value: any;
    label: string;
}

export interface IResultsFilterType {
    type: string;
    label: string;
    options?: IResultsFilterOption[];
}
