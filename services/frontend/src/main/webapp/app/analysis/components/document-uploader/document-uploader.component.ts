import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { DropTargetOptions, FileItem, HttpClientUploadService, InputFileOptions, MineTypeEnum } from '@wkoza/ngx-upload';
import { IDocument } from 'app/analysis';
import { SERVER_API_URL } from 'app/app.constants';

export const enum UploaderState {
    Waiting = 'waiting',
    Uploading = 'uploading',
    Success = 'success',
    Failed = 'failed'
}

@Component({
  selector: 'btw-document-uploader',
  templateUrl: './document-uploader.component.html',
  styleUrls: ['./document-uploader.component.scss']
})
export class DocumentUploaderComponent implements OnInit {

    _acceptMimeType: string[];
    _state = UploaderState.Waiting;
    _document: IDocument;
    error: any;

    optionsInput: InputFileOptions = {
        multiple: false,
        accept: []
    };

    optionsDrop: DropTargetOptions = {
        color: 'dropZoneColorMaterial',
        colorDrag: 'dropZoneColorDragMaterial',
        colorDrop: 'dropZoneColorDropMaterial',
        multiple: false,
        accept: []
    };

    @ViewChild('ourForm') ourForm;

    @Input() documentCategory: string;
    @Input() documentType: string;
    @Input() analysisType: string;

    @Output() documentUpload = new EventEmitter<IDocument>();
    @Output() stateChange = new EventEmitter<UploaderState>();

    @Input()
    set acceptMimeType(type: string[]) {
        this._acceptMimeType = type;
        this.optionsDrop.accept = this._acceptMimeType as MineTypeEnum[];
        this.optionsInput.accept = this.optionsDrop.accept;
    }

    get acceptMimeType(): string[] {
        return this._acceptMimeType;
    }

    get state(): UploaderState {
        return this._state;
    }

    set state(state: UploaderState) {
        if (state !== this._state) {
            this._state = state;
            this.stateChange.emit(this.state);
        }

        if (state !== UploaderState.Failed) {
            this.error = null;
        }
    }

    get document(): IDocument {
        return this._document;
    }

    set document(doc: IDocument) {
        if (doc !== this.document) {
            this._document = doc;
            this.documentUpload.emit(this.document);
        }
    }

    constructor(public uploader: HttpClientUploadService) {
    }

    ngOnInit() {
        this.uploader.queue = [];

        this.uploader.onAddToQueue$.subscribe((item: FileItem) => {
            console.log('Upload started');
            this.ourForm.reset();
            this.upload(item);
            this.state = UploaderState.Uploading;
        });

        this.uploader.onDropError$.subscribe(err => {
            console.log('error during drop action: ', err);
            if (err.errorAccept) {
                this.error = 'File type not allowed, accepted mime types: ' + this.acceptMimeType.join(', ');
            } else if (err.errorMultiple) {
                this.error = 'Multiple file upload not allowed';
            } else {
                this.error = 'File not accepted';
            }
        });

        this.uploader.onCancel$.subscribe(() => {
            console.log('Upload canceled');
            this.state = UploaderState.Waiting;
        });

        this.uploader.onProgress$.subscribe((data: any) => {
            console.log('upload file in progress: ', data.progress);
        });

        this.uploader.onError$.subscribe(err => {
            console.log('On upload error');
            this.state = UploaderState.Failed;
            this.error = err.body.message;
        });

        this.uploader.onSuccess$.subscribe((data: any) => {
            console.log('On upload completed');
            this.document = data.body;
            this.state = UploaderState.Success;
        });
    }

    upload(item: FileItem) {
        item.upload({
            method: 'POST',
            url: `${SERVER_API_URL}analysis/api/public/documents`
        });
    }

    reset() {
        // this.ourForm.reset();
        this.state = UploaderState.Waiting;
    }

    cancel() {
        this.uploader.cancelAll();
        if (this.ourForm) {
            this.ourForm.reset();
        }
    }
}
