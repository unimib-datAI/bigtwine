import { Component } from '@angular/core';
import { AbstractOption } from 'app/analysis/components/analysis-settings/options/abstract-option';

@Component({
    selector: 'btw-text-option',
    templateUrl: './text-option.component.html',
    styleUrls: ['./text-option.component.scss']
})
export class TextOptionComponent extends AbstractOption {}
