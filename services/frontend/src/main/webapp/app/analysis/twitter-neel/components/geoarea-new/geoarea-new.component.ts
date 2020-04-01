import { Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';

import { DropTargetOptions, FileItem, HttpClientUploadService, InputFileOptions, MineTypeEnum } from '@wkoza/ngx-upload';
import { SERVER_API_URL } from 'app/app.constants';
import {
    AnalysisInputType,
    AnalysisState,
    AnalysisType,
    CreateAnalysis,
    IAnalysis,
    IDatasetAnalysisInput,
    IDocument,
    IGeoAreaAnalysisInput,
    selectCurrentAnalysis
} from 'app/analysis';
import { select, Store } from '@ngrx/store';
import { Observable, ReplaySubject } from 'rxjs';
import { Router } from '@angular/router';
import { takeUntil } from 'rxjs/operators';
import { AnalysisNewComponent } from 'app/analysis/components/analysis-new.component';
import { GeoArea } from 'app/analysis/models/geo-area.model';

@Component({
  selector: 'btw-geoarea-new',
  templateUrl: './geoarea-new.component.html',
  styleUrls: ['./geoarea-new.component.scss']
})
export class GeoareaNewComponent extends AnalysisNewComponent {
    geoArea: GeoArea;

    constructor(
        protected router: Router,
        protected analysisStore: Store<AnalysisState>) {
        super(router, analysisStore);
    }

    buildAnalysis() {
        if (!(this.geoArea.boundingBoxes && this.geoArea.boundingBoxes.length)) {
            return null;
        }

        const analysis: IAnalysis = {
            type: AnalysisType.TwitterNeel,
            input: {
                type: AnalysisInputType.GeoArea,
                ...this.geoArea
            } as IGeoAreaAnalysisInput,
        };

       return analysis;
    }

    onEditorSave(geoArea) {
        this.geoArea = geoArea;
        if (geoArea) {
            this.createAnalysis();
        }
    }
}
