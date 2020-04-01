import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ApigatewaySharedModule } from 'app/shared';
import {
    AnalysisSettingCollectionComponent,
    AnalysisSettingCollectionDetailComponent,
    AnalysisSettingCollectionUpdateComponent,
    AnalysisSettingCollectionDeletePopupComponent,
    AnalysisSettingCollectionDeleteDialogComponent,
    analysisSettingCollectionRoute,
    analysisSettingCollectionPopupRoute
} from './';

const ENTITY_STATES = [...analysisSettingCollectionRoute, ...analysisSettingCollectionPopupRoute];

@NgModule({
    imports: [ApigatewaySharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        AnalysisSettingCollectionComponent,
        AnalysisSettingCollectionDetailComponent,
        AnalysisSettingCollectionUpdateComponent,
        AnalysisSettingCollectionDeleteDialogComponent,
        AnalysisSettingCollectionDeletePopupComponent
    ],
    entryComponents: [
        AnalysisSettingCollectionComponent,
        AnalysisSettingCollectionUpdateComponent,
        AnalysisSettingCollectionDeleteDialogComponent,
        AnalysisSettingCollectionDeletePopupComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ApigatewayAnalysisSettingCollectionModule {}
