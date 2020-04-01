/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { ApigatewayTestModule } from '../../../../test.module';
import { AnalysisDefaultSettingComponent } from 'app/entities/analysis/analysis-default-setting/analysis-default-setting.component';
import { AnalysisDefaultSettingService } from 'app/entities/analysis/analysis-default-setting/analysis-default-setting.service';
import { AnalysisDefaultSetting } from 'app/shared/model/analysis/analysis-default-setting.model';

describe('Component Tests', () => {
    describe('AnalysisDefaultSetting Management Component', () => {
        let comp: AnalysisDefaultSettingComponent;
        let fixture: ComponentFixture<AnalysisDefaultSettingComponent>;
        let service: AnalysisDefaultSettingService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ApigatewayTestModule],
                declarations: [AnalysisDefaultSettingComponent],
                providers: []
            })
                .overrideTemplate(AnalysisDefaultSettingComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(AnalysisDefaultSettingComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(AnalysisDefaultSettingService);
        });

        it('Should call load all on init', () => {
            // GIVEN
            const headers = new HttpHeaders().append('link', 'link;link');
            spyOn(service, 'query').and.returnValue(
                of(
                    new HttpResponse({
                        body: [new AnalysisDefaultSetting('123')],
                        headers
                    })
                )
            );

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.query).toHaveBeenCalled();
            expect(comp.analysisDefaultSettings[0]).toEqual(jasmine.objectContaining({ id: '123' }));
        });
    });
});
