import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'btw-tweets-counter-icon',
  templateUrl: './tweets-counter-icon.component.html',
  styleUrls: ['./tweets-counter-icon.component.scss']
})
export class TweetsCounterIconComponent implements OnInit {
    @Input() value: number;

    constructor() { }

    ngOnInit() {
    }

}
