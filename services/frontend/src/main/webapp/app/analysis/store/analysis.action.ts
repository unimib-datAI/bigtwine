import {Action} from '@ngrx/store';
import { AnalysisType, IAnalysis, IAnalysisExport, IPage, IPageDetails, IResultsFilterQuery } from 'app/analysis';
import { IAnalysisResult } from 'app/analysis/models/analysis-result.model';
import { IDocument } from 'app/analysis/models/document.model';

export enum ActionTypes {
    GetAnalysis = '[Analysis] GET_ANALYSIS',
    GetAnalysisSuccess = '[Analysis] GET_ANALYSIS_SUCCESS',
    GetAnalysisError = '[Analysis] GET_ANALYSIS_ERROR',
    CreateAnalysis = '[Analysis] CREATE_ANALYSIS',
    CreateAnalysisSuccess = '[Analysis] CREATE_ANALYSIS_SUCCESS',
    CreateAnalysisError = '[Analysis] CREATE_ANALYSIS_ERROR',
    GetAnalyses = '[Analysis] GET_ANALYSES',
    GetAnalysesSuccess = '[Analysis] GET_ANALYSES_SUCCESS',
    GetAnalysesError = '[Analysis] GET_ANALYSES_ERROR',
    StopAnalysis = '[Analysis] STOP_ANALYSIS',
    StopAnalysisSuccess = '[Analysis] STOP_ANALYSIS_SUCCESS',
    StopAnalysisError = '[Analysis] STOP_ANALYSIS_ERROR',
    StartAnalysis = '[Analysis] START_ANALYSIS',
    StartAnalysisSuccess = '[Analysis] START_ANALYSIS_SUCCESS',
    StartAnalysisError = '[Analysis] START_ANALYSIS_ERROR',
    CompleteAnalysis = '[Analysis] COMPLETE_ANALYSIS',
    CompleteAnalysisSuccess = '[Analysis] COMPLETE_ANALYSIS_SUCCESS',
    CompleteAnalysisError = '[Analysis] COMPLETE_ANALYSIS_ERROR',
    CancelAnalysis = '[Analysis] CANCEL_ANALYSIS',
    CancelAnalysisSuccess = '[Analysis] CANCEL_ANALYSIS_SUCCESS',
    CancelAnalysisError = '[Analysis] CANCEL_ANALYSIS_ERROR',
    UpdateAnalysis = '[Analysis] UPDATE_ANALYSIS',
    UpdateAnalysisSuccess = '[Analysis] UPDATE_ANALYSIS_SUCCESS',
    UpdateAnalysisError = '[Analysis] UPDATE_ANALYSIS_ERROR',
    StartListenAnalysisChanges = '[Analysis] START_LISTEN_ANALYSIS_CHANGES',
    StopListenAnalysisChanges = '[Analysis] STOP_LISTEN_ANALYSIS_CHANGES',
    AnalysisChangeReceived = '[Analysis] ANALYSIS_CHANGE_RECEIVED',
    ListeningAnalysisChangesError = '[Analysis] LISTENING_ANALYSIS_CHANGES_ERROR',
    StartListenAnalysisResults = '[Analysis] START_LISTEN_ANALYSIS_RESULTS',
    StopListenAnalysisResults = '[Analysis] STOP_LISTEN_ANALYSIS_RESULTS',
    ListeningAnalysisResultsError = '[Analysis] LISTENING_ANALYSIS_RESULTS_ERROR',
    GetAnalysisResults = '[Analysis] GET_ANALYSIS_RESULTS',
    GetAnalysisResultsSuccess = '[Analysis] GET_ANALYSIS_RESULTS_SUCCESS',
    GetAnalysisResultsError = '[Analysis] GET_ANALYSIS_RESULTS_ERROR',
    SearchAnalysisResults = '[Analysis] SEARCH_ANALYSIS_RESULTS',
    SearchAnalysisResultsSuccess = '[Analysis] SEARCH_ANALYSIS_RESULTS_SUCCESS',
    SearchAnalysisResultsError = '[Analysis] SEARCH_ANALYSIS_RESULTS_ERROR',
    AnalysisResultsReceived = '[Analysis] ANALYSIS_RESULTS_RECEIVED',
    ClearAnalysisResults = '[Analysis] CLEAR_ANALYSIS_RESULTS',
    SaveAnalysisSettings = '[Analysis] SAVE_ANALYSIS_SETTINGS',
    GetDocumentMeta = '[Analysis] GET_DOCUMENT_META',
    GetDocumentMetaSuccess = '[Analysis] GET_DOCUMENT_META_SUCCESS',
    GetDocumentMetaError = '[Analysis] GET_DOCUMENT_META_ERROR',
    SaveUserSettings = '[Analysis] SAVE_USER_SETTINGS',
    ApplyUserSettings = '[Analysis] APPLY_USER_SETTINGS',
    RestoreUserSettings = '[Analysis] RESTORE_USER_SETTINGS',
    ExportAnalysisResults = '[Analysis] EXPORT_ANALYSIS_RESULTS',
    ExportAnalysisResultsSuccess = '[Analysis] EXPORT_ANALYSIS_RESULTS_SUCCESS',
    ExportAnalysisResultsError = '[Analysis] EXPORT_ANALYSIS_RESULTS_ERROR',
    GenericAnalysisError = '[Analysis] GENERIC_ANALYSIS_ERROR',
}

export interface ActionWithAnalysis {
    readonly analysis: IAnalysis;
}

export class GenericAnalysisError implements Action {
    constructor(readonly type = ActionTypes.GenericAnalysisError, readonly error: any) {}
}

export class GetAnalysis implements Action {
    readonly type = ActionTypes.GetAnalysis;

    constructor(readonly analysisId: string) {}
}

export class GetAnalysisSuccess implements Action, ActionWithAnalysis {
    readonly type = ActionTypes.GetAnalysisSuccess;

    constructor(readonly analysis: IAnalysis) {}
}

export class GetAnalysisError extends GenericAnalysisError {
    constructor(readonly error: any) {
        super(ActionTypes.GetAnalysisError, error);
    }
}

export class GetAnalyses implements Action {
    readonly type = ActionTypes.GetAnalyses;

    constructor(readonly page: IPage, readonly analysisType?: AnalysisType, readonly owned?: boolean) {}
}

export class GetAnalysesSuccess implements Action {
    readonly type = ActionTypes.GetAnalysesSuccess;

    constructor(readonly analyses: IAnalysis[]) {}
}

export class GetAnalysesError extends GenericAnalysisError {
    constructor(readonly error: any) {
        super(ActionTypes.GetAnalysesError, error);
    }
}

export class CreateAnalysis implements Action, ActionWithAnalysis {
    readonly type = ActionTypes.CreateAnalysis;

    constructor(readonly analysis: IAnalysis) {}
}

export class CreateAnalysisSuccess implements Action, ActionWithAnalysis {
    readonly type = ActionTypes.CreateAnalysisSuccess;

    constructor(readonly analysis: IAnalysis) {}
}

export class CreateAnalysisError extends GenericAnalysisError {
    constructor(readonly error: any) {
        super(ActionTypes.CreateAnalysisError, error);
    }
}

export class StopAnalysis implements Action {
    readonly type = ActionTypes.StopAnalysis;

    constructor(readonly analysisId: string) {}
}

export class StopAnalysisSuccess implements Action, ActionWithAnalysis {
    readonly type = ActionTypes.StopAnalysisSuccess;

    constructor(readonly analysis: IAnalysis) {}
}

export class StopAnalysisError extends GenericAnalysisError {
    constructor(readonly error: any) {
        super(ActionTypes.StopAnalysisError, error);
    }
}

export class StartAnalysis implements Action {
    readonly type = ActionTypes.StartAnalysis;

    constructor(readonly analysisId: string) {}
}

export class StartAnalysisSuccess implements Action, ActionWithAnalysis {
    readonly type = ActionTypes.StartAnalysisSuccess;

    constructor(readonly analysis: IAnalysis) {}
}

export class StartAnalysisError extends GenericAnalysisError {
    constructor(readonly error: any) {
        super(ActionTypes.StartAnalysisError, error);
    }
}

export class CompleteAnalysis implements Action {
    readonly type = ActionTypes.CompleteAnalysis;

    constructor(readonly analysisId: string) {}
}

export class CompleteAnalysisSuccess implements Action, ActionWithAnalysis {
    readonly type = ActionTypes.CompleteAnalysisSuccess;

    constructor(readonly analysis: IAnalysis) {}
}

export class CompleteAnalysisError extends GenericAnalysisError {
    constructor(readonly error: any) {
        super(ActionTypes.CompleteAnalysisError, error);
    }
}

export class CancelAnalysis implements Action {
    readonly type = ActionTypes.CancelAnalysis;

    constructor(readonly analysisId: string) {}
}

export class CancelAnalysisSuccess implements Action {
    readonly type = ActionTypes.CancelAnalysisSuccess;

    constructor() {}
}

export class CancelAnalysisError extends GenericAnalysisError {
    constructor(readonly error: any) {
        super(ActionTypes.CancelAnalysisError, error);
    }
}

export class UpdateAnalysis implements Action {
    readonly type = ActionTypes.UpdateAnalysis;

    constructor(readonly analysisId: string, public changes: IAnalysis) {}
}

export class UpdateAnalysisSuccess implements Action, ActionWithAnalysis {
    readonly type = ActionTypes.UpdateAnalysisSuccess;

    constructor(readonly analysis: IAnalysis) {}
}

export class UpdateAnalysisError extends GenericAnalysisError {
    constructor(readonly error: any) {
        super(ActionTypes.UpdateAnalysisError, error);
    }
}

export class StartListenAnalysisChanges implements Action {
    readonly type = ActionTypes.StartListenAnalysisChanges;

    constructor(readonly analysisId: string) {}
}

export class StopListenAnalysisChanges implements Action {
    readonly type = ActionTypes.StopListenAnalysisChanges;

    /**
     * pass null to stop listen any analysis
     * @param analysisId
     */
    constructor(readonly analysisId?: string) {}
}

export class AnalysisChangeReceived implements Action, ActionWithAnalysis {
    readonly type = ActionTypes.AnalysisChangeReceived;

    constructor(readonly analysis: IAnalysis) {}
}

export class ListeningAnalysisChangesError extends GenericAnalysisError {
    constructor(readonly error: any) {
        super(ActionTypes.ListeningAnalysisChangesError, error);
    }
}

export class StartListenAnalysisResults implements Action {
    readonly type = ActionTypes.StartListenAnalysisResults;

    constructor(readonly analysisId: string) {}
}

export class StopListenAnalysisResults implements Action {
    readonly type = ActionTypes.StopListenAnalysisResults;

    /**
     * pass null to stop listen any analysis
     * @param analysisId
     */
    constructor(readonly analysisId?: string) {}
}

export class GetAnalysisResults implements Action {
    readonly type = ActionTypes.GetAnalysisResults;

    constructor(readonly analysisId: string, readonly page: IPage) {}
}

export class GetAnalysisResultsSuccess implements Action {
    readonly type = ActionTypes.GetAnalysisResultsSuccess;

    constructor(readonly results: IAnalysisResult[], readonly pageDetails: IPageDetails) {}
}

export class GetAnalysisResultsError extends GenericAnalysisError {
    constructor(readonly error: any) {
        super(ActionTypes.GetAnalysisResultsError, error);
    }
}

export class SearchAnalysisResults implements Action {
    readonly type = ActionTypes.SearchAnalysisResults;

    constructor(readonly analysisId: string, readonly query: IResultsFilterQuery, readonly page: IPage) {}
}

export class SearchAnalysisResultsSuccess implements Action {
    readonly type = ActionTypes.SearchAnalysisResultsSuccess;

    constructor(readonly results: IAnalysisResult[], readonly pageDetails: IPageDetails) {}
}

export class SearchAnalysisResultsError extends GenericAnalysisError {
    constructor(readonly error: any) {
        super(ActionTypes.SearchAnalysisResultsError, error);
    }
}

export class AnalysisResultsReceived implements Action {
    readonly type = ActionTypes.AnalysisResultsReceived;

    constructor(readonly results: IAnalysisResult[]) {}
}

export class ClearAnalysisResults implements Action {
    readonly type = ActionTypes.ClearAnalysisResults;

    constructor() {}
}

export class ListeningAnalysisResultsError extends GenericAnalysisError {
    constructor(readonly error: any) {
        super(ActionTypes.ListeningAnalysisResultsError, error);
    }
}

export class SaveAnalysisSettings implements Action {
    readonly type = ActionTypes.SaveAnalysisSettings;

    constructor(readonly analysisId: string, readonly values: {[name: string]: any}) {}
}

export class GetDocumentMeta implements Action {
    readonly type = ActionTypes.GetDocumentMeta;

    constructor(readonly documentId: string) {}
}

export class GetDocumentMetaSuccess implements Action {
    readonly type = ActionTypes.GetDocumentMetaSuccess;

    constructor(readonly document: IDocument) {}
}

export class GetDocumentMetaError extends GenericAnalysisError {
    constructor(readonly error: any) {
        super(ActionTypes.GetDocumentMetaError, error);
    }
}

export class SaveUserSettings implements Action {
    readonly type = ActionTypes.SaveUserSettings;

    constructor(readonly group: string, readonly values: {[name: string]: any}) {}
}

export class ApplyUserSettings implements Action {
    readonly type = ActionTypes.ApplyUserSettings;

    constructor(readonly group: string, readonly values: {[name: string]: any}) {}
}

export class RestoreUserSettings implements Action {
    readonly type = ActionTypes.RestoreUserSettings;

    constructor() {}
}

export class ExportAnalysisResults implements Action {
    readonly type = ActionTypes.ExportAnalysisResults;

    constructor(readonly analysisId: string, readonly format: string) {}
}

export class ExportAnalysisResultsSuccess implements Action {
    readonly type = ActionTypes.ExportAnalysisResultsSuccess;

    constructor(readonly analysisExport: IAnalysisExport) {}
}

export class ExportAnalysisResultsError extends GenericAnalysisError {
    constructor(readonly error: any) {
        super(ActionTypes.ExportAnalysisResultsError, error);
    }
}

export type All = GetAnalysis | GetAnalysisSuccess | GetAnalysisError |
    GetAnalyses | GetAnalysesSuccess | GetAnalysesError |
    CreateAnalysis | CreateAnalysisSuccess | CreateAnalysisError |
    StartAnalysis | StartAnalysisSuccess | StartAnalysisError |
    StopAnalysis | StopAnalysisSuccess | StopAnalysisError |
    CompleteAnalysis | CompleteAnalysisSuccess | CompleteAnalysisError |
    CancelAnalysis | CancelAnalysisSuccess | CancelAnalysisError |
    UpdateAnalysis | UpdateAnalysisSuccess | UpdateAnalysisError |
    GetAnalysisResults | GetAnalysisResultsSuccess | GetAnalysisResultsError |
    SearchAnalysisResults | SearchAnalysisResultsSuccess | SearchAnalysisResultsError | ClearAnalysisResults |
    StartListenAnalysisChanges | StopListenAnalysisChanges | AnalysisChangeReceived | ListeningAnalysisChangesError |
    StartListenAnalysisResults | StopListenAnalysisResults | AnalysisResultsReceived | ListeningAnalysisResultsError |
    GetDocumentMeta | GetDocumentMetaSuccess | GetDocumentMetaError |
    SaveUserSettings | ApplyUserSettings | RestoreUserSettings |
    ExportAnalysisResults | ExportAnalysisResultsSuccess | ExportAnalysisResultsError |
    GenericAnalysisError;
