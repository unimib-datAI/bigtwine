import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { ApigatewayAnalysisModule as AnalysisAnalysisModule } from './analysis/analysis/analysis.module';
import { ApigatewayAnalysisSettingModule as AnalysisAnalysisSettingModule } from './analysis/analysis-setting/analysis-setting.module';
import { ApigatewayAnalysisDefaultSettingModule as AnalysisAnalysisDefaultSettingModule } from './analysis/analysis-default-setting/analysis-default-setting.module';
import { ApigatewayAnalysisSettingCollectionModule as AnalysisAnalysisSettingCollectionModule } from './analysis/analysis-setting-collection/analysis-setting-collection.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    // prettier-ignore
    imports: [
        AnalysisAnalysisModule,
        AnalysisAnalysisSettingModule,
        AnalysisAnalysisDefaultSettingModule,
        AnalysisAnalysisSettingCollectionModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ApigatewayEntityModule {}
