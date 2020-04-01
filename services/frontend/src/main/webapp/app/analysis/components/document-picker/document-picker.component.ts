import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { IDocument } from 'app/analysis';
import { DocumentLibraryComponent, DocumentUploaderComponent, UploaderState } from 'app/analysis/components';
import { NgbTabChangeEvent } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'btw-document-picker',
  templateUrl: './document-picker.component.html',
  styleUrls: ['./document-picker.component.scss']
})
export class DocumentPickerComponent implements OnInit {

    @ViewChild('documentUploader') uploaderView: DocumentUploaderComponent;
    @ViewChild('documentLibrary') libraryView: DocumentLibraryComponent;

    @Input() acceptMimeType: string[];
    @Input() documentCategory: string;
    @Input() documentType: string;
    @Input() analysisType: string;

    @Output() documentChange = new EventEmitter<IDocument>();

    activeTabId = 'upload';
    selectBtnEnabled = false;
    resetBtnEnabled = false;
    cancelBtnEnabled = false;
    cancelBtnVisible = false;
    chosenDocument: IDocument;
    uploadedDocument: IDocument;
    uploaderState: UploaderState;

    get isUploadTab() {
        return this.activeTabId === 'upload';
    }

    get isLibraryTab() {
        return this.activeTabId === 'library';
    }

    constructor() { }

    ngOnInit() {
        this._switchButtons();
    }

    onUploaderStateChange(state: UploaderState) {
        this.uploaderState = state;
        this._switchButtons();
    }

    onTabChange(e: NgbTabChangeEvent) {
        this.activeTabId = e.nextId;
        this._switchButtons();
    }

    onDocumentUploaded(document) {
        this.uploadedDocument = document;
        this._switchButtons();
    }

    onDocumentSelected(document) {
        console.log(document);
        this.chosenDocument = document;
        this._switchButtons();
    }

    onResetBtnClick() {
        if (this.isUploadTab) {
            this.uploadedDocument = null;
            this.uploaderView.reset();
        } else {
            this.chosenDocument = null;
            this.libraryView.reset();
        }

        this.documentChange.emit(null);
    }

    onSelectBtnClick() {
        if (this.isUploadTab) {
            this.documentChange.emit(this.uploadedDocument);
        } else {
            this.documentChange.emit(this.chosenDocument);
        }
    }

    onCancelBtnClick() {
        if (this.isUploadTab) {
            this.uploaderView.cancel();
        }
    }

    private _switchButtons() {
        if (this.isUploadTab) {
            this.cancelBtnVisible = true;
            this.cancelBtnEnabled = this.uploaderState === UploaderState.Uploading;
            this.selectBtnEnabled = !!this.uploadedDocument;
            this.resetBtnEnabled = (this.uploaderState === UploaderState.Failed) || (this.uploaderState === UploaderState.Success);
        } else {
            this.cancelBtnVisible = false;
            this.selectBtnEnabled = !!this.chosenDocument;
            this.resetBtnEnabled = this.selectBtnEnabled;
        }
    }
}
