/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { ApigatewayTestModule } from '../../../../test.module';
import { AnalysisSettingComponent } from 'app/entities/analysis/analysis-setting/analysis-setting.component';
import { AnalysisSettingService } from 'app/entities/analysis/analysis-setting/analysis-setting.service';
import { AnalysisSetting } from 'app/shared/model/analysis/analysis-setting.model';

describe('Component Tests', () => {
    describe('AnalysisSetting Management Component', () => {
        let comp: AnalysisSettingComponent;
        let fixture: ComponentFixture<AnalysisSettingComponent>;
        let service: AnalysisSettingService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ApigatewayTestModule],
                declarations: [AnalysisSettingComponent],
                providers: []
            })
                .overrideTemplate(AnalysisSettingComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(AnalysisSettingComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(AnalysisSettingService);
        });

        it('Should call load all on init', () => {
            // GIVEN
            const headers = new HttpHeaders().append('link', 'link;link');
            spyOn(service, 'query').and.returnValue(
                of(
                    new HttpResponse({
                        body: [new AnalysisSetting('123')],
                        headers
                    })
                )
            );

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.query).toHaveBeenCalled();
            expect(comp.analysisSettings[0]).toEqual(jasmine.objectContaining({ id: '123' }));
        });
    });
});
