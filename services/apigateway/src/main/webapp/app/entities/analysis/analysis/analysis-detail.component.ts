import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAnalysis } from 'app/shared/model/analysis/analysis.model';

@Component({
    selector: 'jhi-analysis-detail',
    templateUrl: './analysis-detail.component.html'
})
export class AnalysisDetailComponent implements OnInit {
    analysis: IAnalysis;

    constructor(protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ analysis }) => {
            this.analysis = analysis;
        });
    }

    previousState() {
        window.history.back();
    }
}
