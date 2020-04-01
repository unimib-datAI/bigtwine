import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAnalysisSettingCollection } from 'app/shared/model/analysis/analysis-setting-collection.model';

@Component({
    selector: 'jhi-analysis-setting-collection-detail',
    templateUrl: './analysis-setting-collection-detail.component.html'
})
export class AnalysisSettingCollectionDetailComponent implements OnInit {
    analysisSettingCollection: IAnalysisSettingCollection;

    constructor(protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ analysisSettingCollection }) => {
            this.analysisSettingCollection = analysisSettingCollection;
        });
    }

    previousState() {
        window.history.back();
    }
}
