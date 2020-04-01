import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterEvent } from '@angular/router';
import { filter, takeUntil } from 'rxjs/operators';
import { ReplaySubject } from 'rxjs';
import { faMap, faStream } from '@fortawesome/free-solid-svg-icons';
import { IconDefinition } from '@fortawesome/fontawesome-common-types';

@Component({
    selector: 'btw-view-mode-switcher',
    templateUrl: './view-mode-switcher.component.html',
    styleUrls: ['./view-mode-switcher.component.scss']
})
export class ViewModeSwitcherComponent implements OnInit, OnDestroy {
    private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

    VIEW_MODE_MAP = 'map';
    VIEW_MODE_LIST = 'list';

    currentViewMode: string;

    constructor(
        private router: Router,
        private route: ActivatedRoute
    ) { }

    ngOnInit(): void {
        this.router.events.pipe(
            filter(e => e instanceof RouterEvent),
            takeUntil(this.destroyed$),
        ).subscribe((e: RouterEvent) => {
            this.onRouteChange(e);
        });

        this.onRouteChange(null);
    }

    ngOnDestroy(): void {
        this.destroyed$.next(true);
        this.destroyed$.complete();
    }

    onRouteChange(event: RouterEvent) {
        this.currentViewMode = this.getCurrentViewMode();
    }

    onToggleViewModeBtnClick() {
        const currentMode = this.currentViewMode;

        this.router
            .navigate([{'outlets': {'results-viewer': currentMode}}], {relativeTo: this.route})
            .catch(e => console.error(e));
    }

    getCurrentViewMode(): string {
        const path = this.route.snapshot.children
            .filter(r => r.outlet === 'results-viewer')
            .map(r => r.routeConfig.path)
            .shift();

        return path === '' ? 'map' : path;
    }

    modeLabel(mode: string): string {
        return mode;
    }

    modeIcon(mode: string): IconDefinition {
        switch (mode) {
            case this.VIEW_MODE_MAP:
                return faMap;
            case this.VIEW_MODE_LIST:
                return faStream;
            default:
                return null;
        }
    }
}
