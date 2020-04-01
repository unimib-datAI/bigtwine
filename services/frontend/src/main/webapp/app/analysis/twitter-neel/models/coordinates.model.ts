export interface ICoordinates {
    latitude: number;
    longitude: number;
}

export class CoordinatesModel {
    constructor(
        public latitude: number,
        public longitude: number) {}
}
