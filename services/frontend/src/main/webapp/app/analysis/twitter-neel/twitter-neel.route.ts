import { Routes } from '@angular/router';

import {
    QueryNewComponent,
    QueryViewComponent,
    DatasetViewComponent,
    DatasetUploadComponent,
    MapResultsViewerComponent,
    ListResultsViewerComponent,
    TwitterNeelHomeComponent
} from './';
import { GeoareaNewComponent } from 'app/analysis/twitter-neel/components/geoarea-new/geoarea-new.component';
import { GeoareaViewComponent } from 'app/analysis/twitter-neel/components/geoarea-view/geoarea-view.component';

const viewerModes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        component: MapResultsViewerComponent,
        outlet: 'results-viewer'
    },
    {
        path: 'map',
        component: MapResultsViewerComponent,
        outlet: 'results-viewer'
    },
    {
        path: 'list',
        component: ListResultsViewerComponent,
        outlet: 'results-viewer'
    },
];

export const twitterNeelState: Routes = [
    {
        path: '',
        pathMatch: 'full',
        component: TwitterNeelHomeComponent,
        data: {
            pageTitle: 'analysis.twitterNeel.home'
        }
    },
    {
        path: 'query',
        children: [
            {
                path: '',
                pathMatch: 'full',
                redirectTo: 'new'
            },
            {
                path: 'new',
                component: QueryNewComponent,
                data: {
                    pageTitle: 'analysis.twitterNeel.queryNew'
                }
            },
            {
                path: 'view/:analysisId',
                component: QueryViewComponent,
                children: viewerModes,
                data: {
                    pageTitle: 'analysis.twitterNeel.queryView'
                }
            },
        ]
    },
    {
        path: 'dataset',
        children: [
            {
                path: '',
                pathMatch: 'full',
                redirectTo: 'new'
            },
            {
                path: 'new',
                component: DatasetUploadComponent,
                data: {
                    pageTitle: 'analysis.twitterNeel.datasetUpload'
                }
            },
            {
                path: 'view/:analysisId',
                component: DatasetViewComponent,
                children: viewerModes,
                data: {
                    pageTitle: 'analysis.twitterNeel.datasetView'
                }
            },
        ]
    },
    {
        path: 'geo-area',
        children: [
            {
                path: '',
                pathMatch: 'full',
                redirectTo: 'new'
            },
            {
                path: 'new',
                component: GeoareaNewComponent,
                data: {
                    pageTitle: 'analysis.twitterNeel.geoAreaNew'
                }
            },
            {
                path: 'view/:analysisId',
                component: GeoareaViewComponent,
                children: viewerModes,
                data: {
                    pageTitle: 'analysis.twitterNeel.geoAreaView'
                }
            },
        ]
    },
];
