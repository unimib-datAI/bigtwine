import { Component, OnInit } from '@angular/core';
import { AnalysisState, IAnalysis, RestoreUserSettings, SaveAnalysisSettings, selectCurrentAnalysis } from 'app/analysis';
import { select, Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { skipLast, take, takeUntil } from 'rxjs/operators';
import { AnalysisService } from 'app/analysis/services/analysis.service';
import { IAnalysisSetting } from 'app/analysis/models/analysis-setting.model';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'btw-analysis-settings',
    templateUrl: './analysis-settings.component.html',
    styleUrls: ['./analysis-settings.component.scss']
})
export class AnalysisSettingsComponent implements OnInit {
    currentAnalysis$: Observable<IAnalysis>;
    _settings: IAnalysisSetting[] = [];
    settingsModel: {[name: string]: any};
    hasEditableSettings = false;
    isLoading = false;

    get settings(): IAnalysisSetting[] {
        return this._settings;
    }

    set settings(settings: IAnalysisSetting[]) {
        this._settings = settings;
        this.onSettingsChange();
    }

    get currentAnalysis(): IAnalysis {
        let currentAnalysis: IAnalysis = null;
        this.currentAnalysis$
            .pipe(take(1))
            .subscribe((analysis: IAnalysis) => currentAnalysis = analysis);

        return currentAnalysis;
    }

    constructor(
        private analysisService: AnalysisService,
        private store: Store<AnalysisState>,
        public activeModal: NgbActiveModal) {
    }

    ngOnInit(): void {
        this.currentAnalysis$ = this.store.pipe(select(selectCurrentAnalysis));
        this.store.dispatch(new RestoreUserSettings());

        this.fetchAnalysisSettings(this.currentAnalysis);
        this.currentAnalysis$.subscribe(analysis => {
            this.fetchAnalysisSettings(analysis);
        });
    }

    fetchAnalysisSettings(analysis: IAnalysis) {
        if (!analysis) {
             this.settings = [];
        } else {
            this.isLoading = true;
            this.analysisService
                .getAnalysisSettings(analysis.id)
                .pipe(takeUntil(this.currentAnalysis$.pipe(skipLast(1))))
                .subscribe(settings => {
                    this.settings = settings;
                    this.isLoading = false;
                });
        }
    }

    onSettingsChange() {
        const values = {};
        this.settings.forEach(setting => {
            values[setting.name] = (setting.currentValue == null) ? setting.defaultValue : setting.currentValue;
            this.hasEditableSettings = this.hasEditableSettings || setting.editable;
        });
        this.settingsModel = values;
    }

    save() {
        const analysis = this.currentAnalysis;
        if (analysis) {
            this.store.dispatch(new SaveAnalysisSettings(analysis.id, this.settingsModel));
        }
    }
}
