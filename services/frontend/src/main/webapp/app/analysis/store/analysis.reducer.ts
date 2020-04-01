import { initAnalysisState, AnalysisState } from './analysis.state';
import * as AnalysisActions from './analysis.action';
import { ExportAnalysisResultsSuccess } from './analysis.action';
import { AnalysisStatus, IAnalysis } from 'app/analysis';

export const initialState: AnalysisState = initAnalysisState();

function pushLastError(state: AnalysisState, errAction: AnalysisActions.GenericAnalysisError): AnalysisState {
    const err = {
        type: errAction.type,
        error: errAction.error
    };
    return {
        ...state,
        lastError: err,
        alerts: [
            {
                type: 'danger',
                title:  errAction.type,
                message: errAction.error.message,
                error:  errAction.error
            },
            ...state.alerts
        ]
    };
}

function clearLastError(state: AnalysisState) {
    return {
        ...state,
        lastError: null,
    };
}

function notifyAnalysisUpdates(state: AnalysisState, updatedAnalysis: IAnalysis) {
    let alerts = state.alerts;
    if (state.currentAnalysis) {
        if (state.currentAnalysis.status !== updatedAnalysis.status) {
            alerts = [
                {
                    type: updatedAnalysis.status === AnalysisStatus.Failed ? 'danger' : 'info',
                    title: 'Analysis status updated',
                    message: 'Analysis status changed to ' + updatedAnalysis.status,
                },
                ...alerts
            ];
        }

        if ((!state.currentAnalysis.exports && updatedAnalysis.exports) ||
            (state.currentAnalysis.exports && updatedAnalysis.exports && state.currentAnalysis.exports.length !== updatedAnalysis.exports.length)) {
            alerts = [
                {
                    type: 'info',
                    title: 'Analysis results export started',
                },
                ...alerts
            ];
        }

        if (state.currentAnalysis.exports && updatedAnalysis.exports) {
            const oldCompletedCount = state.currentAnalysis.exports.filter(e => e.completed).length;
            const newCompletedCount = updatedAnalysis.exports.filter(e => e.completed).length;
            const oldFailedCount = state.currentAnalysis.exports.filter(e => e.failed).length;
            const newFailedCount = updatedAnalysis.exports.filter(e => e.failed).length;

            if (oldCompletedCount < newCompletedCount) {
                alerts = [
                    {
                        type: 'success',
                        title: 'Analysis results export completed',
                    },
                    ...alerts
                ];
            } else if (oldFailedCount < newFailedCount) {
                alerts = [
                    {
                        type: 'danger',
                        title: 'Analysis results export failed'
                    },
                    ...alerts
                ];
            }
        }
    }

    return {
        ...state,
        alerts
    };
}

export function AnalysisReducer(state = initialState, action: AnalysisActions.All): AnalysisState {
    switch (action.type) {
        case AnalysisActions.ActionTypes.GetAnalysisSuccess:
            return {
                ...clearLastError(state),
                currentAnalysis: (action as AnalysisActions.GetAnalysisSuccess).analysis
            };
        case AnalysisActions.ActionTypes.GetAnalysesSuccess:
            const act = action as AnalysisActions.GetAnalysesSuccess;
            const analysesById = {...state.analyses.byId, ...act.analyses.reduce((o: any, a) => { o[a.id] = a; return o; }, {})};
            return {
                ...clearLastError(state),
                analyses: {
                    ...state.analyses,
                    all: Object.keys(analysesById).map(k => analysesById[k]),
                    byId: analysesById,
                }
            };
        case AnalysisActions.ActionTypes.CreateAnalysisSuccess:
            const analysis = (action as AnalysisActions.CreateAnalysisSuccess).analysis;
            return {
                ...clearLastError(state),
                analyses: {
                    ...state.analyses,
                    all: [analysis, ...state.analyses.all],
                    byId: {...state.analyses.byId, ...{[analysis.id]: analysis}}
                },
                currentAnalysis: analysis
            };
        case AnalysisActions.ActionTypes.CancelAnalysisSuccess:
            return {
                ...notifyAnalysisUpdates(clearLastError(state), state.currentAnalysis),
            };
        case AnalysisActions.ActionTypes.StartAnalysisSuccess:
        case AnalysisActions.ActionTypes.StopAnalysisSuccess:
        case AnalysisActions.ActionTypes.CompleteAnalysisSuccess:
        case AnalysisActions.ActionTypes.AnalysisChangeReceived:
        case AnalysisActions.ActionTypes.UpdateAnalysisSuccess:
            const updatedAnalysis = (action as AnalysisActions.ActionWithAnalysis).analysis;
            let currentAnalysis = state.currentAnalysis;
            if (currentAnalysis && currentAnalysis.id === updatedAnalysis.id) {
                currentAnalysis = {...currentAnalysis, ...updatedAnalysis};
            }

            return {
                ...notifyAnalysisUpdates(clearLastError(state), currentAnalysis),
                analyses: {
                    ...state.analyses,
                    all: state.analyses.all.map(a => (a.id === updatedAnalysis.id) ? updatedAnalysis : a),
                    byId: {...state.analyses.byId, ...{[updatedAnalysis.id]: updatedAnalysis}},
                },
                currentAnalysis
            };

        case AnalysisActions.ActionTypes.AnalysisResultsReceived:
            return {
                ...clearLastError(state),
                resultsPagination: {
                    ...state.resultsPagination,
                    enabled: false,
                }
            };
        case AnalysisActions.ActionTypes.GetAnalysisResultsSuccess:
            const getResActionSuc = action as AnalysisActions.GetAnalysisResultsSuccess;
            return {
                ...clearLastError(state),
                resultsPagination: {
                    ...state.resultsPagination,
                    enabled: true,
                    currentPage: getResActionSuc.pageDetails.page,
                    pageSize: getResActionSuc.pageDetails.pageSize,
                    allItemsCount: getResActionSuc.pageDetails.totalCount,
                    pagesCount: Math.ceil(getResActionSuc.pageDetails.totalCount / getResActionSuc.pageDetails.pageSize)
                },
                resultsFilters: {
                    ...state.resultsFilters,
                    pagination: {
                        ...state.resultsFilters.pagination,
                        enabled: false,
                    }
                }
            };
        case AnalysisActions.ActionTypes.SearchAnalysisResults:
            const searchAction = action as AnalysisActions.SearchAnalysisResults;
            return {
                ...state,
                resultsFilters: {
                    ...state.resultsFilters,
                    query: searchAction.query
                }
            };
        case AnalysisActions.ActionTypes.SearchAnalysisResultsSuccess:
            const searchActionSuc = action as AnalysisActions.SearchAnalysisResultsSuccess;
            return {
                ...clearLastError(state),
                resultsFilters: {
                    ...state.resultsFilters,
                    pagination: {
                        ...state.resultsFilters.pagination,
                        enabled: true,
                        currentPage: searchActionSuc.pageDetails.page,
                        pageSize: searchActionSuc.pageDetails.pageSize,
                        allItemsCount: searchActionSuc.pageDetails.totalCount,
                        pagesCount: Math.ceil(searchActionSuc.pageDetails.totalCount / searchActionSuc.pageDetails.pageSize)
                    }
                },
                resultsPagination: {
                    ...state.resultsPagination,
                    enabled: false
                }
            };
        case AnalysisActions.ActionTypes.StartListenAnalysisChanges:
            return {
                ...state,
                changesListeningAnalysisId: (action as AnalysisActions.StartListenAnalysisChanges).analysisId
            };
        case AnalysisActions.ActionTypes.StopListenAnalysisChanges:
            return {
                ...state,
                changesListeningAnalysisId: null
            };
        case AnalysisActions.ActionTypes.StartListenAnalysisResults:
            return {
                ...state,
                resultsListeningAnalysisId: (action as AnalysisActions.StartListenAnalysisResults).analysisId
            };
        case AnalysisActions.ActionTypes.StopListenAnalysisResults:
            return {
                ...state,
                resultsListeningAnalysisId: null
            };
        case AnalysisActions.ActionTypes.GetDocumentMetaSuccess:
            const doc = (action as AnalysisActions.GetDocumentMetaSuccess).document;
            return {
                ...clearLastError(state),
                documents: {
                    ...state.documents,
                    byId: {
                        ...state.documents.byId,
                        [doc.id]: doc
                    }
                }
            };
        case AnalysisActions.ActionTypes.ApplyUserSettings:
            const values = (action as AnalysisActions.ApplyUserSettings).values;
            if (values && values['pageSize']) {
                const pageSize = values['pageSize'];
                return {
                    ...state,
                    resultsPagination: {
                        ...state.resultsPagination,
                        pageSize,
                    },
                    resultsFilters: {
                        ...state.resultsFilters,
                        pagination: {
                            ...state.resultsFilters.pagination,
                            pageSize,
                        }
                    }
                };
            } else {
                return state;
            }
        case AnalysisActions.ActionTypes.ExportAnalysisResultsSuccess:
            return {
                ...state,
                currentAnalysis: {
                    ...state.currentAnalysis,
                    exports: [...(state.currentAnalysis.exports || []), (action as AnalysisActions.ExportAnalysisResultsSuccess).analysisExport]
                }
            };
        case AnalysisActions.ActionTypes.ListeningAnalysisChangesError:
            return {
                ...pushLastError(state, (action as AnalysisActions.GenericAnalysisError)),
                changesListeningAnalysisId: null
            };
        case AnalysisActions.ActionTypes.ListeningAnalysisResultsError:
            return {
                ...pushLastError(state, (action as AnalysisActions.GenericAnalysisError)),
                resultsListeningAnalysisId: null
            };
        case AnalysisActions.ActionTypes.ClearAnalysisResults:
            return {
                ...state,
                resultsFilters: {
                    ...state.resultsFilters,
                    pagination: {
                        ...state.resultsFilters.pagination,
                        enabled: false
                    }
                },
                resultsPagination: {
                    ...state.resultsPagination,
                    enabled: false
                }
            };
        case AnalysisActions.ActionTypes.GetAnalysisError:
        case AnalysisActions.ActionTypes.GetAnalysesError:
        case AnalysisActions.ActionTypes.CreateAnalysisError:
        case AnalysisActions.ActionTypes.StartAnalysisError:
        case AnalysisActions.ActionTypes.StopAnalysisError:
        case AnalysisActions.ActionTypes.CompleteAnalysisError:
        case AnalysisActions.ActionTypes.CancelAnalysisError:
        case AnalysisActions.ActionTypes.SearchAnalysisResultsError:
        case AnalysisActions.ActionTypes.GetAnalysisResultsError:
        case AnalysisActions.ActionTypes.GetDocumentMetaError:
        case AnalysisActions.ActionTypes.ExportAnalysisResultsError:
        case AnalysisActions.ActionTypes.GenericAnalysisError:
            return pushLastError(state, (action as AnalysisActions.GenericAnalysisError));
        default:
            return state;
    }
}
