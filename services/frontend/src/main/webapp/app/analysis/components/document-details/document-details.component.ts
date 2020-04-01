import { AfterContentInit, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { AnalysisState, GetDocumentMeta, IDocument, selectDocumentById } from 'app/analysis';
import { takeUntil } from 'rxjs/operators';
import { ReplaySubject } from 'rxjs';

@Component({
    selector: 'btw-document-details',
    templateUrl: './document-details.component.html',
    styleUrls: ['./document-details.component.scss']
})
export class DocumentDetailsComponent implements OnInit, OnDestroy, AfterContentInit {
    protected destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

    @Input() document: IDocument;
    @Input() documentId: string;
    @Input() autoloadDocument = false;

    constructor(private store: Store<AnalysisState>) {}

    ngOnInit(): void {}

    ngOnDestroy(): void {
        this.destroyed$.next(true);
        this.destroyed$.complete();
    }

    ngAfterContentInit(): void {
        if (this.autoloadDocument) {
            this.fetchDocumentMeta();
        }
    }

    fetchDocumentMeta() {
        if (this.documentId != null) {
            this.store
                .select(selectDocumentById(this.documentId))
                .pipe(takeUntil(this.destroyed$))
                .subscribe(doc => {
                    if (!doc) {
                        this.store.dispatch(new GetDocumentMeta(this.documentId));
                    } else {
                        this.document = doc;
                    }
                });
        }
    }
}
