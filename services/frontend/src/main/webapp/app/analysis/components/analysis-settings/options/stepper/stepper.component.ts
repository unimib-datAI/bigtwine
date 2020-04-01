import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
    selector: 'btw-stepper',
    templateUrl: './stepper.component.html',
    styleUrls: ['./stepper.component.scss']
})
export class StepperComponent {
    @Input() value = 0;
    @Output() valueChange = new EventEmitter<number>();
    @Input() disabled = false;
    @Input() step = 1;
    @Input() minValue = Number.MIN_VALUE;
    @Input() maxValue = Number.MAX_VALUE;

    onPlusBtnClick() {
        this.updateValue(this.step);
    }

    onMinusBtnClick() {
        this.updateValue(-this.step);
    }

    updateValue(diff: number) {
        const newValue = (this.value ? this.value : 0) + diff;

        if (newValue >= this.minValue && newValue <= this.maxValue) {
            this.value = newValue;
            this.valueChange.emit(this.value);
        }
    }
}
