/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ApigatewayTestModule } from '../../../../test.module';
import { AnalysisDefaultSettingDetailComponent } from 'app/entities/analysis/analysis-default-setting/analysis-default-setting-detail.component';
import { AnalysisDefaultSetting } from 'app/shared/model/analysis/analysis-default-setting.model';

describe('Component Tests', () => {
    describe('AnalysisDefaultSetting Management Detail Component', () => {
        let comp: AnalysisDefaultSettingDetailComponent;
        let fixture: ComponentFixture<AnalysisDefaultSettingDetailComponent>;
        const route = ({ data: of({ analysisDefaultSetting: new AnalysisDefaultSetting('123') }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ApigatewayTestModule],
                declarations: [AnalysisDefaultSettingDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(AnalysisDefaultSettingDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(AnalysisDefaultSettingDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.analysisDefaultSetting).toEqual(jasmine.objectContaining({ id: '123' }));
            });
        });
    });
});
