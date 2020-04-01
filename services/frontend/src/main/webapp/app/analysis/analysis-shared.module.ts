import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TagInputModule } from 'ngx-chips';
import { NgxUploadModule } from '@wkoza/ngx-upload';
import { AgmCoreModule } from '@agm/core';
import { BigtwineSharedModule } from 'app/shared';
import {
    AnalysisInputBadgeComponent,
    AnalysisListComponent,
    AnalysisSettingsComponent,
    AnalysisStatusBadgeComponent,
    AnalysisStatusHistoryComponent,
    AnalysisToolbarComponent,
    ResultsToolbarComponent,
    ResultsExportBtnComponent,
    ChoicesOptionComponent,
    NumberOptionComponent,
    TextOptionComponent,
    BooleanOptionComponent,
    OptionWrapperComponent,
    StepperComponent,
    QueryInputComponent,
    DocumentDetailsComponent,
    DocumentPickerComponent,
    DocumentLibraryComponent,
    DocumentUploaderComponent,
    GeoAreaEditorComponent,
    GeoAreaDetailsComponent,
} from 'app/analysis/components';

@NgModule({
    imports: [
        CommonModule,
        BigtwineSharedModule,
        RouterModule,
        TagInputModule,
        NgxUploadModule.forRoot(),
        AgmCoreModule,
    ],
    declarations: [
        AnalysisToolbarComponent,
        AnalysisStatusHistoryComponent,
        AnalysisSettingsComponent,
        AnalysisListComponent,
        ResultsToolbarComponent,
        ResultsExportBtnComponent,
        AnalysisStatusBadgeComponent,
        AnalysisInputBadgeComponent,
        ChoicesOptionComponent,
        NumberOptionComponent,
        TextOptionComponent,
        BooleanOptionComponent,
        OptionWrapperComponent,
        StepperComponent,
        QueryInputComponent,
        DocumentDetailsComponent,
        DocumentPickerComponent,
        DocumentLibraryComponent,
        DocumentUploaderComponent,
        GeoAreaEditorComponent,
        GeoAreaDetailsComponent,
    ],
    exports: [
        AnalysisToolbarComponent,
        AnalysisStatusHistoryComponent,
        AnalysisSettingsComponent,
        AnalysisListComponent,
        ResultsToolbarComponent,
        AnalysisStatusBadgeComponent,
        AnalysisInputBadgeComponent,
        ChoicesOptionComponent,
        NumberOptionComponent,
        TextOptionComponent,
        BooleanOptionComponent,
        OptionWrapperComponent,
        StepperComponent,
        QueryInputComponent,
        DocumentDetailsComponent,
        DocumentPickerComponent,
        DocumentLibraryComponent,
        DocumentUploaderComponent,
        GeoAreaEditorComponent,
        GeoAreaDetailsComponent,
    ],
    entryComponents: [
        AnalysisStatusHistoryComponent,
        AnalysisSettingsComponent,
        GeoAreaEditorComponent,
    ],
})
export class AnalysisSharedModule {
    static forRoot() {
        return {
            ngModule: AnalysisSharedModule,
            providers: []
        };
    }
}
