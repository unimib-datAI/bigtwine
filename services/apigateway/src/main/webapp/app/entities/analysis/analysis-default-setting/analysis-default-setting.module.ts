import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ApigatewaySharedModule } from 'app/shared';
import {
    AnalysisDefaultSettingComponent,
    AnalysisDefaultSettingDetailComponent,
    AnalysisDefaultSettingUpdateComponent,
    AnalysisDefaultSettingDeletePopupComponent,
    AnalysisDefaultSettingDeleteDialogComponent,
    analysisDefaultSettingRoute,
    analysisDefaultSettingPopupRoute
} from './';

const ENTITY_STATES = [...analysisDefaultSettingRoute, ...analysisDefaultSettingPopupRoute];

@NgModule({
    imports: [ApigatewaySharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        AnalysisDefaultSettingComponent,
        AnalysisDefaultSettingDetailComponent,
        AnalysisDefaultSettingUpdateComponent,
        AnalysisDefaultSettingDeleteDialogComponent,
        AnalysisDefaultSettingDeletePopupComponent
    ],
    entryComponents: [
        AnalysisDefaultSettingComponent,
        AnalysisDefaultSettingUpdateComponent,
        AnalysisDefaultSettingDeleteDialogComponent,
        AnalysisDefaultSettingDeletePopupComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ApigatewayAnalysisDefaultSettingModule {}
