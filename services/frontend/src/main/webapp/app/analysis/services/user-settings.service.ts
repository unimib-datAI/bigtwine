import { EventEmitter, Injectable, OnDestroy } from '@angular/core';
import { AnalysisState, IAnalysis, IOptionConfig, IOptionChange, SaveUserSettings, selectCurrentAnalysis } from 'app/analysis';
import { Observable, ReplaySubject } from 'rxjs';
import { Store } from '@ngrx/store';
import { take } from 'rxjs/operators';
import { LocalStorageService } from 'ngx-webstorage';

@Injectable({
    providedIn: 'root'
})
export class UserSettingsService implements OnDestroy {

    readonly GLOBAL_OPTIONS_GROUP_KEY = '#global';
    readonly ANALYSIS_OPTIONS_GROUP_KEY = '#analysis';

    private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
    private currentAnalysis$: Observable<IAnalysis>;
    private _options = new Map<string, Map<string, IOptionConfig>>();
    private _values = new Map<string, Map<string, any>>();
    private _changes$ = new EventEmitter<IOptionChange>();

    get changes$(): Observable<IOptionChange> {
        return this._changes$;
    }

    get registeredGroups(): string[] {
        const groups = [];
        for (const group of this._options.entries()) {
            if (group[1].size > 0) {
                groups.push(group[0]);
            }
        }
        return groups;
    }

    get currentAnalysis(): IAnalysis {
        let currentAnalysis: IAnalysis = null;
        this.currentAnalysis$
            .pipe(take(1))
            .subscribe((analysis: IAnalysis) => currentAnalysis = analysis);

        return currentAnalysis;
    }

    constructor(private store: Store<AnalysisState>, private storage: LocalStorageService) {
        this.currentAnalysis$ = this.store.select(selectCurrentAnalysis);
        this._options.set(this.GLOBAL_OPTIONS_GROUP_KEY, new Map());
        this._options.set(this.ANALYSIS_OPTIONS_GROUP_KEY, new Map());
    }

    ngOnDestroy(): void {
        this.destroyed$.next(true);
        this.destroyed$.complete();
    }

    protected registerOption(option: IOptionConfig, group: string) {
        if (!this._options.has(group)) {
            this._options.set(group, new Map());
        }

        const options = this._options.get(group);
        options.set(option.key, option);
    }

    registerOptions(options: IOptionConfig[], initialValues?: {[name: string]: any}, group?: string) {
        group = group ? group : this.GLOBAL_OPTIONS_GROUP_KEY;
        this._options.set(group, new Map());
        this._values.set(group, initialValues ? new Map(Object.keys(initialValues).map(k => [k, initialValues[k] ] as [string, any])) : new Map());
        options.forEach(opt => this.registerOption(opt, group));
    }

    registerGlobalOptions(options: IOptionConfig[], initialValues?: {[name: string]: any}) {
        this.registerOptions(options, initialValues, this.GLOBAL_OPTIONS_GROUP_KEY);
    }

    registerAnalysisOptions(options: IOptionConfig[], initialValues?: {[name: string]: any}) {
        this.registerOptions(options, initialValues, this.ANALYSIS_OPTIONS_GROUP_KEY);
    }

    getOptions(group: string): IOptionConfig[] {
        if (this._options.has(group)) {
            return [...this._options.get(group).values()];
        } else {
            return [];
        }
    }

    getGlobalOptions(): IOptionConfig[] {
        return this.getOptions(this.GLOBAL_OPTIONS_GROUP_KEY);
    }

    getAnalysisOptions(): IOptionConfig[] {
        return this.getOptions(this.ANALYSIS_OPTIONS_GROUP_KEY);
    }

    setOptionValue(group: string, optionKey: string, value: any) {
        if (!this._values.has(group)) {
            return;
        }

        this._values
            .get(group)
            .set(optionKey, value);
    }

    getOptionValue(group: string, optionKey: string): any {
        if (!(this._options.has(group) && this._options.get(group).has(optionKey))) {
            return undefined;
        }

        if (!this._values.has(group) || !this._values.get(group).has(optionKey)) {
            const option = this._options.get(group).get(optionKey);
            return option.defaultValue;
        }

        return this._values
            .get(group)
            .get(optionKey);
    }

    getGroupValues(group: string): {[name: string]: any} {
        if (!this._values.has(group)) {
            return null;
        }

        const valuesMap = this._values.get(group);
        const values = {};
        for (const key of valuesMap.keys()) {
            values[key] = valuesMap.get(key);
        }

        return values;
    }

    setGroupValues(group: string, values: {[name: string]: any}) {
        for (const key in values) {
            if (values.hasOwnProperty(key)) {
                this.setOptionValue(group, key, values[key]);
            }
        }
    }

    protected _saveChanges(groupKey: string) {
        const values = this.getGroupValues(groupKey) || {};
        this.store.dispatch(new SaveUserSettings(groupKey, values));
    }

    saveChanges(group?: string) {
        if (group) {
            this._saveChanges(group);
        } else {
            for (const groupKey of this._options.keys()) {
                this._saveChanges(groupKey);
            }
        }
    }
}
