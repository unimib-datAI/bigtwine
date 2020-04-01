import { Component, OnInit } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';

import { AccountService, JhiLanguageHelper } from 'app/core';
import { SocialSignInService } from 'app/social-signin/social-signin.service';
import { SocialSignInProvider } from 'app/social-signin';
import { ConnectionResponse } from 'app/social-signin/model/connection-response.model';

@Component({
    selector: 'btw-settings',
    templateUrl: './settings.component.html'
})
export class SettingsComponent implements OnInit {
    error: string;
    success: string;
    settingsAccount: any;
    languages: any[];
    ssiProviders: SocialSignInProvider[];
    ssiConnections: {[name: string]: ConnectionResponse} = {};

    constructor(
        private accountService: AccountService,
        private languageService: JhiLanguageService,
        private languageHelper: JhiLanguageHelper,
        private ssiService: SocialSignInService,
    ) {}

    ngOnInit() {
        this.accountService.identity().then(account => {
            this.settingsAccount = this.copyAccount(account);
        });
        this.languageHelper.getAll().then(languages => {
            this.languages = languages;
        });
        this.ssiProviders = this.ssiService.signInProviders;
        this.refreshSsiConnections();
    }

    save() {
        this.accountService.save(this.settingsAccount).subscribe(
            () => {
                this.error = null;
                this.success = 'OK';
                this.accountService.identity(true).then(account => {
                    this.settingsAccount = this.copyAccount(account);
                });
                this.languageService.getCurrent().then(current => {
                    if (this.settingsAccount.langKey !== current) {
                        this.languageService.changeLanguage(this.settingsAccount.langKey);
                    }
                });
            },
            () => {
                this.success = null;
                this.error = 'ERROR';
            }
        );
    }

    copyAccount(account) {
        return {
            activated: account.activated,
            email: account.email,
            firstName: account.firstName,
            langKey: account.langKey,
            lastName: account.lastName,
            login: account.login,
            imageUrl: account.imageUrl
        };
    }

    ssiConnectionLoaded(providerId: string): boolean {
        return typeof this.ssiConnections[providerId] !== 'undefined';
    }

    ssiDeleteConnection(providerId: string) {
        this.ssiService.deleteConnection(providerId).subscribe(() => {
            this.refreshSsiConnections();
        });
    }

    refreshSsiConnections() {
        this.ssiConnections = {};
        this.ssiProviders.forEach(provider => {
            this.ssiService.getConnection(provider.id)
                .then(connection => {
                    this.ssiConnections[provider.id] = connection;
                })
                .catch(() => {
                    this.ssiConnections[provider.id] = null;
                });
        });
    }
}
