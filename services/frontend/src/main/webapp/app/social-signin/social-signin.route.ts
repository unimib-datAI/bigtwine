import {Routes} from '@angular/router';
import {SocialSignInOauthCallbackComponent} from 'app/social-signin/social-signin-oauth-callback.component';

export const socialSignInState: Routes = [
    {
        path: 'social-signin',
        children: [
            {
                path: 'oauth-callback/:providerId',
                component: SocialSignInOauthCallbackComponent,
            }
        ]
    }
];
