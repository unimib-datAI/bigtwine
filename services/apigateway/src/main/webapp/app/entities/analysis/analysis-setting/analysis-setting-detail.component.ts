import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { IAnalysisSetting } from 'app/shared/model/analysis/analysis-setting.model';

@Component({
    selector: 'jhi-analysis-setting-detail',
    templateUrl: './analysis-setting-detail.component.html'
})
export class AnalysisSettingDetailComponent implements OnInit {
    analysisSetting: IAnalysisSetting;

    constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ analysisSetting }) => {
            this.analysisSetting = analysisSetting;
        });
    }

    byteSize(field) {
        return this.dataUtils.byteSize(field);
    }

    openFile(contentType, field) {
        return this.dataUtils.openFile(contentType, field);
    }
    previousState() {
        window.history.back();
    }
}
