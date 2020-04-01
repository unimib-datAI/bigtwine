import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {SocialSignInService} from 'app/social-signin/social-signin.service';
import { ConnectResponse } from 'app/social-signin/model/connect-response.model';
import { WindowRef } from 'app/core';

@Component({
    templateUrl: './social-signin-oauth-callback.component.html'
})
export class SocialSignInOauthCallbackComponent implements OnInit {
    isLoading = true;
    username: string;
    error: string;

    constructor(
        private route: ActivatedRoute,
        private ssiService: SocialSignInService,
        private $window: WindowRef) { }

    ngOnInit(): void {
        const oauthVerifier = this.route.snapshot.queryParamMap.get('oauth_verifier');
        const oauthToken = this.route.snapshot.queryParamMap.get('oauth_token');
        const providerId = this.route.snapshot.paramMap.get('providerId');

        this.ssiService.completeSignInOAuth10Routine(providerId, oauthToken, oauthVerifier)
            .then((response: ConnectResponse) => {
                this.isLoading = false;
                this.username = response.displayName;
                this.refreshParentWindow();
                this.closeWindow();
            })
            .catch(err => {
                this.isLoading = false;
                this.error = err.message;
            });
    }

    closeWindow(delay = 3000) {
        setTimeout(() => {
            this.$window.nativeWindow.close();
        }, delay);
    }

    refreshParentWindow() {
        if (this.$window.nativeWindow.opener) {
            this.$window.nativeWindow.opener.location.reload(true);
        }
    }
}
