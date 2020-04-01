import { ICoordinates } from 'app/analysis/twitter-neel/models/coordinates.model';

export interface ITextRange {
    start: number;
    end: number;
}

export interface IResource {
    name: string;
    shortDesc: string;
    thumb: string;
    thumbLarge: string;
    url: string;
    coordinates: ICoordinates;
    extra?: {[name: string]: any};
}

export interface ITwitterUser {
    id: string;
    name: string;
    screenName: string;
    location: string;
    profileImageUrl: string;
    coordinates: ICoordinates;
}

export interface ITwitterStatus {
    id: string;
    text: string;
    user: ITwitterUser;
    coordinates: ICoordinates;
}

export interface ILinkedEntity {
    value: string;
    position: ITextRange;
    link: string;
    confidence: number;
    category: string;
    isNil: boolean;
    nilCluster: string;
    resource: IResource;
}

export interface INilEntity {
    value: string;
    nilCluster: string;
}

export interface INeelProcessedTweet {
    status: ITwitterStatus;
    entities: ILinkedEntity[];
}
