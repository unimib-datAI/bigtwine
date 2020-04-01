import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiAlertService, JhiDataUtils } from 'ng-jhipster';

import { IAnalysisSetting } from 'app/shared/model/analysis/analysis-setting.model';
import { AccountService } from 'app/core';
import { AnalysisSettingService } from './analysis-setting.service';

@Component({
    selector: 'jhi-analysis-setting',
    templateUrl: './analysis-setting.component.html'
})
export class AnalysisSettingComponent implements OnInit, OnDestroy {
    analysisSettings: IAnalysisSetting[];
    currentAccount: any;
    eventSubscriber: Subscription;

    constructor(
        protected analysisSettingService: AnalysisSettingService,
        protected jhiAlertService: JhiAlertService,
        protected dataUtils: JhiDataUtils,
        protected eventManager: JhiEventManager,
        protected accountService: AccountService
    ) {}

    loadAll() {
        this.analysisSettingService.query().subscribe(
            (res: HttpResponse<IAnalysisSetting[]>) => {
                this.analysisSettings = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    ngOnInit() {
        this.loadAll();
        this.accountService.identity().then(account => {
            this.currentAccount = account;
        });
        this.registerChangeInAnalysisSettings();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: IAnalysisSetting) {
        return item.id;
    }

    byteSize(field) {
        return this.dataUtils.byteSize(field);
    }

    openFile(contentType, field) {
        return this.dataUtils.openFile(contentType, field);
    }

    registerChangeInAnalysisSettings() {
        this.eventSubscriber = this.eventManager.subscribe('analysisSettingListModification', response => this.loadAll());
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
