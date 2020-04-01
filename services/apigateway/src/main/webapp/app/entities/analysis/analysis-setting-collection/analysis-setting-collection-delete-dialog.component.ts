import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IAnalysisSettingCollection } from 'app/shared/model/analysis/analysis-setting-collection.model';
import { AnalysisSettingCollectionService } from './analysis-setting-collection.service';

@Component({
    selector: 'jhi-analysis-setting-collection-delete-dialog',
    templateUrl: './analysis-setting-collection-delete-dialog.component.html'
})
export class AnalysisSettingCollectionDeleteDialogComponent {
    analysisSettingCollection: IAnalysisSettingCollection;

    constructor(
        protected analysisSettingCollectionService: AnalysisSettingCollectionService,
        public activeModal: NgbActiveModal,
        protected eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: string) {
        this.analysisSettingCollectionService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'analysisSettingCollectionListModification',
                content: 'Deleted an analysisSettingCollection'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-analysis-setting-collection-delete-popup',
    template: ''
})
export class AnalysisSettingCollectionDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ analysisSettingCollection }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(AnalysisSettingCollectionDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.analysisSettingCollection = analysisSettingCollection;
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
