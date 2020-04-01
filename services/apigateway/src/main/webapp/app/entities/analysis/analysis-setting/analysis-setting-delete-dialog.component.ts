import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IAnalysisSetting } from 'app/shared/model/analysis/analysis-setting.model';
import { AnalysisSettingService } from './analysis-setting.service';

@Component({
    selector: 'jhi-analysis-setting-delete-dialog',
    templateUrl: './analysis-setting-delete-dialog.component.html'
})
export class AnalysisSettingDeleteDialogComponent {
    analysisSetting: IAnalysisSetting;

    constructor(
        protected analysisSettingService: AnalysisSettingService,
        public activeModal: NgbActiveModal,
        protected eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: string) {
        this.analysisSettingService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'analysisSettingListModification',
                content: 'Deleted an analysisSetting'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-analysis-setting-delete-popup',
    template: ''
})
export class AnalysisSettingDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ analysisSetting }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(AnalysisSettingDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.analysisSetting = analysisSetting;
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
