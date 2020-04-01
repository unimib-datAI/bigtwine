/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ApigatewayTestModule } from '../../../../test.module';
import { AnalysisSettingCollectionDeleteDialogComponent } from 'app/entities/analysis/analysis-setting-collection/analysis-setting-collection-delete-dialog.component';
import { AnalysisSettingCollectionService } from 'app/entities/analysis/analysis-setting-collection/analysis-setting-collection.service';

describe('Component Tests', () => {
    describe('AnalysisSettingCollection Management Delete Component', () => {
        let comp: AnalysisSettingCollectionDeleteDialogComponent;
        let fixture: ComponentFixture<AnalysisSettingCollectionDeleteDialogComponent>;
        let service: AnalysisSettingCollectionService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ApigatewayTestModule],
                declarations: [AnalysisSettingCollectionDeleteDialogComponent]
            })
                .overrideTemplate(AnalysisSettingCollectionDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(AnalysisSettingCollectionDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(AnalysisSettingCollectionService);
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
