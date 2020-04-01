import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IAnalysisDefaultSetting } from 'app/shared/model/analysis/analysis-default-setting.model';
import { AccountService } from 'app/core';
import { AnalysisDefaultSettingService } from './analysis-default-setting.service';

@Component({
    selector: 'jhi-analysis-default-setting',
    templateUrl: './analysis-default-setting.component.html'
})
export class AnalysisDefaultSettingComponent implements OnInit, OnDestroy {
    analysisDefaultSettings: IAnalysisDefaultSetting[];
    currentAccount: any;
    eventSubscriber: Subscription;

    constructor(
        protected analysisDefaultSettingService: AnalysisDefaultSettingService,
        protected jhiAlertService: JhiAlertService,
        protected eventManager: JhiEventManager,
        protected accountService: AccountService
    ) {}

    loadAll() {
        this.analysisDefaultSettingService.query().subscribe(
            (res: HttpResponse<IAnalysisDefaultSetting[]>) => {
                this.analysisDefaultSettings = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    ngOnInit() {
        this.loadAll();
        this.accountService.identity().then(account => {
            this.currentAccount = account;
        });
        this.registerChangeInAnalysisDefaultSettings();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: IAnalysisDefaultSetting) {
        return item.id;
    }

    registerChangeInAnalysisDefaultSettings() {
        this.eventSubscriber = this.eventManager.subscribe('analysisDefaultSettingListModification', response => this.loadAll());
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
