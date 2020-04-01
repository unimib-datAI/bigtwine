import { EventEmitter, Input, Output } from '@angular/core';
import { IOptionConfig, IOptionChange } from 'app/analysis';

export abstract class AbstractOption {
    @Input() option: IOptionConfig;
    @Input() value: any;
    @Input() disabled = false;
    @Output() change = new EventEmitter<IOptionChange>();

    onValueChange(newValue) {
        console.log(newValue);
        this.change.emit({
            optionKey: this.option.key,
            value: newValue
        });
    }
}
