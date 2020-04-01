import { NgModule } from '@angular/core';

import { BigtwineSharedLibsModule, FindLanguageFromKeyPipe, BtwAlertComponent, BtwAlertErrorComponent } from './';

@NgModule({
    imports: [BigtwineSharedLibsModule],
    declarations: [FindLanguageFromKeyPipe, BtwAlertComponent, BtwAlertErrorComponent],
    exports: [BigtwineSharedLibsModule, FindLanguageFromKeyPipe, BtwAlertComponent, BtwAlertErrorComponent]
})
export class BigtwineSharedCommonModule {}
