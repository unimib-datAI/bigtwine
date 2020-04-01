import { INeelProcessedTweet, INilEntity, IResource } from '../models/neel-processed-tweet.model';
import { ILocation, LocationSource } from 'app/analysis/twitter-neel/models/location.model';
import { createFeatureSelector, createSelector } from '@ngrx/store';

export const MAX_STREAM_TWEETS_COUNT = 1000;

export interface TwitterNeelState {
    listeningAnalysisId: string;
    tweets: {
        all: INeelProcessedTweet[],
    };
    nilEntities: {
        all: INilEntity[],
        tweetsCount: {[key: string]: number},
    };
    resources: {
        all: IResource[],
        tweetsCount: {[key: string]: number},
        // _flags: {},
    };
    locations: {
        bySource: {[key in LocationSource]: ILocation[]},
        _flags: {},
    };
}

export const initTwitterNeelState: () => TwitterNeelState = () => {
    return {
        listeningAnalysisId: null,
        tweets: {
            all: [],
        },
        nilEntities: {
            all: [],
            tweetsCount: {},
        },
        resources: {
            all: [],
            tweetsCount: {},
        },
        locations: {
            bySource: {
               [LocationSource.Status]: [],
               [LocationSource.TwitterUser]: [],
               [LocationSource.Resource]: [],
            },
            _flags: {},
        },
    };
};

export const selectTwitterNeelFeature = createFeatureSelector<TwitterNeelState>('twitterNeel');

export const selectListeningAnalysisId = createSelector(
    selectTwitterNeelFeature,
    (state: TwitterNeelState)  => state.listeningAnalysisId
);

export const selectAllTweets = createSelector(
    selectTwitterNeelFeature,
    (state: TwitterNeelState) => state.tweets.all,
);

export const selectResourcesTweetsCount = createSelector(
    selectTwitterNeelFeature,
    (state: TwitterNeelState) => state.resources.tweetsCount,
);

export const selectAllResources = createSelector(
    selectTwitterNeelFeature,
    (state: TwitterNeelState) => state.resources.all,
);

export const selectNilEntitiesTweetsCount = createSelector(
    selectTwitterNeelFeature,
    (state: TwitterNeelState) => state.nilEntities.tweetsCount,
);

export const selectNilEntities = createSelector(
    selectTwitterNeelFeature,
    (state: TwitterNeelState) => state.nilEntities.all,
);

export const selectLocationsBySource = (source: LocationSource) => createSelector(
    selectTwitterNeelFeature,
    (state: TwitterNeelState) => state.locations.bySource[source],
);
