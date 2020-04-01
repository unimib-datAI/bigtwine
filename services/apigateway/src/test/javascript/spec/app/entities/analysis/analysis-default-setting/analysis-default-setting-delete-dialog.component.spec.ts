/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ApigatewayTestModule } from '../../../../test.module';
import { AnalysisDefaultSettingDeleteDialogComponent } from 'app/entities/analysis/analysis-default-setting/analysis-default-setting-delete-dialog.component';
import { AnalysisDefaultSettingService } from 'app/entities/analysis/analysis-default-setting/analysis-default-setting.service';

describe('Component Tests', () => {
    describe('AnalysisDefaultSetting Management Delete Component', () => {
        let comp: AnalysisDefaultSettingDeleteDialogComponent;
        let fixture: ComponentFixture<AnalysisDefaultSettingDeleteDialogComponent>;
        let service: AnalysisDefaultSettingService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ApigatewayTestModule],
                declarations: [AnalysisDefaultSettingDeleteDialogComponent]
            })
                .overrideTemplate(AnalysisDefaultSettingDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(AnalysisDefaultSettingDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(AnalysisDefaultSettingService);
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
