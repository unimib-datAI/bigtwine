import { Component } from '@angular/core';
import { AbstractOption } from 'app/analysis/components/analysis-settings/options/abstract-option';
import { ChoiceEntry, ChoicesOptionConfig } from 'app/analysis';

@Component({
    selector: 'btw-choices-option',
    templateUrl: './choices-option.component.html',
    styleUrls: ['./choices-option.component.scss']
})
export class ChoicesOptionComponent extends AbstractOption {
    get choices(): ChoiceEntry[] {
        return (this.option as ChoicesOptionConfig).choices;
    }
}
