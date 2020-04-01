import { Injectable, Injector } from '@angular/core';

import { AccountService } from '../auth/account.service';
import { AuthServerProvider } from '../auth/auth-jwt.service';
import { BtwTrackerService } from '../tracker/tracker.service';

@Injectable({ providedIn: 'root' })
export class LoginService {

    get accountService(): AccountService {
        if (!this._accountService) {
            this._accountService = this.injector.get(AccountService);
        }

        return this._accountService;
    }

    constructor(
        private _accountService: AccountService,
        private injector: Injector,
        private trackerService: BtwTrackerService,
        private authServerProvider: AuthServerProvider
    ) {}

    login(credentials, callback?) {
        const cb = callback || function() {};

        return new Promise((resolve, reject) => {
            this.authServerProvider.login(credentials).subscribe(
                data => {
                    this.accountService.identity(true).then(account => {
                        this.trackerService.sendActivity();
                        resolve(data);
                    });
                    return cb();
                },
                err => {
                    this.logout();
                    reject(err);
                    return cb(err);
                }
            );
        });
    }

    loginWithToken(jwt, rememberMe) {
        return this.authServerProvider.loginWithToken(jwt, rememberMe);
    }

    logout() {
        this.authServerProvider.logout().subscribe();
        this.accountService.authenticate(null);
    }
}
