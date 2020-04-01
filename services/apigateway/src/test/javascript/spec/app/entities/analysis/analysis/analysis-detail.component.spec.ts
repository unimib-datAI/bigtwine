/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ApigatewayTestModule } from '../../../../test.module';
import { AnalysisDetailComponent } from 'app/entities/analysis/analysis/analysis-detail.component';
import { Analysis } from 'app/shared/model/analysis/analysis.model';

describe('Component Tests', () => {
    describe('Analysis Management Detail Component', () => {
        let comp: AnalysisDetailComponent;
        let fixture: ComponentFixture<AnalysisDetailComponent>;
        const route = ({ data: of({ analysis: new Analysis('123') }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ApigatewayTestModule],
                declarations: [AnalysisDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(AnalysisDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(AnalysisDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.analysis).toEqual(jasmine.objectContaining({ id: '123' }));
            });
        });
    });
});
