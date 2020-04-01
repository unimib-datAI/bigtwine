import './vendor.ts';

import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgbDatepickerConfig } from '@ng-bootstrap/ng-bootstrap';
import { AgmCoreModule } from '@agm/core';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { Ng2Webstorage } from 'ngx-webstorage';
import { NgJhipsterModule } from 'ng-jhipster';

import { AuthInterceptor } from './blocks/interceptor/auth.interceptor';
import { AuthExpiredInterceptor } from './blocks/interceptor/auth-expired.interceptor';
import { ErrorHandlerInterceptor } from './blocks/interceptor/errorhandler.interceptor';
import { NotificationInterceptor } from './blocks/interceptor/notification.interceptor';
import { BigtwineSharedModule } from 'app/shared';
import { BigtwineCoreModule } from 'app/core';
import { BigtwineAppRoutingModule } from './app-routing.module';
import { BigtwineHomeModule } from './home/home.module';
import { BigtwineAccountModule } from './account/account.module';
import { BigtwineAnalysisModule } from './analysis/analysis.module';
import { BigtwineSocialSignInModule } from './social-signin/social-signin.module';
import * as moment from 'moment';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { BtwMainComponent, NavbarComponent, FooterComponent, PageRibbonComponent, ActiveMenuDirective, ErrorComponent } from './layouts';
import { AgmJsMarkerClustererModule } from '@agm/js-marker-clusterer';
import { DEBUG_INFO_ENABLED, GOOGLE_API_KEY } from 'app/app.constants';

@NgModule({
    imports: [
        BrowserModule,
        BigtwineAppRoutingModule,
        Ng2Webstorage.forRoot({ prefix: 'btw', separator: '-' }),
        NgJhipsterModule.forRoot({
            // set below to true to make alerts look like toast
            alertAsToast: false,
            alertTimeout: 5000,
            i18nEnabled: false,
            defaultI18nLang: 'en'
        }),
        StoreModule.forRoot({}),
        EffectsModule.forRoot([]),
        StoreDevtoolsModule.instrument({ maxAge: 25, logOnly: !DEBUG_INFO_ENABLED }),
        AgmCoreModule.forRoot({
            apiKey: GOOGLE_API_KEY,
            libraries: ['places']
        }),
        AgmJsMarkerClustererModule,
        BigtwineSharedModule.forRoot(),
        BigtwineCoreModule,
        BigtwineHomeModule,
        BigtwineAccountModule,
        BigtwineAnalysisModule,
        BigtwineSocialSignInModule,
        NoopAnimationsModule,
        // jhipster-needle-angular-add-module JHipster will add new module here
    ],
    declarations: [BtwMainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, ActiveMenuDirective, FooterComponent],
    providers: [
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthInterceptor,
            multi: true
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthExpiredInterceptor,
            multi: true
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: ErrorHandlerInterceptor,
            multi: true
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: NotificationInterceptor,
            multi: true
        },
    ],
    bootstrap: [BtwMainComponent]
})
export class BigtwineAppModule {
    constructor(private dpConfig: NgbDatepickerConfig) {
        this.dpConfig.minDate = { year: moment().year() - 100, month: 1, day: 1 };
    }
}
