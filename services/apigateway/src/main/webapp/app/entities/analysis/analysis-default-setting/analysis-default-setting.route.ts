import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { AnalysisDefaultSetting } from 'app/shared/model/analysis/analysis-default-setting.model';
import { AnalysisDefaultSettingService } from './analysis-default-setting.service';
import { AnalysisDefaultSettingComponent } from './analysis-default-setting.component';
import { AnalysisDefaultSettingDetailComponent } from './analysis-default-setting-detail.component';
import { AnalysisDefaultSettingUpdateComponent } from './analysis-default-setting-update.component';
import { AnalysisDefaultSettingDeletePopupComponent } from './analysis-default-setting-delete-dialog.component';
import { IAnalysisDefaultSetting } from 'app/shared/model/analysis/analysis-default-setting.model';

@Injectable({ providedIn: 'root' })
export class AnalysisDefaultSettingResolve implements Resolve<IAnalysisDefaultSetting> {
    constructor(private service: AnalysisDefaultSettingService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<AnalysisDefaultSetting> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<AnalysisDefaultSetting>) => response.ok),
                map((analysisDefaultSetting: HttpResponse<AnalysisDefaultSetting>) => analysisDefaultSetting.body)
            );
        }
        return of(new AnalysisDefaultSetting());
    }
}

export const analysisDefaultSettingRoute: Routes = [
    {
        path: 'analysis-default-setting',
        component: AnalysisDefaultSettingComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'apigatewayApp.analysisAnalysisDefaultSetting.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'analysis-default-setting/:id/view',
        component: AnalysisDefaultSettingDetailComponent,
        resolve: {
            analysisDefaultSetting: AnalysisDefaultSettingResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'apigatewayApp.analysisAnalysisDefaultSetting.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'analysis-default-setting/new',
        component: AnalysisDefaultSettingUpdateComponent,
        resolve: {
            analysisDefaultSetting: AnalysisDefaultSettingResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'apigatewayApp.analysisAnalysisDefaultSetting.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'analysis-default-setting/:id/edit',
        component: AnalysisDefaultSettingUpdateComponent,
        resolve: {
            analysisDefaultSetting: AnalysisDefaultSettingResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'apigatewayApp.analysisAnalysisDefaultSetting.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const analysisDefaultSettingPopupRoute: Routes = [
    {
        path: 'analysis-default-setting/:id/delete',
        component: AnalysisDefaultSettingDeletePopupComponent,
        resolve: {
            analysisDefaultSetting: AnalysisDefaultSettingResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'apigatewayApp.analysisAnalysisDefaultSetting.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
