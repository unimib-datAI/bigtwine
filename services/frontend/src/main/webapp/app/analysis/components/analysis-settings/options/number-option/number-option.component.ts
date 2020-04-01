import { Component, Input } from '@angular/core';
import { AbstractOption } from 'app/analysis/components/analysis-settings/options/abstract-option';

@Component({
    selector: 'btw-number-option',
    templateUrl: './number-option.component.html',
    styleUrls: ['./number-option.component.scss']
})
export class NumberOptionComponent extends AbstractOption {
    @Input() step = 1;
    @Input() minValue = Number.MIN_VALUE;
    @Input() maxValue = Number.MAX_VALUE;
}
