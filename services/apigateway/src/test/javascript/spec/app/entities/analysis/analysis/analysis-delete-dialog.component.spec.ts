/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ApigatewayTestModule } from '../../../../test.module';
import { AnalysisDeleteDialogComponent } from 'app/entities/analysis/analysis/analysis-delete-dialog.component';
import { AnalysisService } from 'app/entities/analysis/analysis/analysis.service';

describe('Component Tests', () => {
    describe('Analysis Management Delete Component', () => {
        let comp: AnalysisDeleteDialogComponent;
        let fixture: ComponentFixture<AnalysisDeleteDialogComponent>;
        let service: AnalysisService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ApigatewayTestModule],
                declarations: [AnalysisDeleteDialogComponent]
            })
                .overrideTemplate(AnalysisDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(AnalysisDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(AnalysisService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete', inject(
                [],
                fakeAsync(() => {
                    // GIVEN
                    spyOn(service, 'delete').and.returnValue(of({}));

                    // WHEN
                    comp.confirmDelete('123');
                    tick();

                    // THEN
                    expect(service.delete).toHaveBeenCalledWith('123');
                    expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                })
            ));
        });
    });
});
