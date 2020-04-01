import { Component, Input, OnInit } from '@angular/core';
import { ICoordinates, ILinkedEntity, INeelProcessedTweet, IResource, SPARQL_NS_PREFIXES_REV } from 'app/analysis/twitter-neel';
import { ALT_MAP_STYLES, ALT_MAP_BG } from 'app/shared/gmap-styles';
import { KeyValue } from '@angular/common';

@Component({
    selector: 'btw-linked-entity-details',
    templateUrl: './linked-entity-details.component.html',
    styleUrls: ['./linked-entity-details.component.scss']
})
export class LinkedEntityDetailsComponent implements OnInit {

    mapStyles = ALT_MAP_STYLES;
    mapBg = ALT_MAP_BG;

    @Input() entity: ILinkedEntity;
    @Input() parentTweet: INeelProcessedTweet;

    get resource(): IResource {
        return this.entity.resource;
    }

    get shouldShowMap(): boolean {
        return this.shouldShowResourceMapMarker || this.shouldShowStatusMapMarker || this.shouldShowTwitterUserMapMarker;
    }

    get shouldShowResourceMapMarker(): boolean {
        return !!(this.resource && this.resource.coordinates);
    }

    get shouldShowStatusMapMarker(): boolean {
        return !!(this.parentTweet &&
            this.parentTweet.status &&
            this.parentTweet.status.coordinates);
    }

    get shouldShowTwitterUserMapMarker(): boolean {
        return !!(this.parentTweet &&
            this.parentTweet.status &&
            this.parentTweet.status.user &&
            this.parentTweet.status.user.coordinates);
    }
    get shouldShowExtraFields(): boolean {
        return !!(this.resource && this.resource.extra);
    }

    get mapCenterCoordinates(): ICoordinates {
        const coordinates: ICoordinates[] = [];

        if (this.shouldShowResourceMapMarker) {
            coordinates.push(this.resource.coordinates);
        }

        if (this.shouldShowStatusMapMarker) {
            coordinates.push(this.parentTweet.status.coordinates);
        }

        if (this.shouldShowTwitterUserMapMarker) {
            coordinates.push(this.parentTweet.status.user.coordinates);
        }

        if (coordinates.length === 0) {
            return {latitude: 0, longitude: 0};
        }

        const lat = coordinates.reduce((sum, coords) => sum + coords.latitude, 0) / coordinates.length;
        const lng = coordinates.reduce((sum, coords) => sum + coords.longitude, 0) / coordinates.length;

        return {latitude: lat, longitude: lng};
    }

    constructor() { }

    ngOnInit() {
    }

    isListExtraField(field: KeyValue<string, any>) {
        return field.value instanceof Array;
    }

    extraFieldValueFormatter(fieldKey: string, value: any) {
        if (fieldKey === 'rdfType') {
            let formattedValue = value;
            for (const prefix in SPARQL_NS_PREFIXES_REV) {
                if (value.startsWith(prefix)) {
                    formattedValue = value.replace(prefix, SPARQL_NS_PREFIXES_REV[prefix] + ':');
                    break;
                }
            }
            return `<a target="_blank" href="${value}">${formattedValue}</a>`;
        } else {
            if (value.test(/^https?:\/\//)) {
                return `<a target="_blank" href="${value}">${value}</a>`;
            }
            return value;
        }
    }
}
