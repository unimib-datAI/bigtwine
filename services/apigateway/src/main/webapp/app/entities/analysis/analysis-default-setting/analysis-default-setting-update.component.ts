import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';

import { IAnalysisDefaultSetting } from 'app/shared/model/analysis/analysis-default-setting.model';
import { AnalysisDefaultSettingService } from './analysis-default-setting.service';
import { IAnalysisSetting } from 'app/shared/model/analysis/analysis-setting.model';
import { AnalysisSettingService } from 'app/entities/analysis/analysis-setting';
import { UserService } from 'app/core';

@Component({
    selector: 'jhi-analysis-default-setting-update',
    templateUrl: './analysis-default-setting-update.component.html'
})
export class AnalysisDefaultSettingUpdateComponent implements OnInit {
    analysisDefaultSetting: IAnalysisDefaultSetting;
    isSaving: boolean;
    authorities: any[];

    analysissettings: IAnalysisSetting[];

    constructor(
        protected jhiAlertService: JhiAlertService,
        protected analysisDefaultSettingService: AnalysisDefaultSettingService,
        protected analysisSettingService: AnalysisSettingService,
        protected activatedRoute: ActivatedRoute,
        protected userService: UserService
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ analysisDefaultSetting }) => {
            this.analysisDefaultSetting = analysisDefaultSetting;
        });
        this.analysisSettingService.query().subscribe(
            (res: HttpResponse<IAnalysisSetting[]>) => {
                this.analysissettings = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
        this.authorities = [];
        this.userService.authorities().subscribe(authorities => {
            console.log(authorities, 'authorities');
            this.authorities = authorities;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;

        if (!this.analysisDefaultSetting.defaultValue) {
            this.analysisDefaultSetting.defaultValue = null;
        } else {
            try {
                this.analysisDefaultSetting.defaultValue = JSON.parse(this.analysisDefaultSetting.defaultValue);
            } catch (e) {}
        }

        if (this.analysisDefaultSetting.id !== undefined) {
            this.subscribeToSaveResponse(this.analysisDefaultSettingService.update(this.analysisDefaultSetting));
        } else {
            this.subscribeToSaveResponse(this.analysisDefaultSettingService.create(this.analysisDefaultSetting));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IAnalysisDefaultSetting>>) {
        result.subscribe(
            (res: HttpResponse<IAnalysisDefaultSetting>) => this.onSaveSuccess(),
            (res: HttpErrorResponse) => this.onSaveError()
        );
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    trackAnalysisSettingById(index: number, item: IAnalysisSetting) {
        return item.id;
    }
}
