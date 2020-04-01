import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ApigatewaySharedModule } from 'app/shared';
import {
    AnalysisSettingComponent,
    AnalysisSettingDetailComponent,
    AnalysisSettingUpdateComponent,
    AnalysisSettingDeletePopupComponent,
    AnalysisSettingDeleteDialogComponent,
    analysisSettingRoute,
    analysisSettingPopupRoute
} from './';

const ENTITY_STATES = [...analysisSettingRoute, ...analysisSettingPopupRoute];

@NgModule({
    imports: [ApigatewaySharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        AnalysisSettingComponent,
        AnalysisSettingDetailComponent,
        AnalysisSettingUpdateComponent,
        AnalysisSettingDeleteDialogComponent,
        AnalysisSettingDeletePopupComponent
    ],
    entryComponents: [
        AnalysisSettingComponent,
        AnalysisSettingUpdateComponent,
        AnalysisSettingDeleteDialogComponent,
        AnalysisSettingDeletePopupComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ApigatewayAnalysisSettingModule {}
