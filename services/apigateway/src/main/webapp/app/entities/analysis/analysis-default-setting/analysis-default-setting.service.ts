import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IAnalysisDefaultSetting } from 'app/shared/model/analysis/analysis-default-setting.model';

type EntityResponseType = HttpResponse<IAnalysisDefaultSetting>;
type EntityArrayResponseType = HttpResponse<IAnalysisDefaultSetting[]>;

@Injectable({ providedIn: 'root' })
export class AnalysisDefaultSettingService {
    public resourceUrl = SERVER_API_URL + 'analysis/api/analysis-default-settings';

    constructor(protected http: HttpClient) {}

    create(analysisDefaultSetting: IAnalysisDefaultSetting): Observable<EntityResponseType> {
        return this.http.post<IAnalysisDefaultSetting>(this.resourceUrl, analysisDefaultSetting, { observe: 'response' });
    }

    update(analysisDefaultSetting: IAnalysisDefaultSetting): Observable<EntityResponseType> {
        return this.http.put<IAnalysisDefaultSetting>(this.resourceUrl, analysisDefaultSetting, { observe: 'response' });
    }

    find(id: string): Observable<EntityResponseType> {
        return this.http.get<IAnalysisDefaultSetting>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IAnalysisDefaultSetting[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: string): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }
}
