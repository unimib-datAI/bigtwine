import { Component, ElementRef, EventEmitter, Input, NgZone, OnInit, Output, ViewChild } from '@angular/core';
import { GeoArea, IBoundingBox } from 'app/analysis';
import { AgmMap, MapsAPILoader } from '@agm/core';
import { DEFAULT_MAP_BG, DEFAULT_MAP_STYLES } from 'app/shared/gmap-styles';

@Component({
  selector: 'btw-geo-area-editor',
  templateUrl: './geo-area-editor.component.html',
  styleUrls: ['./geo-area-editor.component.scss']
})
export class GeoAreaEditorComponent implements OnInit {
    @ViewChild('map') agmMap: AgmMap;
    @ViewChild('search') searchElementRef: ElementRef;

    @Input() geoArea = new GeoArea('', []);
    @Output() geoAreaChange = new EventEmitter<GeoArea>();

    @Input() minBbox = 1;
    @Input() maxBbox = 25;
    @Input() readonly = false;

    @Output() save = new EventEmitter<GeoArea>();

    get bboxCountValid(): boolean {
        return this.geoArea.boundingBoxes.length >= this.minBbox && this.geoArea.boundingBoxes.length <= this.maxBbox;
    }

    get saveBtnEnabled(): boolean {
        return this.bboxCountValid && this.geoArea.description !== null && this.geoArea.description.length > 1;
    }

    mapStyles = DEFAULT_MAP_STYLES;
    mapBg = DEFAULT_MAP_BG;
    private isDragRectangle = false;

    constructor(
        private mapsAPILoader: MapsAPILoader,
        private ngZone: NgZone) { }

    ngOnInit() {
        // load Places Autocomplete
        if (!this.readonly) {
            this.setupGooglePlaceAutocomplete();
        }
    }

    private setupGooglePlaceAutocomplete() {
        this.mapsAPILoader.load().then(() => {
            const autocomplete = new google.maps.places.Autocomplete(this.searchElementRef.nativeElement, {
                types: ['(regions)']
            });

            autocomplete.addListener('place_changed', () => {
                this.ngZone.run(() => {
                    this.searchElementRef.nativeElement.value = '';

                    // get the place result
                    const place: google.maps.places.PlaceResult = autocomplete.getPlace();

                    // verify result
                    if (!place.geometry || !place.geometry.viewport) {
                        return;
                    }

                    const placeViewport = place.geometry.viewport;
                    const bbox = this.createBbox(
                        placeViewport.getNorthEast().lat(),
                        placeViewport.getNorthEast().lng(),
                        placeViewport.getSouthWest().lat(),
                        placeViewport.getSouthWest().lng(),
                        place.formatted_address
                    );
                    this.geoArea.addBoundingBox(bbox);
                });
            });
        });
    }

    private createBbox(north: number, east: number, south: number, west: number, name = ''): IBoundingBox {
        return  {
            name,
            northEastCoords: {
                latitude: north,
                longitude: east
            },
            southWestCoords: {
                latitude: south,
                longitude: west
            }
        };
    }

    addBbox() {
        if (this.readonly) {
            return;
        }

        const lat = this.agmMap.latitude;
        const lng = this.agmMap.longitude;

        const bbox = this.createBbox(lat + 2, lng + 2, lat - 2, lng - 2);
        this.geoArea.addBoundingBox(bbox);
    }

    removeBbox(i: number) {
        if (this.readonly) {
            return;
        }

        this.geoArea.removeBoundingBox(i);
    }

    updateBbox(i: number, bbox: IBoundingBox) {
        if (this.readonly) {
            return;
        }

        this.geoArea.replaceBoundingBox(i, bbox);
    }

    rectangleBoundsChange(bounds, i) {
        if (!this.isDragRectangle) {
            const bbox = this.createBbox(bounds.north, bounds.east, bounds.south, bounds.west, this.geoArea.boundingBoxes[i].name);
            this.updateBbox(i, bbox);
        }
    }

    rectangleMouseDown(i: number) {
        this.isDragRectangle = true;
    }

    rectangleDragStart(bounds, i: number) {
        this.isDragRectangle = true;
    }

    rectangleDragEnd(bounds, i: number) {
        this.isDragRectangle = false;
        this.rectangleBoundsChange(bounds, i);
    }

    colorForBboxAtIndex(i: number, alpha = 1.0): string {
        const hueStepCount = 9;
        const hueStep = Math.round(360 / hueStepCount);
        const hueIndex = i % hueStepCount;
        const hueOffset = Math.round(360 / this.maxBbox) * Math.floor(i / hueStepCount);
        const hue = hueStep * hueIndex + hueOffset;

        return `hsla(${hue}, 58%, 52%, ${alpha})`;
    }

    onSave() {
        this.save.emit(this.geoArea);
    }
}
