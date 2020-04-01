import { ICoordinates } from 'app/analysis/twitter-neel/models/coordinates.model';

export enum LocationSource {
    TwitterUser = 'twitter-user',
    Resource = 'resource',
    Status = 'status',
}

export interface ILocation {
    coordinates: ICoordinates;
    source: LocationSource;
    ref: string;
    object: any;
}

export class Location implements ILocation {
    constructor(
        public coordinates: ICoordinates,
        public source: LocationSource,
        public ref: string,
        public object: any) {}
}
