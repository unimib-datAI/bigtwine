import { Injectable } from '@angular/core';
import { ILinkedEntity, INeelProcessedTweet } from 'app/analysis/twitter-neel';

export interface IStatusTextPart {
    type: string;
    content: string;
    entity?: ILinkedEntity;
}

@Injectable()
export class TweetEntityHighlighterService {
    private _cache: {[key: string]: IStatusTextPart[]} = {};

    private isInCache(tweet: INeelProcessedTweet) {
        return typeof this._cache[tweet.status.id] !== 'undefined';
    }

    private getFromCache(tweet: INeelProcessedTweet) {
        return this._cache[tweet.status.id];
    }

    private addToCache(tweet: INeelProcessedTweet, parts: IStatusTextPart[]) {
        this._cache[tweet.status.id] = parts;
    }

    invalidateCache() {
        this._cache = {};
    }

    private sortEntities(entities: ILinkedEntity[]) {
        if (!entities || entities.length <= 1) {
            return entities;
        } else {
            return entities.sort((e1, e2) => {
                return (e1.position.start - e2.position.start);
            });
        }
    }

    getTextParts(tweet: INeelProcessedTweet, useCache = true): IStatusTextPart[] {
        if (!tweet.status || !tweet.status.text) {
            return [];
        }

        if (useCache && this.isInCache(tweet)) {
            return this.getFromCache(tweet);
        }

        const sorted_entities = this.sortEntities(tweet.entities);
        const text = tweet.status.text;
        const parts: IStatusTextPart[] = [];

        for (let i = 0; i < sorted_entities.length; ++i) {
            const entity = sorted_entities[i];
            const ps = i === 0 ? 0 : sorted_entities[i - 1].position.end;
            const pe = entity.position.start;
            const prefix = text.substring(ps, pe);

            if (prefix.length > 0) {
                parts.push({
                    type: 'text',
                    content: prefix,
                });
            }

            const entityText = text.substring(entity.position.start, entity.position.end);

            if (entityText.length > 0) {
                parts.push({
                    type: 'entity',
                    content: entityText,
                    entity,
                });
            }
        }

        const postfix_start = (sorted_entities.length > 0) ? sorted_entities[sorted_entities.length - 1].position.end : 0;
        const postfix_end = text.length;
        const postfix = text.substring(postfix_start, postfix_end);

        if (postfix.length > 0) {
            parts.push({
                type: 'text',
                content: postfix,
            });
        }

        this.addToCache(tweet, parts);

        return parts;
    }
}
