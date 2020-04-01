import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { AnalysisState, IGeoAreaAnalysisInput } from 'app/analysis';
import { StreamAnalysisViewComponent } from 'app/analysis/components';
import { GeoArea } from 'app/analysis/models/geo-area.model';
import { AccountService } from 'app/core';

@Component({
  selector: 'btw-geoarea-view',
  templateUrl: './geoarea-view.component.html',
  styleUrls: ['./geoarea-view.component.scss']
})
export class GeoareaViewComponent extends StreamAnalysisViewComponent {

    get geoArea(): GeoArea {
        if (!this.currentAnalysis) {
            return null;
        }

        const geoInput = (this.currentAnalysis.input as IGeoAreaAnalysisInput);
        return new GeoArea(geoInput.description, geoInput.boundingBoxes);
    }

    constructor(
        protected router: Router,
        protected route: ActivatedRoute,
        protected analysisStore: Store<AnalysisState>,
        protected accountService: AccountService) {
        super(router, route, analysisStore, accountService);
    }
}
