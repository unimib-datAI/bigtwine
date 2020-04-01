/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ApigatewayTestModule } from '../../../../test.module';
import { AnalysisSettingCollectionDetailComponent } from 'app/entities/analysis/analysis-setting-collection/analysis-setting-collection-detail.component';
import { AnalysisSettingCollection } from 'app/shared/model/analysis/analysis-setting-collection.model';

describe('Component Tests', () => {
    describe('AnalysisSettingCollection Management Detail Component', () => {
        let comp: AnalysisSettingCollectionDetailComponent;
        let fixture: ComponentFixture<AnalysisSettingCollectionDetailComponent>;
        const route = ({ data: of({ analysisSettingCollection: new AnalysisSettingCollection('123') }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ApigatewayTestModule],
                declarations: [AnalysisSettingCollectionDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(AnalysisSettingCollectionDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(AnalysisSettingCollectionDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.analysisSettingCollection).toEqual(jasmine.objectContaining({ id: '123' }));
            });
        });
    });
});
