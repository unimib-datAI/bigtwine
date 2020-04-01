import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';

import { IAnalysisSettingCollection } from 'app/shared/model/analysis/analysis-setting-collection.model';
import { AnalysisSettingCollectionService } from './analysis-setting-collection.service';
import { IAnalysisSetting } from 'app/shared/model/analysis/analysis-setting.model';
import { AnalysisSettingService } from 'app/entities/analysis/analysis-setting';

@Component({
    selector: 'jhi-analysis-setting-collection-update',
    templateUrl: './analysis-setting-collection-update.component.html'
})
export class AnalysisSettingCollectionUpdateComponent implements OnInit {
    analysisSettingCollection: IAnalysisSettingCollection;
    isSaving: boolean;

    analysissettings: IAnalysisSetting[];

    constructor(
        protected jhiAlertService: JhiAlertService,
        protected analysisSettingCollectionService: AnalysisSettingCollectionService,
        protected analysisSettingService: AnalysisSettingService,
        protected activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ analysisSettingCollection }) => {
            this.analysisSettingCollection = analysisSettingCollection;
        });
        this.analysisSettingService.query().subscribe(
            (res: HttpResponse<IAnalysisSetting[]>) => {
                this.analysissettings = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.analysisSettingCollection.id !== undefined) {
            this.subscribeToSaveResponse(this.analysisSettingCollectionService.update(this.analysisSettingCollection));
        } else {
            this.subscribeToSaveResponse(this.analysisSettingCollectionService.create(this.analysisSettingCollection));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IAnalysisSettingCollection>>) {
        result.subscribe(
            (res: HttpResponse<IAnalysisSettingCollection>) => this.onSaveSuccess(),
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

    getSelected(selectedVals: Array<any>, option: any) {
        if (selectedVals) {
            for (let i = 0; i < selectedVals.length; i++) {
                if (option.id === selectedVals[i].id) {
                    return selectedVals[i];
                }
            }
        }
        return option;
    }
}
