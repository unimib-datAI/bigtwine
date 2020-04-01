import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { IQueryAnalysisInput } from 'app/analysis';

@Component({
    selector: 'btw-query-input',
    templateUrl: './query-input.component.html',
    styleUrls: ['./query-input.component.scss']
})
export class QueryInputComponent implements OnInit {
    @Output() queryChange = new EventEmitter<any>();
    @Input() set query(query: IQueryAnalysisInput) {
        if (JSON.stringify(this._query) !== JSON.stringify(query)) {
            this._query = query || '';
            this.parseQuery(query);
        }
    }
    _query = null;
    tokens = [];
    joiner = 'any';

    constructor() {}

    ngOnInit(): void {
    }

    onTagsChanged() {
        this._query = { type: 'query', tokens: this.tokens, joinOperator: this.joiner };
        this.queryChange.emit(this._query);
    }

    parseQuery(query: any) {
        if (!query) {
            this.tokens = [];
            this.joiner = ' ';
        } else {
            this.tokens = query.tokens;
            this.joiner = query.joinOperator;
        }
    }
}
