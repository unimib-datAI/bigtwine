import { Component, OnInit } from '@angular/core';
import { LegendModalComponent } from 'app/analysis/twitter-neel/components/legend-modal/legend-modal.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'btw-legend-btn',
    templateUrl: './legend-btn.component.html',
    styleUrls: ['./legend-btn.component.scss']
})
export class LegendBtnComponent implements OnInit {

    constructor(protected modal: NgbModal) { }

    ngOnInit() {
    }

    onOpenLegendBtnClick() {
        if (!this.modal.hasOpenModals()) {
            this.modal.open(LegendModalComponent);
        }
    }

}
