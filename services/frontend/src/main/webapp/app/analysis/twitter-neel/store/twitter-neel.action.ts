import { Action } from '@ngrx/store';

import { INeelProcessedTweet } from '../models/neel-processed-tweet.model';
import { ITwitterNeelAnalysisResult } from 'app/analysis/twitter-neel/models/twitter-neel-analysis-result.model';
import { IPageDetails } from 'app/analysis';

export enum ActionTypes {
    StartListenTwitterNeelResults = '[TwitterNeel] StartListenTwitterNeelResults',
    StopListenTwitterNeelResults = '[TwitterNeel] StopListenTwitterNeelResults',
    TwitterNeelResultsReceived = '[TwitterNeel] TwitterNeelResultsReceived',
    TwitterNeelSearchResultsReceived = '[TwitterNeel] TwitterNeelSearchResultsReceived',
    TwitterNeelPagedResultsReceived = '[TwitterNeel] TwitterNeelPagedResultsReceived',
    ClearTwitterNeelResults = '[TwitterNeel] ClearTwitterNeelResults',
    SortTwitterNeelResults = '[TwitterNeel] SortTwitterNeelResults',
    SliceTwitterNeelResults = '[TwitterNeel] SliceTwitterNeelResults',
}

export class StartListenTwitterNeelResults implements Action {
    readonly type = ActionTypes.StartListenTwitterNeelResults;

    constructor(public analysisId: string) {}
}

export class StopListenTwitterNeelResults implements Action {
    readonly type = ActionTypes.StopListenTwitterNeelResults;

    constructor(public analysisId: string) {}
}

export class TwitterNeelResultsReceived implements Action {
    readonly type = ActionTypes.TwitterNeelResultsReceived;

    constructor(public results: ITwitterNeelAnalysisResult[]) {}
}

export class TwitterNeelSearchResultsReceived implements Action {
    readonly type = ActionTypes.TwitterNeelSearchResultsReceived;

    constructor(public results: ITwitterNeelAnalysisResult[], public pageDetails: IPageDetails) {}
}

export class TwitterNeelPagedResultsReceived implements Action {
    readonly type = ActionTypes.TwitterNeelPagedResultsReceived;

    constructor(public results: ITwitterNeelAnalysisResult[], public pageDetails: IPageDetails) {}
}

export class ClearTwitterNeelResults implements Action {
    readonly type = ActionTypes.ClearTwitterNeelResults;

    constructor() {}
}

export class SortTwitterNeelResults implements Action {
    readonly type = ActionTypes.SortTwitterNeelResults;

    constructor() {}
}

export class SliceTwitterNeelResults implements Action {
    readonly type = ActionTypes.SliceTwitterNeelResults;

    constructor() {}
}

export type All = StartListenTwitterNeelResults | StopListenTwitterNeelResults |
    TwitterNeelResultsReceived | TwitterNeelPagedResultsReceived | TwitterNeelSearchResultsReceived |
    ClearTwitterNeelResults | SortTwitterNeelResults | SliceTwitterNeelResults;
