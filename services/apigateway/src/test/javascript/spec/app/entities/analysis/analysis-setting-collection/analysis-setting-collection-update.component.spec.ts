/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { ApigatewayTestModule } from '../../../../test.module';
import { AnalysisSettingCollectionUpdateComponent } from 'app/entities/analysis/analysis-setting-collection/analysis-setting-collection-update.component';
import { AnalysisSettingCollectionService } from 'app/entities/analysis/analysis-setting-collection/analysis-setting-collection.service';
import { AnalysisSettingCollection } from 'app/shared/model/analysis/analysis-setting-collection.model';

describe('Component Tests', () => {
    describe('AnalysisSettingCollection Management Update Component', () => {
        let comp: AnalysisSettingCollectionUpdateComponent;
        let fixture: ComponentFixture<AnalysisSettingCollectionUpdateComponent>;
        let service: AnalysisSettingCollectionService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ApigatewayTestModule],
                declarations: [AnalysisSettingCollectionUpdateComponent]
            })
                .overrideTemplate(AnalysisSettingCollectionUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(AnalysisSettingCollectionUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(AnalysisSettingCollectionService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new AnalysisSettingCollection('123');
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.analysisSettingCollection = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new AnalysisSettingCollection();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.analysisSettingCollection = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.create).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));
        });
    });
});
