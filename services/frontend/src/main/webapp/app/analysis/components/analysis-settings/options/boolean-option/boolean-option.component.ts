import { Component, Input } from '@angular/core';
import { AbstractOption } from 'app/analysis/components/analysis-settings/options/abstract-option';

@Component({
    selector: 'btw-boolean-option',
    templateUrl: './boolean-option.component.html',
    styleUrls: ['./boolean-option.component.scss']
})
export class BooleanOptionComponent extends AbstractOption {
}
