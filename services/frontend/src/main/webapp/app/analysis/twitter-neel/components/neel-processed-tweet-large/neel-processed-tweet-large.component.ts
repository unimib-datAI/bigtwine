import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ILinkedEntity, INeelProcessedTweet } from 'app/analysis/twitter-neel/models/neel-processed-tweet.model';

@Component({
    selector: 'btw-neel-processed-tweet-large',
    templateUrl: './neel-processed-tweet-large.component.html',
    styleUrls: ['./neel-processed-tweet-large.component.scss'],
})
export class NeelProcessedTweetLargeComponent {

    @Input() tweet: INeelProcessedTweet;
    @Input() selectedEntity?: ILinkedEntity;
    @Output() selectedEntityChange = new EventEmitter<ILinkedEntity>();

    get hasLinkedEntities(): boolean {
        return this.tweet && this.tweet.entities.some(e => !e.isNil);
    }

    get linkedEntities(): ILinkedEntity[] {
        return this.tweet.entities.filter(e => !e.isNil && e.resource);
    }

    constructor() { }

    onEntityClick(entity: ILinkedEntity) {
        if (this.selectedEntity === entity) {
            this.selectedEntity = null;
        } else {
            this.selectedEntity = entity;
        }

        this.selectedEntityChange.emit(this.selectedEntity);
    }

    isSelected(entity: ILinkedEntity): boolean {
        return this.selectedEntity === entity;
    }
}
