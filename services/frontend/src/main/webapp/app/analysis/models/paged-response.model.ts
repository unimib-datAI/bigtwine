import { IPageDetails } from 'app/analysis/models/page-details.model';

export interface IPagedResponse extends IPageDetails {
    objects: any[];
}
