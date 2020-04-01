/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ApigatewayTestModule } from '../../../../test.module';
import { AnalysisSettingDeleteDialogComponent } from 'app/entities/analysis/analysis-setting/analysis-setting-delete-dialog.component';
import { AnalysisSettingService } from 'app/entities/analysis/analysis-setting/analysis-setting.service';

describe('Component Tests', () => {
    describe('AnalysisSetting Management Delete Component', () => {
        let comp: AnalysisSettingDeleteDialogComponent;
        let fixture: ComponentFixture<AnalysisSettingDeleteDialogComponent>;
        let service: AnalysisSettingService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ApigatewayTestModule],
                declarations: [AnalysisSettingDeleteDialogComponent]
            })
                .overrideTemplate(AnalysisSettingDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(AnalysisSettingDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(AnalysisSettingService);
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
