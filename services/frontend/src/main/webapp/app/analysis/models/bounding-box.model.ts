import { ICoordinates } from 'app/analysis/twitter-neel';

export interface IBoundingBox {
    name: string;
    southWestCoords: ICoordinates;
    northEastCoords: ICoordinates;
}

export class BoundingBox implements IBoundingBox {
    constructor(public name: string,
                public southWestCoords: ICoordinates,
                public northEastCoords: ICoordinates) {
    }
}
