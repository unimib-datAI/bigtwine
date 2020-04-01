import { Component } from '@angular/core';

import { AnalysisInputType, AnalysisState, AnalysisType, IAnalysis, IDatasetAnalysisInput, IDocument } from 'app/analysis';
import { select, Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { AnalysisNewComponent } from 'app/analysis/components/analysis-new.component';

@Component({
  selector: 'btw-dataset-upload',
  templateUrl: './dataset-upload.component.html',
  styleUrls: ['./dataset-upload.component.scss']
})
export class DatasetUploadComponent extends AnalysisNewComponent {
    document: IDocument;

    constructor(
        protected router: Router,
        protected analysisStore: Store<AnalysisState>) {
        super(router, analysisStore);
    }

    buildAnalysis() {
        if (!this.document.id) {
            return null;
        }

        const analysis: IAnalysis = {
            type: AnalysisType.TwitterNeel,
            input: {
                type: AnalysisInputType.Dataset,
                documentId: this.document.id
            } as IDatasetAnalysisInput,
        };

       return analysis;
    }

    onDocumentSelection(document: IDocument) {
        this.document = document;
        if (this.document) {
            this.createAnalysis();
        }
    }
}
