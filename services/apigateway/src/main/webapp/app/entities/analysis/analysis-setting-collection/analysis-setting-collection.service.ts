import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IAnalysisSettingCollection } from 'app/shared/model/analysis/analysis-setting-collection.model';

type EntityResponseType = HttpResponse<IAnalysisSettingCollection>;
type EntityArrayResponseType = HttpResponse<IAnalysisSettingCollection[]>;

@Injectable({ providedIn: 'root' })
export class AnalysisSettingCollectionService {
    public resourceUrl = SERVER_API_URL + 'analysis/api/analysis-setting-collections';

    constructor(protected http: HttpClient) {}

    create(analysisSettingCollection: IAnalysisSettingCollection): Observable<EntityResponseType> {
        return this.http.post<IAnalysisSettingCollection>(this.resourceUrl, analysisSettingCollection, { observe: 'response' });
    }

    update(analysisSettingCollection: IAnalysisSettingCollection): Observable<EntityResponseType> {
        return this.http.put<IAnalysisSettingCollection>(this.resourceUrl, analysisSettingCollection, { observe: 'response' });
    }

    find(id: string): Observable<EntityResponseType> {
        return this.http.get<IAnalysisSettingCollection>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IAnalysisSettingCollection[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: string): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }
}
