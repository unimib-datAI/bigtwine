import { INeelProcessedTweet } from 'app/analysis/twitter-neel/models/neel-processed-tweet.model';
import { CoordinatesModel } from 'app/analysis/twitter-neel/models/coordinates.model';
import {
    ClearTwitterNeelResults,
    initTwitterNeelState,
    StartListenTwitterNeelResults,
    StopListenTwitterNeelResults,
    TwitterNeelReducer,
    TwitterNeelResultsReceived,
    TwitterNeelState
} from 'app/analysis/twitter-neel';
import { LocationSource } from 'app/analysis/twitter-neel/models/location.model';
import { ActionTypes } from 'app/analysis/store';
import { ITwitterNeelAnalysisResult } from 'app/analysis/twitter-neel/models/twitter-neel-analysis-result.model';

describe('TwitterNeelReducer', () => {
    let initialState: TwitterNeelState;
    const tweet1: ITwitterNeelAnalysisResult = {
        id: 'result1',
        analysisId: 'analysis1',
        processDate: new Date(),
        saveDate: new Date(),
        payload: {
            status: {
                id: 'status1',
                text: 'text 1',
                user: {
                    id: 'user1',
                    name: 'User1',
                    screenName: 'user1',
                    location: 'location1',
                    profileImageUrl: 'image1',
                    coordinates: new CoordinatesModel(15, 20)
                },
                coordinates: new CoordinatesModel(5, 10)
            },
            entities: [
                {
                    value: 'nil1',
                    link: null,
                    confidence: 1,
                    category: 'generic',
                    isNil: true,
                    nilCluster: '1',
                    position: {
                        start: 0,
                        end: 1
                    },
                    resource: null
                },
                {
                    value: 'resource1',
                    link: 'http://dbpedia.com/resource1',
                    confidence: 1,
                    category: 'generic',
                    isNil: false,
                    nilCluster: null,
                    position: {
                        start: 0,
                        end: 1
                    },
                    resource: {
                        name: 'resource1',
                        shortDesc: 'desc1',
                        thumb: 'http://dbpedia.com/thumb1',
                        thumbLarge: 'http://dbpedia.com/thumb1Large',
                        url: 'http://dbpedia.com/resource1',
                        coordinates: new CoordinatesModel(25, 30)
                    }
                }
            ]
        }
    };

    const tweet2: ITwitterNeelAnalysisResult = {
        id: 'result2',
        analysisId: 'analysis1',
        processDate: new Date(),
        saveDate: new Date(),
        payload: {
            status: {
                id: 'status2',
                text: 'text 2',
                user: {
                    id: 'user2',
                    name: 'User2',
                    screenName: 'user2',
                    location: 'location2',
                    profileImageUrl: 'image2',
                    coordinates: new CoordinatesModel(15, 20)
                },
                coordinates: new CoordinatesModel(5, 10)
            },
            entities: [
                {
                    value: 'resource2',
                    link: 'http://dbpedia.com/resource2',
                    confidence: 1,
                    category: 'generic',
                    isNil: false,
                    nilCluster: null,
                    position: {
                        start: 2,
                        end: 3
                    },
                    resource: {
                        name: 'resource2',
                        shortDesc: 'desc2',
                        thumb: 'http://dbpedia.com/thumb2',
                        thumbLarge: 'http://dbpedia.com/thumb2Large',
                        url: 'http://dbpedia.com/resource2',
                        coordinates: new CoordinatesModel(25, 30)
                    }
                }
            ]
        }
    };

    beforeEach(() => {
        initialState = initTwitterNeelState();
        initialState.listeningAnalysisId = 'analysis1';
    });

    it('should be unique', () => {
        const typesIds = Object.keys(ActionTypes).map(t => ActionTypes[t]);
        const uniqueTypeIds = new Set(typesIds);

        expect(typesIds.length).toBe(uniqueTypeIds.size);
    });

    it('should start listening analysis', () => {
        const updatedState = TwitterNeelReducer(initialState, new StartListenTwitterNeelResults('analysis2'));

        expect(initialState.listeningAnalysisId).toBe('analysis1');
        expect(updatedState.listeningAnalysisId).toBe('analysis2');
    });

    it('should stop listening analysis', () => {
        const updatedState = TwitterNeelReducer(initialState, new StopListenTwitterNeelResults('analysis1'));

        expect(initialState.listeningAnalysisId).toBe('analysis1');
        expect(updatedState.listeningAnalysisId).toBeNull();
    });

    it('should not change source state', () => {
        const results = [tweet1];

        const updatedState = TwitterNeelReducer(initialState, new TwitterNeelResultsReceived(results));

        expect(initialState.tweets.all.length).toBe(0);
        expect(updatedState.tweets.all.length).toBe(1);
    });

    it('adds single result to the state', () => {
        const results = [tweet1];

        const updatedState = TwitterNeelReducer(initialState, new TwitterNeelResultsReceived(results));

        expect(updatedState.tweets.all.length).toBe(1);
        expect(updatedState.nilEntities.all.length).toBe(2);
        expect(updatedState.locations.bySource[LocationSource.Resource].length).toBe(1);
        expect(updatedState.locations.bySource[LocationSource.Resource][0].coordinates).toBe(tweet1.payload.entities[1].resource.coordinates);
        expect(updatedState.locations.bySource[LocationSource.TwitterUser].length).toBe(1);
        expect(updatedState.locations.bySource[LocationSource.Status].length).toBe(1);
    });

    it('adds multiple results to the state', () => {
        const results = [tweet1, tweet2];

        const updatedState = TwitterNeelReducer(initialState, new TwitterNeelResultsReceived(results));

        expect(updatedState.tweets.all.length).toBe(2);
        expect(updatedState.nilEntities.all.length).toBe(3);
        expect(updatedState.locations.bySource[LocationSource.Resource].length).toBe(2);
        expect(updatedState.locations.bySource[LocationSource.TwitterUser].length).toBe(2);
        expect(updatedState.locations.bySource[LocationSource.Status].length).toBe(2);
    });

    it('should clear the state', () => {
        const results = [tweet1];

        const updatedState = TwitterNeelReducer(initialState, new TwitterNeelResultsReceived(results));

        expect(updatedState.tweets.all.length).toBe(1);

        const clearedState = TwitterNeelReducer(updatedState, new ClearTwitterNeelResults());

        expect(clearedState.tweets.all.length).toBe(0);
    });

});
