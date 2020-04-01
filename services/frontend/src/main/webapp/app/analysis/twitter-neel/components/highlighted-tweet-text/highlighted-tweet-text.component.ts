import { Component, Input } from '@angular/core';
import {
    ILinkedEntity,
    INeelProcessedTweet, INilEntity,
} from 'app/analysis/twitter-neel';
import { TweetEntityHighlighterService, IStatusTextPart } from 'app/analysis/twitter-neel/services/tweet-entity-highlighter.service';

@Component({
    selector: 'btw-highlighted-tweet-text',
    templateUrl: './highlighted-tweet-text.component.html',
    styleUrls: ['./highlighted-tweet-text.component.scss'],
})
export class HighlightedTweetTextComponent {
    @Input() tweet: INeelProcessedTweet;

    constructor(private highlighter: TweetEntityHighlighterService) {}

    getTextParts(): IStatusTextPart[] {
        return this.highlighter.getTextParts(this.tweet);
    }

    getCategoryClass(category: string): string {
        if (!category) {
            return '';
        }

        return category.toLowerCase().replace(/[^a-z0-9]+/ig, '');
    }

    getTooltip(entity: ILinkedEntity): string {
        return entity.category + ' | Confidence: ' + (Math.floor(entity.confidence * 10) / 10) + (entity.isNil ? ' | NIL' : '');
    }
}
