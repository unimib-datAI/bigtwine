import { Routes } from '@angular/router';
import { AnalysisHomeComponent } from 'app/analysis/components/analysis-home/analysis-home.component';
import { AnalysisNotFoundComponent } from 'app/analysis/components';
import { UserRouteAccessService } from 'app/core';

export const analysisState: Routes = [
    {
        path: 'analysis',
        children: [
            {
                path: '',
                pathMatch: 'full',
                component: AnalysisHomeComponent
            },
            {
                path: 'not-found',
                pathMatch: 'full',
                component: AnalysisNotFoundComponent
            },
            {
                path: 'twitter-neel',
                loadChildren: './twitter-neel/twitter-neel.module#TwitterNeelModule'
                // children: twitterNeelState
            }
        ],
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: ''
        },
        canActivate: [UserRouteAccessService]
    },
];
