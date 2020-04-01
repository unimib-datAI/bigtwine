import { Injectable, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { AnalysisState, selectAlerts } from 'app/analysis';
import { IAlert } from 'app/analysis/models/alert.model';
import { JhiAlert, JhiAlertService } from 'ng-jhipster';

@Injectable()
export class AnalysisAlertsService {
    alertsCount = 0;

    constructor(private store: Store<AnalysisState>, private alertService: JhiAlertService) {
        this.init();
    }

    init() {
        this.store.select(selectAlerts).subscribe((alerts: IAlert[]) => {
            console.log('On alerts', alerts, this.alertsCount);
            if (alerts.length > this.alertsCount) {
                this.onAlert(alerts[0]);
                this.alertsCount = alerts.length;
            }
        });
    }

    onAlert(alert: IAlert) {
        const jhiAlert: JhiAlert = {
            type: alert.type,
            msg: `<strong>${alert.title}</strong><br>${alert.message}`,
            timeout: 5000,
            toast: true,
            position: 'right bottom'
        };

        this.alertService.addAlert(jhiAlert, []);
    }
}
