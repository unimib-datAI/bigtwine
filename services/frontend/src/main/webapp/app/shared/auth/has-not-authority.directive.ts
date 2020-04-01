import { Directive, Input, TemplateRef, ViewContainerRef } from '@angular/core';
import { AccountService } from 'app/core/auth/account.service';

/**
 * @whatItDoes Conditionally includes an HTML element if current user hasn't
 * the authority passed as the `expression`.
 *
 * @howToUse
 * ```
 *     <some-element *btwHasNotAuthority="'ROLE_ADMIN'">...</some-element>
 *
 * ```
 */
@Directive({
    selector: '[btwHasNotAuthority]'
})
export class HasNotAuthorityDirective {
    private authority: string;

    constructor(
        private accountService: AccountService,
        private templateRef: TemplateRef<any>,
        private viewContainerRef: ViewContainerRef
    ) {}

    @Input()
    set btwHasNotAuthority(value: string) {
        this.authority = value;
        this.updateView();
        // Get notified each time authentication state changes.
        this.accountService.getAuthenticationState().subscribe(identity => this.updateView());
    }

    private updateView(): void {
        const hasAnyAuthority = this.accountService.hasAnyAuthority([this.authority]);
        this.viewContainerRef.clear();
        if (!hasAnyAuthority) {
            this.viewContainerRef.createEmbeddedView(this.templateRef);
        }
    }
}
