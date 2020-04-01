import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { IDocument } from 'app/analysis';
import { AnalysisService } from 'app/analysis/services/analysis.service';

@Component({
    selector: 'btw-document-library',
    templateUrl: './document-library.component.html',
    styleUrls: ['./document-library.component.scss']
})
export class DocumentLibraryComponent implements OnInit {
    documents: IDocument[];
    selectedDocument: IDocument;

    @Input() documentCategory: string;
    @Input() documentType: string;
    @Input() analysisType: string;

    @Output() documentSelected = new EventEmitter<IDocument>();

    constructor(private analysisService: AnalysisService) { }

    ngOnInit() {
        this.analysisService.getDocuments(this.documentType, this.documentCategory, this.analysisType)
            .subscribe((documents: IDocument[]) => {
                this.documents = documents;
            });
    }

    reset() {
        this.selectedDocument = null;
        this.documentSelected.emit(this.selectedDocument);
    }

    onDocumentSelect(doc: IDocument) {
        if (this.selectedDocument === doc) {
            this.selectedDocument = null;
        } else {
            this.selectedDocument = doc;
        }

        this.documentSelected.emit(this.selectedDocument);
    }
}
