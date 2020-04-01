import { Injectable } from '@angular/core';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { map, withLatestFrom, filter, mergeMap, takeUntil, tap } from 'rxjs/operators';
import * as TwitterNeelActions from './twitter-neel.action';
import * as AnalysisActions from 'app/analysis/store/analysis.action';
import { Action, Store } from '@ngrx/store';
import { selectListeningAnalysisId, TwitterNeelState } from 'app/analysis/twitter-neel';
import { interval, Observable } from 'rxjs';
import { AnalysisState, IAnalysis, selectCurrentAnalysis } from 'app/analysis';
import { ITwitterNeelAnalysisResult } from 'app/analysis/twitter-neel/models/twitter-neel-analysis-result.model';

@Injectable()
export class TwitterNeelEffects {
    @Effect()
    startListenTwitterNeelResults$ = this.action$.pipe(
        ofType(AnalysisActions.ActionTypes.StartListenAnalysisResults),
        map((action: AnalysisActions.StartListenAnalysisResults) => new TwitterNeelActions.StartListenTwitterNeelResults(action.analysisId))
    );

    @Effect()
    stopListenTwitterNeelResults$ = this.action$.pipe(
        ofType(AnalysisActions.ActionTypes.StopListenAnalysisResults),
        map((action: AnalysisActions.StopListenAnalysisResults) => new TwitterNeelActions.StopListenTwitterNeelResults(action.analysisId))
    );

    @Effect()
    twitterNeelResultReceived$ = this.action$.pipe(
        ofType(AnalysisActions.ActionTypes.AnalysisResultsReceived),
        withLatestFrom(this.store$.select(selectListeningAnalysisId)),
        filter(([action, listeningAnalysisId]: [AnalysisActions.AnalysisResultsReceived, string]) =>
            (listeningAnalysisId != null && action.results.length > 0 && action.results[0].analysisId === listeningAnalysisId)),
        map(([action]: [AnalysisActions.AnalysisResultsReceived, string]) => (
            new TwitterNeelActions.TwitterNeelResultsReceived(action.results as ITwitterNeelAnalysisResult[]))),
    );

    @Effect()
    twitterNeelSearchResultsReceived$ = this.action$.pipe(
        ofType(AnalysisActions.ActionTypes.SearchAnalysisResultsSuccess),
        withLatestFrom(this.analysisStore$.select(selectCurrentAnalysis)),
        filter(([action, currentAnalysis]: [AnalysisActions.SearchAnalysisResultsSuccess, IAnalysis]) =>
            (currentAnalysis != null && action.results.length > 0 && action.results[0].analysisId === currentAnalysis.id)),
        map(([action]: [AnalysisActions.SearchAnalysisResultsSuccess, IAnalysis]) => (
            new TwitterNeelActions.TwitterNeelSearchResultsReceived(action.results as ITwitterNeelAnalysisResult[], action.pageDetails))),
    );

    @Effect()
    twitterNeelPagedResultsReceived$ = this.action$.pipe(
        ofType(AnalysisActions.ActionTypes.GetAnalysisResultsSuccess),
        withLatestFrom(this.analysisStore$.select(selectCurrentAnalysis)),
        filter(([action, currentAnalysis]: [AnalysisActions.GetAnalysisResultsSuccess, IAnalysis]) =>
            (currentAnalysis != null && action.results.length > 0 && action.results[0].analysisId === currentAnalysis.id)),
        map(([action]: [AnalysisActions.GetAnalysisResultsSuccess, IAnalysis]) => (
            new TwitterNeelActions.TwitterNeelPagedResultsReceived(action.results as ITwitterNeelAnalysisResult[], action.pageDetails))),
    );

    @Effect()
    sortTwitterNeelResult$: Observable<Action> = this.action$.pipe(
        ofType(TwitterNeelActions.ActionTypes.StartListenTwitterNeelResults),
        mergeMap((startAction: AnalysisActions.StartListenAnalysisResults) => interval(5000)
            .pipe(
                map(() => new TwitterNeelActions.SortTwitterNeelResults()),
                takeUntil(
                    this.action$.pipe(
                        ofType(TwitterNeelActions.ActionTypes.StopListenTwitterNeelResults),
                        filter((stopAction: AnalysisActions.StopListenAnalysisResults) => stopAction.analysisId === null || stopAction.analysisId === startAction.analysisId)
                    )
                )
            ))
    );

    @Effect()
    clearTwitterNeelResults$ = this.action$.pipe(
        ofType(AnalysisActions.ActionTypes.ClearAnalysisResults),
        map(() => new TwitterNeelActions.ClearTwitterNeelResults())
    );

    @Effect()
    sliceTwitterNeelResult$: Observable<Action> = this.action$.pipe(
        ofType(TwitterNeelActions.ActionTypes.StartListenTwitterNeelResults),
        mergeMap((startAction: AnalysisActions.StartListenAnalysisResults) => interval(5000)
            .pipe(
                map(() => new TwitterNeelActions.SliceTwitterNeelResults()),
                takeUntil(
                    this.action$.pipe(
                        ofType(TwitterNeelActions.ActionTypes.StopListenTwitterNeelResults),
                        filter((stopAction: AnalysisActions.StopListenAnalysisResults) => stopAction.analysisId === null || stopAction.analysisId === startAction.analysisId)
                    )
                )
            ))
    );

    constructor(private action$: Actions, private store$: Store<TwitterNeelState>, private analysisStore$: Store<AnalysisState>) {}
}
