import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { AnalysisSettingCollection } from 'app/shared/model/analysis/analysis-setting-collection.model';
import { AnalysisSettingCollectionService } from './analysis-setting-collection.service';
import { AnalysisSettingCollectionComponent } from './analysis-setting-collection.component';
import { AnalysisSettingCollectionDetailComponent } from './analysis-setting-collection-detail.component';
import { AnalysisSettingCollectionUpdateComponent } from './analysis-setting-collection-update.component';
import { AnalysisSettingCollectionDeletePopupComponent } from './analysis-setting-collection-delete-dialog.component';
import { IAnalysisSettingCollection } from 'app/shared/model/analysis/analysis-setting-collection.model';

@Injectable({ providedIn: 'root' })
export class AnalysisSettingCollectionResolve implements Resolve<IAnalysisSettingCollection> {
    constructor(private service: AnalysisSettingCollectionService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<AnalysisSettingCollection> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<AnalysisSettingCollection>) => response.ok),
                map((analysisSettingCollection: HttpResponse<AnalysisSettingCollection>) => analysisSettingCollection.body)
            );
        }
        return of(new AnalysisSettingCollection());
    }
}

export const analysisSettingCollectionRoute: Routes = [
    {
        path: 'analysis-setting-collection',
        component: AnalysisSettingCollectionComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            defaultSort: 'id,asc',
            pageTitle: 'apigatewayApp.analysisAnalysisSettingCollection.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'analysis-setting-collection/:id/view',
        component: AnalysisSettingCollectionDetailComponent,
        resolve: {
            analysisSettingCollection: AnalysisSettingCollectionResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'apigatewayApp.analysisAnalysisSettingCollection.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'analysis-setting-collection/new',
        component: AnalysisSettingCollectionUpdateComponent,
        resolve: {
            analysisSettingCollection: AnalysisSettingCollectionResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'apigatewayApp.analysisAnalysisSettingCollection.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'analysis-setting-collection/:id/edit',
        component: AnalysisSettingCollectionUpdateComponent,
        resolve: {
            analysisSettingCollection: AnalysisSettingCollectionResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'apigatewayApp.analysisAnalysisSettingCollection.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const analysisSettingCollectionPopupRoute: Routes = [
    {
        path: 'analysis-setting-collection/:id/delete',
        component: AnalysisSettingCollectionDeletePopupComponent,
        resolve: {
            analysisSettingCollection: AnalysisSettingCollectionResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'apigatewayApp.analysisAnalysisSettingCollection.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
