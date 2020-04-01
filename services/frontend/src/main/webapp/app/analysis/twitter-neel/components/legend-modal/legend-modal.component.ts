import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'btw-legend-modal',
    templateUrl: './legend-modal.component.html',
    styleUrls: ['./legend-modal.component.scss']
})
export class LegendModalComponent implements OnInit {

    categories = [
        'Location',
        'Event',
        'Product',
        'Organization',
        'Thing',
        'Character',
        'Person',
    ];

    constructor(public activeModal: NgbActiveModal) { }

    ngOnInit() {
    }

}
