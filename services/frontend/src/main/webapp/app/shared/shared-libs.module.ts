import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgJhipsterModule } from 'ng-jhipster';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { CookieModule } from 'ngx-cookie';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgCircleProgressModule } from 'ng-circle-progress';
import { LazyLoadImageModule } from 'ng-lazyload-image';
import { CIRCLE_PROGRESS_DEFAULTS } from './config/circle-progress-defaults';

@NgModule({
    imports: [
        NgbModule,
        InfiniteScrollModule,
        CookieModule.forRoot(),
        FontAwesomeModule,
        NgCircleProgressModule.forRoot(CIRCLE_PROGRESS_DEFAULTS),
        LazyLoadImageModule,
    ],
    exports: [
        FormsModule,
        ReactiveFormsModule,
        CommonModule,
        NgbModule,
        NgJhipsterModule,
        InfiniteScrollModule,
        FontAwesomeModule,
        NgCircleProgressModule,
        LazyLoadImageModule,
    ]
})
export class BigtwineSharedLibsModule {
    static forRoot() {
        return {
            ngModule: BigtwineSharedLibsModule
        };
    }
}
