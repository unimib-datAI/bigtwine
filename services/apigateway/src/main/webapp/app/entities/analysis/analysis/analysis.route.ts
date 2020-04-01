import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Analysis } from 'app/shared/model/analysis/analysis.model';
import { AnalysisService } from './analysis.service';
import { AnalysisComponent } from './analysis.component';
import { AnalysisDetailComponent } from './analysis-detail.component';
import { AnalysisUpdateComponent } from './analysis-update.component';
import { AnalysisDeletePopupComponent } from './analysis-delete-dialog.component';
import { IAnalysis } from 'app/shared/model/analysis/analysis.model';

@Injectable({ providedIn: 'root' })
export class AnalysisResolve implements Resolve<IAnalysis> {
    constructor(private service: AnalysisService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Analysis> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<Analysis>) => response.ok),
                map((analysis: HttpResponse<Analysis>) => analysis.body)
            );
        }
        return of(new Analysis());
    }
}

export const analysisRoute: Routes = [
    {
        path: 'analysis',
        component: AnalysisComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            defaultSort: 'id,asc',
            pageTitle: 'apigatewayApp.analysisAnalysis.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'analysis/:id/view',
        component: AnalysisDetailComponent,
        resolve: {
            analysis: AnalysisResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'apigatewayApp.analysisAnalysis.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'analysis/new',
        component: AnalysisUpdateComponent,
        resolve: {
            analysis: AnalysisResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'apigatewayApp.analysisAnalysis.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'analysis/:id/edit',
        component: AnalysisUpdateComponent,
        resolve: {
            analysis: AnalysisResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'apigatewayApp.analysisAnalysis.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const analysisPopupRoute: Routes = [
    {
        path: 'analysis/:id/delete',
        component: AnalysisDeletePopupComponent,
        resolve: {
            analysis: AnalysisResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'apigatewayApp.analysisAnalysis.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
