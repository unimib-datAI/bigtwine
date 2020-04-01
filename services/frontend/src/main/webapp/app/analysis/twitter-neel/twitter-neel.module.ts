import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BigtwineSharedModule } from 'app/shared';
import { RouterModule } from '@angular/router';
import { AgmCoreModule } from '@agm/core';
import { AgmJsMarkerClustererModule } from '@agm/js-marker-clusterer';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';

import {
    twitterNeelState,
    TwitterNeelReducer,
    TwitterNeelEffects,
    DatasetUploadComponent,
    DatasetViewComponent,
    ListResultsViewerComponent,
    MapResultsViewerComponent,
    QueryNewComponent,
    QueryViewComponent,
    NeelProcessedTweetComponent,
    HighlightedTweetTextComponent,
    NeelProcessedTweetLargeComponent,
    LinkedEntityComponent,
    NilEntityComponent,
    LegendBtnComponent,
    TweetsCounterIconComponent,
    EntityCategoryIconComponent,
    ConfidenceIconComponent,
    LinkedEntityDetailsComponent,
    LegendModalComponent,
    GeoareaNewComponent,
    GeoareaViewComponent,
    TwitterNeelHomeComponent,
    ViewModeSwitcherComponent,
} from './';
import { TweetEntityHighlighterService } from 'app/analysis/twitter-neel/services/tweet-entity-highlighter.service';
import { ResultsFilterService } from 'app/analysis/twitter-neel/services/results-filter.service';
import { RESULTS_FILTER_SERVICE } from 'app/analysis/services/results-filter.service';
import { RESULTS_EXPORT_SERVICE } from 'app/analysis/services/results-export.service';
import { AnalysisSharedModule } from 'app/analysis/analysis-shared.module';
import { ResultsExportService } from 'app/analysis/twitter-neel/services/results-export.service';

@NgModule({
    imports: [
        CommonModule,
        BigtwineSharedModule,
        AnalysisSharedModule.forRoot(),
        RouterModule.forChild(twitterNeelState),
        StoreModule.forFeature('twitterNeel', TwitterNeelReducer),
        EffectsModule.forFeature([TwitterNeelEffects]),
        AgmCoreModule,
        AgmJsMarkerClustererModule
    ],
    declarations: [
        TwitterNeelHomeComponent,
        DatasetUploadComponent,
        DatasetViewComponent,
        ListResultsViewerComponent,
        MapResultsViewerComponent,
        QueryNewComponent,
        QueryViewComponent,
        NeelProcessedTweetComponent,
        NeelProcessedTweetLargeComponent,
        HighlightedTweetTextComponent,
        ViewModeSwitcherComponent,
        LinkedEntityComponent,
        NilEntityComponent,
        TweetsCounterIconComponent,
        EntityCategoryIconComponent,
        ConfidenceIconComponent,
        LinkedEntityDetailsComponent,
        GeoareaNewComponent,
        GeoareaViewComponent,
        LegendModalComponent,
        LegendBtnComponent,
    ],
    entryComponents: [
        LegendModalComponent
    ],
    providers: [
        {
            provide: TweetEntityHighlighterService,
            useClass: TweetEntityHighlighterService,
        },
        {
            provide: RESULTS_FILTER_SERVICE,
            useClass: ResultsFilterService,
        },
        {
            provide: RESULTS_EXPORT_SERVICE,
            useClass: ResultsExportService,
        },
    ]
})
export class TwitterNeelModule {
}
