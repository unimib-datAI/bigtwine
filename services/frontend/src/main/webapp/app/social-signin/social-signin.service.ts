import {Injectable} from '@angular/core';

import {ISocialSignInProvider, SocialSignInProvider, SocialSignInType} from './model';
import {HttpClient} from '@angular/common/http';
import {SERVER_API_URL} from 'app/app.constants';
import {AuthServerProvider, WindowRef} from 'app/core';
import { ConnectResponse } from 'app/social-signin/model/connect-response.model';
import {AccountService} from 'app/core/auth/account.service';
import { ConnectionResponse } from 'app/social-signin/model/connection-response.model';

export const enum SocialSignInProviderId {
    Twitter = 'twitter'
}

export const SOCIAL_SIGNIN_PROVIDERS: ISocialSignInProvider[] = [
    new SocialSignInProvider(SocialSignInProviderId.Twitter, 'Twitter', SocialSignInType.OAuth10),
];

@Injectable({ providedIn: 'root' })
export class SocialSignInService {

    get signInProviders(): ISocialSignInProvider[] {
        return SOCIAL_SIGNIN_PROVIDERS;
    }

    constructor(
        private authServerProvider: AuthServerProvider,
        private accountService: AccountService,
        private http: HttpClient,
        private $window: WindowRef) {}

    startSignInRoutine(provider: ISocialSignInProvider): Promise<any> {
        if (provider.type === SocialSignInType.OAuth10) {
            return this.startSignInOAuth10Routine(provider);
        }

        throw new Error('Unimplemented provider type: ' + provider.type);
    }

    async startSignInOAuth10Routine(provider: ISocialSignInProvider): Promise<any> {
        const callbackUrl = `${location.protocol}//${location.host}/social-signin/oauth-callback/${provider.id}`;
        const data = {
            callbackUrl
        };
        const response: any = await this.http.post(`${SERVER_API_URL}socials/api/oauth/authorize/${provider.id}`, data).toPromise();

        this.$window.nativeWindow.open(response.authorizedUrl, 'Social Sign In', 'width=640,height=480,scrollbars=no');

        return response;
    }

    async completeSignInOAuth10Routine(providerId: String, oauthToken: String, oauthVerifier: String): Promise<ConnectResponse> {
        const data = {
            allowImplicitSignUp: true,
            requestToken: oauthToken,
            verifier: oauthVerifier
        };

        const isAuthenticated = !!(await this.accountService.identity());
        const action = isAuthenticated ? 'connect' : 'signin';

        const response = (await this.http
            .post(`${SERVER_API_URL}socials/api/oauth/${action}/${providerId}`, data)
            .toPromise()) as ConnectResponse;

        if (response.idToken) {
            await this.authServerProvider.loginWithToken(response.idToken, true);
        }

        return response;
    }

    getConnection(providerId: string): Promise<ConnectionResponse> {
        return this.http
            .get<ConnectionResponse>(`${SERVER_API_URL}socials/api/oauth/connect/${providerId}`)
            .toPromise();
    }

    deleteConnection(providerId: string) {
        return this.http
            .delete(`${SERVER_API_URL}socials/api/oauth/connect/${providerId}`);
    }
}
