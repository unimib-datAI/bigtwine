import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAnalysisDefaultSetting } from 'app/shared/model/analysis/analysis-default-setting.model';

@Component({
    selector: 'jhi-analysis-default-setting-detail',
    templateUrl: './analysis-default-setting-detail.component.html'
})
export class AnalysisDefaultSettingDetailComponent implements OnInit {
    analysisDefaultSetting: IAnalysisDefaultSetting;

    constructor(protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ analysisDefaultSetting }) => {
            this.analysisDefaultSetting = analysisDefaultSetting;
        });
    }

    previousState() {
        window.history.back();
    }
}
