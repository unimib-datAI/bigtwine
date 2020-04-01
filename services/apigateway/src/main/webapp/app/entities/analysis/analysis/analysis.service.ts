import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IAnalysis } from 'app/shared/model/analysis/analysis.model';

type EntityResponseType = HttpResponse<IAnalysis>;
type EntityArrayResponseType = HttpResponse<IAnalysis[]>;

@Injectable({ providedIn: 'root' })
export class AnalysisService {
    public resourceUrl = SERVER_API_URL + 'analysis/api/analyses';

    constructor(protected http: HttpClient) {}

    create(analysis: IAnalysis): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(analysis);
        return this.http
            .post<IAnalysis>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    update(analysis: IAnalysis): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(analysis);
        return this.http
            .put<IAnalysis>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    find(id: string): Observable<EntityResponseType> {
        return this.http
            .get<IAnalysis>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IAnalysis[]>(this.resourceUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    delete(id: string): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    protected convertDateFromClient(analysis: IAnalysis): IAnalysis {
        const copy: IAnalysis = Object.assign({}, analysis, {
            createDate: analysis.createDate != null && analysis.createDate.isValid() ? analysis.createDate.toJSON() : null,
            updateDate: analysis.updateDate != null && analysis.updateDate.isValid() ? analysis.updateDate.toJSON() : null
        });
        return copy;
    }

    protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
        if (res.body) {
            res.body.createDate = res.body.createDate != null ? moment(res.body.createDate) : null;
            res.body.updateDate = res.body.updateDate != null ? moment(res.body.updateDate) : null;
        }
        return res;
    }

    protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        if (res.body) {
            res.body.forEach((analysis: IAnalysis) => {
                analysis.createDate = analysis.createDate != null ? moment(analysis.createDate) : null;
                analysis.updateDate = analysis.updateDate != null ? moment(analysis.updateDate) : null;
            });
        }
        return res;
    }
}
