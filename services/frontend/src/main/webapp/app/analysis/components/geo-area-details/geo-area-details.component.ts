import { Component, Input, OnInit } from '@angular/core';
import { GeoArea } from 'app/analysis';
import { NgbModal, NgbModalOptions } from '@ng-bootstrap/ng-bootstrap';
import { GeoAreaEditorComponent } from 'app/analysis/components';

@Component({
  selector: 'btw-geo-area-details',
  templateUrl: './geo-area-details.component.html',
  styleUrls: ['./geo-area-details.component.scss']
})
export class GeoAreaDetailsComponent implements OnInit {
    @Input() geoArea: GeoArea;

    constructor(protected modal: NgbModal) { }

    ngOnInit() {
    }

    onShowMoreDetails() {
        this.openModal();
    }

    private openModal() {
        if (!this.modal.hasOpenModals()) {
            const modalOptions: NgbModalOptions = {
                size: 'lg',
                windowClass: 'geo-area-editor-modal'
            };
            const modalRef = this.modal.open(GeoAreaEditorComponent, modalOptions);
            const editor: GeoAreaEditorComponent = modalRef.componentInstance;

            editor.geoArea = this.geoArea;
            editor.readonly = true;
        }
    }
}
