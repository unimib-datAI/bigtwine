import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'btw-confidence-icon',
  templateUrl: './confidence-icon.component.html',
  styleUrls: ['./confidence-icon.component.scss']
})
export class ConfidenceIconComponent implements OnInit {
    @Input() value: number;

    get roundedValue(): number {
        return Math.floor(this.value * 10) / 10;
    }

    constructor() { }

    ngOnInit() {
    }

    getConfidenceClasses(): string[] {
        const baseClass = 'confidence-icon';
        const classes = [baseClass];

        if (this.value < 0.3) {
            classes.push(`${baseClass}--low`);
        } else if (this.value < 0.6) {
            classes.push(`${baseClass}--medium`);
        } else {
            classes.push(`${baseClass}--high`);
        }

        return classes;
    }

}
