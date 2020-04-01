import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IAnalysis } from 'app/shared/model/analysis/analysis.model';
import { AnalysisService } from './analysis.service';

@Component({
    selector: 'jhi-analysis-delete-dialog',
    templateUrl: './analysis-delete-dialog.component.html'
})
export class AnalysisDeleteDialogComponent {
    analysis: IAnalysis;

    constructor(protected analysisService: AnalysisService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: string) {
        this.analysisService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'analysisListModification',
                content: 'Deleted an analysis'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-analysis-delete-popup',
    template: ''
})
export class AnalysisDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ analysis }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(AnalysisDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
                this.ngbModalRef.componentInstance.analysis = analysis;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate([{ outlets: { popup: null } }], { replaceUrl: true, queryParamsHandling: 'merge' });
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate([{ outlets: { popup: null } }], { replaceUrl: true, queryParamsHandling: 'merge' });
                        this.ngbModalRef = null;
                    }
                );
            }, 0);
        });
    }

    ngOnDestroy() {
        this.ngbModalRef = null;
    }
}
