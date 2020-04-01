import { JhiAlertType } from 'ng-jhipster';

export interface IAlert {
    type: JhiAlertType;
    title: string;
    message?: string;
    error?: any;
}
