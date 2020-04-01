import { Component, Input } from '@angular/core';
import { IOptionConfig } from 'app/analysis';

@Component({
    selector: 'btw-option-wrapper',
    templateUrl: './option-wrapper.component.html',
    styleUrls: ['./option-wrapper.component.scss']
})
export class OptionWrapperComponent {
    @Input() option: IOptionConfig;
}
