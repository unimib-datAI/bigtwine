import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { AnalysisSetting } from 'app/shared/model/analysis/analysis-setting.model';
import { AnalysisSettingService } from './analysis-setting.service';
import { AnalysisSettingComponent } from './analysis-setting.component';
import { AnalysisSettingDetailComponent } from './analysis-setting-detail.component';
import { AnalysisSettingUpdateComponent } from './analysis-setting-update.component';
import { AnalysisSettingDeletePopupComponent } from './analysis-setting-delete-dialog.component';
import { IAnalysisSetting } from 'app/shared/model/analysis/analysis-setting.model';

@Injectable({ providedIn: 'root' })
export class AnalysisSettingResolve implements Resolve<IAnalysisSetting> {
    constructor(private service: AnalysisSettingService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<AnalysisSetting> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<AnalysisSetting>) => response.ok),
                map((analysisSetting: HttpResponse<AnalysisSetting>) => analysisSetting.body)
            );
        }
        return of(new AnalysisSetting());
    }
}

export const analysisSettingRoute: Routes = [
    {
        path: 'analysis-setting',
        component: AnalysisSettingComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'apigatewayApp.analysisAnalysisSetting.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'analysis-setting/:id/view',
        component: AnalysisSettingDetailComponent,
        resolve: {
            analysisSetting: AnalysisSettingResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'apigatewayApp.analysisAnalysisSetting.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'analysis-setting/new',
        component: AnalysisSettingUpdateComponent,
        resolve: {
            analysisSetting: AnalysisSettingResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'apigatewayApp.analysisAnalysisSetting.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'analysis-setting/:id/edit',
        component: AnalysisSettingUpdateComponent,
        resolve: {
            analysisSetting: AnalysisSettingResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'apigatewayApp.analysisAnalysisSetting.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const analysisSettingPopupRoute: Routes = [
    {
        path: 'analysis-setting/:id/delete',
        component: AnalysisSettingDeletePopupComponent,
        resolve: {
            analysisSetting: AnalysisSettingResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'apigatewayApp.analysisAnalysisSetting.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
