import { Injectable } from '@angular/core';
import { AnalysisService } from 'app/analysis/services/analysis.service';
import { of, Observable } from 'rxjs';
import { bufferTime, catchError, filter, map, mergeMap, takeUntil, withLatestFrom } from 'rxjs/operators';
import { Action, Store } from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';

import * as AnalysisActions from './analysis.action';
import { LocalStorageService } from 'ngx-webstorage';
import { UserSettingsService } from 'app/analysis/services/user-settings.service';
import { AnalysisState, IAnalysis, selectCurrentAnalysis } from 'app/analysis';
import { AnalysisAlertsService } from 'app/analysis/services/analysis-alerts.service';

@Injectable({providedIn: 'root'})
export class AnalysisEffects {

    @Effect()
    getAnalysis$: Observable<Action> = this.action$.pipe(
        ofType(AnalysisActions.ActionTypes.GetAnalysis),
        mergeMap((action: AnalysisActions.GetAnalysis) => this.analysisService.getAnalysisById(action.analysisId)
            .pipe(
                map(analysis => new AnalysisActions.GetAnalysisSuccess(analysis)),
                catchError(e => of(new AnalysisActions.GetAnalysisError(e)))
            ))
    );

    @Effect()
    createAnalysis$: Observable<Action> = this.action$.pipe(
        ofType(AnalysisActions.ActionTypes.CreateAnalysis),
        mergeMap((action: AnalysisActions.CreateAnalysis) => this.analysisService.createAnalysis(action.analysis)
            .pipe(
                map(analysis => new AnalysisActions.CreateAnalysisSuccess(analysis)),
                catchError(e => of(new AnalysisActions.CreateAnalysisError(e)))
            ))
    );

    @Effect()
    getAnalyses$: Observable<Action> = this.action$.pipe(
        ofType(AnalysisActions.ActionTypes.GetAnalyses),
        mergeMap((action: AnalysisActions.GetAnalyses) => this.analysisService.getAnalyses(action.page, action.analysisType, action.owned)
            .pipe(
                map(response => new AnalysisActions.GetAnalysesSuccess(response.objects)),
                catchError(e => of(new AnalysisActions.GetAnalysesError(e)))
            ))
    );

    @Effect()
    startAnalysis$: Observable<Action> = this.action$.pipe(
        ofType(AnalysisActions.ActionTypes.StartAnalysis),
        mergeMap((action: AnalysisActions.StartAnalysis) => this.analysisService.startAnalysis(action.analysisId)
            .pipe(
                map(analysis => new AnalysisActions.StartAnalysisSuccess(analysis)),
                catchError(e => of(new AnalysisActions.StartAnalysisError(e)))
            ))
    );

    @Effect()
    stopAnalysis$: Observable<Action> = this.action$.pipe(
        ofType(AnalysisActions.ActionTypes.StopAnalysis),
        mergeMap((action: AnalysisActions.StopAnalysis) => this.analysisService.stopAnalysis(action.analysisId)
            .pipe(
                map(analysis => new AnalysisActions.StopAnalysisSuccess(analysis)),
                catchError(e => of(new AnalysisActions.StopAnalysisError(e)))
            ))
    );

    @Effect()
    completeAnalysis$: Observable<Action> = this.action$.pipe(
        ofType(AnalysisActions.ActionTypes.CompleteAnalysis),
        mergeMap((action: AnalysisActions.CompleteAnalysis) => this.analysisService.completeAnalysis(action.analysisId)
            .pipe(
                map(analysis => new AnalysisActions.CompleteAnalysisSuccess(analysis)),
                catchError(e => of(new AnalysisActions.CompleteAnalysisError(e)))
            ))
    );

    @Effect()
    cancelAnalysis$: Observable<Action> = this.action$.pipe(
        ofType(AnalysisActions.ActionTypes.CancelAnalysis),
        mergeMap((action: AnalysisActions.CancelAnalysis) => this.analysisService.cancelAnalysis(action.analysisId)
            .pipe(
                map(() => new AnalysisActions.CancelAnalysisSuccess()),
                catchError(e => of(new AnalysisActions.CancelAnalysisError(e)))
            ))
    );

    @Effect()
    updateAnalysis$: Observable<Action> = this.action$.pipe(
        ofType(AnalysisActions.ActionTypes.UpdateAnalysis),
        mergeMap((action: AnalysisActions.UpdateAnalysis) => this.analysisService.updateAnalysis(action.analysisId, action.changes)
            .pipe(
                map(analysis => new AnalysisActions.UpdateAnalysisSuccess(analysis)),
                catchError(e => of(new AnalysisActions.UpdateAnalysisError(e)))
            ))
    );

    @Effect()
    listenAnalysisChanges$: Observable<Action> = this.action$.pipe(
        ofType(AnalysisActions.ActionTypes.StartListenAnalysisChanges),
        mergeMap((startAction: AnalysisActions.StartListenAnalysisChanges) => this.analysisService.listenAnalysisStatusChanges(startAction.analysisId)
            .pipe(
                map(analysis => new AnalysisActions.AnalysisChangeReceived(analysis)),
                catchError(e => of(new AnalysisActions.ListeningAnalysisChangesError(e))),
                takeUntil(
                    this.action$.pipe(
                        ofType(AnalysisActions.ActionTypes.StopListenAnalysisChanges, AnalysisActions.ActionTypes.StartListenAnalysisChanges),
                        filter((action: Action) => action.type === AnalysisActions.ActionTypes.StartListenAnalysisChanges ||
                            (action as AnalysisActions.StopListenAnalysisChanges).analysisId === null ||
                            (action as AnalysisActions.StopListenAnalysisChanges).analysisId === startAction.analysisId)
                    )
                )
            ))
    );

    @Effect()
    listenAnalysisResults$: Observable<Action> = this.action$.pipe(
        ofType(AnalysisActions.ActionTypes.StartListenAnalysisResults),
        mergeMap((startAction: AnalysisActions.StartListenAnalysisResults) => this.analysisService.listenAnalysisResults(startAction.analysisId)
            .pipe(
                bufferTime(1.0),
                filter(buffer => buffer.length > 0),
                map(results => new AnalysisActions.AnalysisResultsReceived(results)),
                catchError(e => of(new AnalysisActions.ListeningAnalysisResultsError(e))),
                takeUntil(
                    this.action$.pipe(
                        ofType(AnalysisActions.ActionTypes.StopListenAnalysisResults, AnalysisActions.ActionTypes.StartListenAnalysisResults),
                        filter((action: Action) => action.type === AnalysisActions.ActionTypes.StartListenAnalysisResults ||
                            (action as AnalysisActions.StopListenAnalysisResults).analysisId === null ||
                            (action as AnalysisActions.StopListenAnalysisResults).analysisId === startAction.analysisId)
                    )
                )
            ))
    );

    @Effect()
    getAnalysisResults$: Observable<Action> = this.action$.pipe(
        ofType(AnalysisActions.ActionTypes.GetAnalysisResults),
        mergeMap((action: AnalysisActions.GetAnalysisResults) => this.analysisService.getAnalysisResults(action.analysisId, action.page)
            .pipe(
                map(response => new AnalysisActions.GetAnalysisResultsSuccess(response.objects, response)),
                catchError(e => of(new AnalysisActions.GetAnalysisResultsError(e)))
            ))
    );

    @Effect()
    searchAnalysisResults$: Observable<Action> = this.action$.pipe(
        ofType(AnalysisActions.ActionTypes.SearchAnalysisResults),
        mergeMap((action: AnalysisActions.SearchAnalysisResults) => this.analysisService.searchAnalysisResults(action.analysisId, action.query, action.page)
            .pipe(
                map(response => new AnalysisActions.SearchAnalysisResultsSuccess(response.objects, response)),
                catchError(e => of(new AnalysisActions.SearchAnalysisResultsError(e)))
            ))
    );

    @Effect()
    exportAnalysisResults$: Observable<Action> = this.action$.pipe(
        ofType(AnalysisActions.ActionTypes.ExportAnalysisResults),
        mergeMap((action: AnalysisActions.ExportAnalysisResults) => this.analysisService.exportAnalysisResults(action.analysisId, action.format)
            .pipe(
                map(response => new AnalysisActions.ExportAnalysisResultsSuccess(response)),
                catchError(e => of(new AnalysisActions.ExportAnalysisResultsError(e)))
            ))
    );

    @Effect()
    getDocument$: Observable<Action> = this.action$.pipe(
        ofType(AnalysisActions.ActionTypes.GetDocumentMeta),
        mergeMap((action: AnalysisActions.GetDocumentMeta) => this.analysisService.getDocumentById(action.documentId)
            .pipe(
                map(document => new AnalysisActions.GetDocumentMetaSuccess(document)),
                catchError(e => of(new AnalysisActions.GetDocumentMetaError(e)))
            ))
    );

    @Effect()
    saveAnalysisSettings$: Observable<Action> = this.action$.pipe(
        ofType(AnalysisActions.ActionTypes.SaveAnalysisSettings),
        map((action: AnalysisActions.SaveAnalysisSettings) =>
            new AnalysisActions.UpdateAnalysis(action.analysisId, {settings: action.values})),
    );

    @Effect()
    saveGlobalUserSettings$: Observable<Action> = this.action$.pipe(
        ofType(AnalysisActions.ActionTypes.SaveUserSettings),
        filter((action: AnalysisActions.SaveUserSettings) => action.group === this.userSettingsService.GLOBAL_OPTIONS_GROUP_KEY),
        map((action: AnalysisActions.SaveUserSettings) => {
            this.storageService.store('appPrefs', action.values);
            return new AnalysisActions.ApplyUserSettings(action.group, action.values);
        }),
    );

    @Effect()
    restoreGlobalUserSettings$: Observable<Action> = this.action$.pipe(
        ofType(AnalysisActions.ActionTypes.RestoreUserSettings),
        map(() => {
            const prefs = this.storageService.retrieve('appPrefs');
            this.userSettingsService.setGroupValues(this.userSettingsService.GLOBAL_OPTIONS_GROUP_KEY, prefs);
            return new AnalysisActions.ApplyUserSettings(this.userSettingsService.GLOBAL_OPTIONS_GROUP_KEY, prefs);
        }),
    );

    constructor(
        private analysisService: AnalysisService,
        private analysisAlertsService: AnalysisAlertsService,
        private action$: Actions,
        private store$: Store<AnalysisState>,
        private storageService: LocalStorageService,
        private userSettingsService: UserSettingsService) {}
}
