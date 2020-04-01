/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';

import { ApigatewayTestModule } from '../../../../test.module';
import { AnalysisSettingUpdateComponent } from 'app/entities/analysis/analysis-setting/analysis-setting-update.component';
import { AnalysisSettingService } from 'app/entities/analysis/analysis-setting/analysis-setting.service';
import { AnalysisSetting } from 'app/shared/model/analysis/analysis-setting.model';

describe('Component Tests', () => {
    describe('AnalysisSetting Management Update Component', () => {
        let comp: AnalysisSettingUpdateComponent;
        let fixture: ComponentFixture<AnalysisSettingUpdateComponent>;
        let service: AnalysisSettingService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ApigatewayTestModule],
                declarations: [AnalysisSettingUpdateComponent]
            })
                .overrideTemplate(AnalysisSettingUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(AnalysisSettingUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(AnalysisSettingService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new AnalysisSetting('123');
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.analysisSetting = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new AnalysisSetting();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.analysisSetting = entity;
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
