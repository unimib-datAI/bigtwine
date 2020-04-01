import {
    initialState,
    AnalysisReducer,
    GetAnalysis,
    CreateAnalysis,
    GetAnalyses,
    StopAnalysis,
    StartAnalysis,
    StartListenAnalysisChanges,
    StopListenAnalysisChanges,
    StartListenAnalysisResults,
    StopListenAnalysisResults,
    ListeningAnalysisChangesError,
    ListeningAnalysisResultsError,
    initAnalysisState,
    GetAnalysisSuccess, CreateAnalysisSuccess, AnalysisChangeReceived
} from 'app/analysis/store';

describe('AnalysisReducer', () => {
    it('should return initial state on send actions', () => {
        const sendActions = [
            new GetAnalysis('analysis1'),
            new CreateAnalysis(null),
            new GetAnalyses({page: 0, pageSize: 250}),
            new StopAnalysis('analysis1'),
            new StartAnalysis('analysis1'),
        ];

        for (const action of sendActions) {
            const state = AnalysisReducer(undefined, action);

            expect(state).toBe(initialState);
        }
    });

    it('should set current analysis on get', () => {
        const action = new GetAnalysisSuccess({id: 'analysis1'});
        const state = AnalysisReducer(undefined, action);

        expect(state.currentAnalysis).not.toBeNull();
        expect(state.currentAnalysis.id).toBe('analysis1');
    });

    it('should set current analysis on create', () => {
        const action = new CreateAnalysisSuccess({id: 'analysis1'});
        const state = AnalysisReducer(undefined, action);

        expect(state.currentAnalysis).not.toBeNull();
        expect(state.currentAnalysis.id).toBe('analysis1');
    });

    it('should update current analysis', () => {
        const action = new AnalysisChangeReceived({id: 'analysis1', status: 'started'});
        const initial = initAnalysisState();
        initial.currentAnalysis = {
            id: 'analysis1',
            status: 'ready',
        };
        const state = AnalysisReducer(initial, action);

        expect(state.currentAnalysis).not.toBeNull();
        expect(state.currentAnalysis.status).toBe('started');
    });

    it('should not update current analysis if id is different', () => {
        const action = new AnalysisChangeReceived({id: 'analysis2', status: 'started'});
        const initial = initAnalysisState();
        initial.currentAnalysis = {
            id: 'analysis1',
            status: 'ready',
        };
        const state = AnalysisReducer(initial, action);

        expect(state.currentAnalysis).not.toBeNull();
        expect(state.currentAnalysis.status).toBe('ready');
    });

    it('should set listening analysis changes on start', () => {
        const action = new StartListenAnalysisResults('analysis1');
        const state = AnalysisReducer(undefined, action);

        expect(state.resultsListeningAnalysisId).toBe('analysis1');
    });

    it('should clear listening analysis changes on stop', () => {
        const action = new StopListenAnalysisResults('analysis1');
        const initial = initAnalysisState();
        initial.resultsListeningAnalysisId = 'analysis1';
        const state = AnalysisReducer(initial, action);

        expect(state.resultsListeningAnalysisId).toBeNull();
    });

    it('should clear listening analysis changes on error', () => {
        const action = new ListeningAnalysisResultsError('analysis1');
        const initial = initAnalysisState();
        initial.resultsListeningAnalysisId = 'analysis1';
        const state = AnalysisReducer(undefined, action);

        expect(state.resultsListeningAnalysisId).toBeNull();
    });

    it('should set listening analysis results on start', () => {
        const action = new StartListenAnalysisChanges('analysis1');
        const state = AnalysisReducer(undefined, action);

        expect(state.changesListeningAnalysisId).toBe('analysis1');
    });

    it('should clear listening analysis results on stop', () => {
        const action = new StopListenAnalysisChanges('analysis1');
        const initial = initAnalysisState();
        initial.changesListeningAnalysisId = 'analysis1';
        const state = AnalysisReducer(undefined, action);

        expect(state.changesListeningAnalysisId).toBeNull();
    });

    it('should clear listening analysis changes on error', () => {
        const action = new ListeningAnalysisChangesError('analysis1');
        const initial = initAnalysisState();
        initial.changesListeningAnalysisId = 'analysis1';
        const state = AnalysisReducer(undefined, action);

        expect(state.changesListeningAnalysisId).toBeNull();
    });
});
