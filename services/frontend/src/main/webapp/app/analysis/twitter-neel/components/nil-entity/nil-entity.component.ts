import { Component, Input } from '@angular/core';
import { ILinkedEntity, INilEntity } from 'app/analysis/twitter-neel';

@Component({
    selector: 'btw-nil-entity',
    templateUrl: './nil-entity.component.html',
    styleUrls: ['./nil-entity.component.scss']
})
export class NilEntityComponent {
    @Input() nilEntity: INilEntity;
    @Input() entity: ILinkedEntity;
    @Input() tweetsCount: number;

    get entityValue(): string {
        return (this.entity) ? this.entity.value :  this.nilEntity.value;
    }

    get nilCluster(): string {
        return (this.entity) ? this.entity.nilCluster :  this.nilEntity.nilCluster;
    }
}
