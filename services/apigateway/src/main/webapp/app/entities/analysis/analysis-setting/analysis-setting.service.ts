import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IAnalysisSetting } from 'app/shared/model/analysis/analysis-setting.model';

type EntityResponseType = HttpResponse<IAnalysisSetting>;
type EntityArrayResponseType = HttpResponse<IAnalysisSetting[]>;

@Injectable({ providedIn: 'root' })
export class AnalysisSettingService {
    public resourceUrl = SERVER_API_URL + 'analysis/api/analysis-settings';

    constructor(protected http: HttpClient) {}

    create(analysisSetting: IAnalysisSetting): Observable<EntityResponseType> {
        return this.http.post<IAnalysisSetting>(this.resourceUrl, analysisSetting, { observe: 'response' });
    }

    update(analysisSetting: IAnalysisSetting): Observable<EntityResponseType> {
        return this.http.put<IAnalysisSetting>(this.resourceUrl, analysisSetting, { observe: 'response' });
    }

    find(id: string): Observable<EntityResponseType> {
        return this.http.get<IAnalysisSetting>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IAnalysisSetting[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: string): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }
}
