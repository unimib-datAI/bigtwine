import { Moment } from 'moment';

export const enum AnalysisType {
    TWITTER_NEEL = 'TWITTER_NEEL'
}

export const enum AnalysisInputType {
    QUERY = 'QUERY',
    GEO_AREA = 'GEO_AREA',
    DATASET = 'DATASET'
}

export const enum AnalysisStatus {
    READY = 'READY',
    STARTED = 'STARTED',
    STOPPED = 'STOPPED',
    COMPLETED = 'COMPLETED',
    CANCELLED = 'CANCELLED',
    FAILED = 'FAILED'
}

export const enum AnalysisVisibility {
    PRIVATE = 'PRIVATE',
    PUBLIC = 'PUBLIC'
}

export interface IAnalysis {
    id?: string;
    type?: AnalysisType;
    inputType?: AnalysisInputType;
    status?: AnalysisStatus;
    visibility?: AnalysisVisibility;
    owner?: string;
    progress?: number;
    createDate?: Moment;
    updateDate?: Moment;
    input?: string;
    settings?: string;
    resultsCount?: number;
}

export class Analysis implements IAnalysis {
    constructor(
        public id?: string,
        public type?: AnalysisType,
        public inputType?: AnalysisInputType,
        public status?: AnalysisStatus,
        public visibility?: AnalysisVisibility,
        public owner?: string,
        public progress?: number,
        public createDate?: Moment,
        public updateDate?: Moment,
        public input?: string,
        public settings?: string,
        public resultsCount?: number
    ) {}
}
