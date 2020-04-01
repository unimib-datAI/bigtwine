/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { ApigatewayTestModule } from '../../../../test.module';
import { AnalysisDefaultSettingUpdateComponent } from 'app/entities/analysis/analysis-default-setting/analysis-default-setting-update.component';
import { AnalysisDefaultSettingService } from 'app/entities/analysis/analysis-default-setting/analysis-default-setting.service';
import { AnalysisDefaultSetting } from 'app/shared/model/analysis/analysis-default-setting.model';

describe('Component Tests', () => {
    describe('AnalysisDefaultSetting Management Update Component', () => {
        let comp: AnalysisDefaultSettingUpdateComponent;
        let fixture: ComponentFixture<AnalysisDefaultSettingUpdateComponent>;
        let service: AnalysisDefaultSettingService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ApigatewayTestModule],
                declarations: [AnalysisDefaultSettingUpdateComponent]
            })
                .overrideTemplate(AnalysisDefaultSettingUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(AnalysisDefaultSettingUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(AnalysisDefaultSettingService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new AnalysisDefaultSetting('123');
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.analysisDefaultSetting = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new AnalysisDefaultSetting();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.analysisDefaultSetting = entity;
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
