/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { ApigatewayTestModule } from '../../../../test.module';
import { AnalysisUpdateComponent } from 'app/entities/analysis/analysis/analysis-update.component';
import { AnalysisService } from 'app/entities/analysis/analysis/analysis.service';
import { Analysis } from 'app/shared/model/analysis/analysis.model';

describe('Component Tests', () => {
    describe('Analysis Management Update Component', () => {
        let comp: AnalysisUpdateComponent;
        let fixture: ComponentFixture<AnalysisUpdateComponent>;
        let service: AnalysisService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ApigatewayTestModule],
                declarations: [AnalysisUpdateComponent]
            })
                .overrideTemplate(AnalysisUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(AnalysisUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(AnalysisService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new Analysis('123');
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.analysis = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new Analysis();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.analysis = entity;
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
