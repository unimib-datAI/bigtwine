import { Component, Input, OnInit } from '@angular/core';

@Component({
    selector: 'btw-entity-category-icon',
    templateUrl: './entity-category-icon.component.html',
    styleUrls: ['./entity-category-icon.component.scss']
})
export class EntityCategoryIconComponent implements OnInit {

    private _category: string;
    private _categoryClean: string;

    @Input()
    set category(cat: string) {
        this._category = cat;
        this._categoryClean = cat.toLowerCase().replace(/[^a-z0-9]/ig, '');
    }

    get category(): string {
        return this._category;
    }

    get categoryClean(): string {
        return this._categoryClean;
    }

    constructor() { }

    ngOnInit() {
    }

    getCategoryIcon() {
        switch (this.categoryClean) {
            case 'location':
                return 'fas fa-location-arrow';
            case 'event':
                return 'fas fa-calendar';
            case 'product':
                return 'fas fa-box';
            case 'organization':
                return 'fas fa-building';
            case 'thing':
                return 'fas fa-lightbulb';
            case 'character':
                return 'fas fa-film';
            case 'person':
                return 'fas fa-user';
            default:
                return 'fas fa-question-circle';
        }
    }
}
