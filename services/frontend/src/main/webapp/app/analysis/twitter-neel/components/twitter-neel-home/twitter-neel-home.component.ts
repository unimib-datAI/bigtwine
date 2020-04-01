import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ReplaySubject } from 'rxjs';
import { AnalysisListComponent } from 'app/analysis/components';
import { SocialSignInProviderId, SocialSignInService } from 'app/social-signin/social-signin.service';

@Component({
    templateUrl: './twitter-neel-home.component.html',
    styleUrls: ['./twitter-neel-home.component.scss'],
    selector: 'btw-twitter-neel-home',
})
export class TwitterNeelHomeComponent implements OnInit, OnDestroy {

    @ViewChild(AnalysisListComponent) analysisListChild;
    private destroyed$ = new ReplaySubject<boolean>(1);
    hasTwitterConnection: boolean;

    constructor(
        private ssiService: SocialSignInService
    ) {}

    ngOnInit(): void {
        this.ssiService.getConnection(SocialSignInProviderId.Twitter)
            .then(connection => {
                this.hasTwitterConnection = connection && connection.connected;
            })
            .catch(() => {
                this.hasTwitterConnection = false;
            });
    }

    ngOnDestroy(): void {
        this.destroyed$.next(true);
        this.destroyed$.complete();
    }

    refresh() {
        this.analysisListChild.refresh();
    }
}
