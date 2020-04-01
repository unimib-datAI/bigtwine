import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {
    SocialSignInOauthCallbackComponent,
    socialSignInState
} from './';
import { BigtwineSharedModule } from 'app/shared';

@NgModule({
    imports: [
        BigtwineSharedModule,
        RouterModule.forChild(socialSignInState)
    ],
    declarations: [SocialSignInOauthCallbackComponent],
})
export class BigtwineSocialSignInModule {}
