import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ApigatewaySharedModule } from 'app/shared';
import {
    AnalysisComponent,
    AnalysisDetailComponent,
    AnalysisUpdateComponent,
    AnalysisDeletePopupComponent,
    AnalysisDeleteDialogComponent,
    analysisRoute,
    analysisPopupRoute
} from './';

const ENTITY_STATES = [...analysisRoute, ...analysisPopupRoute];

@NgModule({
    imports: [ApigatewaySharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        AnalysisComponent,
        AnalysisDetailComponent,
        AnalysisUpdateComponent,
        AnalysisDeleteDialogComponent,
        AnalysisDeletePopupComponent
    ],
    entryComponents: [AnalysisComponent, AnalysisUpdateComponent, AnalysisDeleteDialogComponent, AnalysisDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ApigatewayAnalysisModule {}
