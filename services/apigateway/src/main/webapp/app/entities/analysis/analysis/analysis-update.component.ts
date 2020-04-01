import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IAnalysis } from 'app/shared/model/analysis/analysis.model';
import { AnalysisService } from './analysis.service';

@Component({
    selector: 'jhi-analysis-update',
    templateUrl: './analysis-update.component.html'
})
export class AnalysisUpdateComponent implements OnInit {
    analysis: IAnalysis;
    isSaving: boolean;
    createDate: string;
    updateDate: string;

    constructor(protected analysisService: AnalysisService, protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ analysis }) => {
            this.analysis = analysis;
            this.createDate = this.analysis.createDate != null ? this.analysis.createDate.format(DATE_TIME_FORMAT) : null;
            this.updateDate = this.analysis.updateDate != null ? this.analysis.updateDate.format(DATE_TIME_FORMAT) : null;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        this.analysis.createDate = this.createDate != null ? moment(this.createDate, DATE_TIME_FORMAT) : null;
        this.analysis.updateDate = this.updateDate != null ? moment(this.updateDate, DATE_TIME_FORMAT) : null;
        if (this.analysis.id !== undefined) {
            this.subscribeToSaveResponse(this.analysisService.update(this.analysis));
        } else {
            this.subscribeToSaveResponse(this.analysisService.create(this.analysis));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IAnalysis>>) {
        result.subscribe((res: HttpResponse<IAnalysis>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }
}
