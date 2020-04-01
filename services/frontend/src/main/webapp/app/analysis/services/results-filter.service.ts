import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';
import { IPage, IResultsFilterQuery } from 'app/analysis/models';
import { IResultsFilterType } from 'app/analysis/models/results-filter-type.model';

export const DEFAULT_RESULTS_FILTER_THROTTLE = 5000;

export interface IResultsFilterService {

    readonly filteredResults$: Observable<any>;
    readonly currentQuery: IResultsFilterQuery;
    readonly currentQuery$: Observable<IResultsFilterQuery>;

    /**
     * Avvia una ricerca sui risultati disponibili localmente
     * @param query
     * @param page
     * @param throttleDuration
     */
    localSearch(query: IResultsFilterQuery, page: IPage, throttleDuration);

    localSearch(query: IResultsFilterQuery, page: IPage);

    clear();

    localSearchSupportedTypes(): IResultsFilterType[];

    fullSearchSupportedTypes(): IResultsFilterType[];

    buildFullSearchQuery(type: string, value: any): IResultsFilterQuery;
}

export const RESULTS_FILTER_SERVICE = new InjectionToken<IResultsFilterService>('results-filter.service');
