import { Component, Input } from '@angular/core';
import { ILinkedEntity, IResource } from 'app/analysis/twitter-neel';

@Component({
    selector: 'btw-linked-entity',
    templateUrl: './linked-entity.component.html',
    styleUrls: ['./linked-entity.component.scss']
})
export class LinkedEntityComponent {
    private _entity: ILinkedEntity;

    @Input() resource: IResource;

    @Input() tweetsCount: number;

    @Input()
    set entity(entity: ILinkedEntity) {
        this._entity = entity;
        this.resource = entity.resource;
    }

    get entity(): ILinkedEntity {
        return this._entity;
    }

    @Input() vertical: boolean;
}
