import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IAnalysisDefaultSetting } from 'app/shared/model/analysis/analysis-default-setting.model';
import { AnalysisDefaultSettingService } from './analysis-default-setting.service';

@Component({
    selector: 'jhi-analysis-default-setting-delete-dialog',
    templateUrl: './analysis-default-setting-delete-dialog.component.html'
})
export class AnalysisDefaultSettingDeleteDialogComponent {
    analysisDefaultSetting: IAnalysisDefaultSetting;

    constructor(
        protected analysisDefaultSettingService: AnalysisDefaultSettingService,
        public activeModal: NgbActiveModal,
        protected eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: string) {
        this.analysisDefaultSettingService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'analysisDefaultSettingListModification',
                content: 'Deleted an analysisDefaultSetting'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-analysis-default-setting-delete-popup',
    template: ''
})
export class AnalysisDefaultSettingDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ analysisDefaultSetting }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(AnalysisDefaultSettingDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.analysisDefaultSetting = analysisDefaultSetting;
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
