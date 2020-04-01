/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ApigatewayTestModule } from '../../../../test.module';
import { AnalysisSettingDetailComponent } from 'app/entities/analysis/analysis-setting/analysis-setting-detail.component';
import { AnalysisSetting } from 'app/shared/model/analysis/analysis-setting.model';

describe('Component Tests', () => {
    describe('AnalysisSetting Management Detail Component', () => {
        let comp: AnalysisSettingDetailComponent;
        let fixture: ComponentFixture<AnalysisSettingDetailComponent>;
        const route = ({ data: of({ analysisSetting: new AnalysisSetting('123') }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ApigatewayTestModule],
                declarations: [AnalysisSettingDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(AnalysisSettingDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(AnalysisSettingDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.analysisSetting).toEqual(jasmine.objectContaining({ id: '123' }));
            });
        });
    });
});
