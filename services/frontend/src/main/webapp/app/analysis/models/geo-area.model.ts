import { IBoundingBox } from 'app/analysis/models/bounding-box.model';

export class GeoArea {

    constructor(public description: string, public boundingBoxes: IBoundingBox[]) {}

    addBoundingBox(bbox: IBoundingBox) {
        this.boundingBoxes = [...this.boundingBoxes, bbox];
    }

    replaceBoundingBox(index: number, bbox: IBoundingBox) {
        this.boundingBoxes = [
            ...this.boundingBoxes.slice(0, index),
            bbox,
            ...this.boundingBoxes.slice(index + 1, this.boundingBoxes.length),
        ];
    }

    removeBoundingBox(index: number) {
        this.boundingBoxes = [
            ...this.boundingBoxes.slice(0, index),
            ...this.boundingBoxes.slice(index + 1, this.boundingBoxes.length),
        ];
    }

    clearBoundingBoxes() {
        this.boundingBoxes = [];
    }
}
