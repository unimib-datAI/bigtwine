/* tslint:disable max-line-length */
import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { take, map } from 'rxjs/operators';
import { AnalysisSettingService } from 'app/entities/analysis/analysis-setting/analysis-setting.service';
import {
    IAnalysisSetting,
    AnalysisSetting,
    AnalysisSettingType,
    AnalysisSettingVisibility
} from 'app/shared/model/analysis/analysis-setting.model';

describe('Service Tests', () => {
    describe('AnalysisSetting Service', () => {
        let injector: TestBed;
        let service: AnalysisSettingService;
        let httpMock: HttpTestingController;
        let elemDefault: IAnalysisSetting;
        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [HttpClientTestingModule]
            });
            injector = getTestBed();
            service = injector.get(AnalysisSettingService);
            httpMock = injector.get(HttpTestingController);

            elemDefault = new AnalysisSetting(
                'ID',
                'AAAAAAA',
                'AAAAAAA',
                'AAAAAAA',
                AnalysisSettingType.NUMBER,
                AnalysisSettingVisibility.GLOBAL,
                'AAAAAAA'
            );
        });

        describe('Service methods', async () => {
            it('should find an element', async () => {
                const returnedFromService = Object.assign({}, elemDefault);
                service
                    .find('123')
                    .pipe(take(1))
                    .subscribe(resp => expect(resp).toMatchObject({ body: elemDefault }));

                const req = httpMock.expectOne({ method: 'GET' });
                req.flush(JSON.stringify(returnedFromService));
            });

            it('should create a AnalysisSetting', async () => {
                const returnedFromService = Object.assign(
                    {
                        id: 'ID'
                    },
                    elemDefault
                );
                const expected = Object.assign({}, returnedFromService);
                service
                    .create(new AnalysisSetting(null))
                    .pipe(take(1))
                    .subscribe(resp => expect(resp).toMatchObject({ body: expected }));
                const req = httpMock.expectOne({ method: 'POST' });
                req.flush(JSON.stringify(returnedFromService));
            });

            it('should update a AnalysisSetting', async () => {
                const returnedFromService = Object.assign(
                    {
                        name: 'BBBBBB',
                        label: 'BBBBBB',
                        description: 'BBBBBB',
                        type: 'BBBBBB',
                        visibility: 'BBBBBB',
                        options: 'BBBBBB'
                    },
                    elemDefault
                );

                const expected = Object.assign({}, returnedFromService);
                service
                    .update(expected)
                    .pipe(take(1))
                    .subscribe(resp => expect(resp).toMatchObject({ body: expected }));
                const req = httpMock.expectOne({ method: 'PUT' });
                req.flush(JSON.stringify(returnedFromService));
            });

            it('should return a list of AnalysisSetting', async () => {
                const returnedFromService = Object.assign(
                    {
                        name: 'BBBBBB',
                        label: 'BBBBBB',
                        description: 'BBBBBB',
                        type: 'BBBBBB',
                        visibility: 'BBBBBB',
                        options: 'BBBBBB'
                    },
                    elemDefault
                );
                const expected = Object.assign({}, returnedFromService);
                service
                    .query(expected)
                    .pipe(
                        take(1),
                        map(resp => resp.body)
                    )
                    .subscribe(body => expect(body).toContainEqual(expected));
                const req = httpMock.expectOne({ method: 'GET' });
                req.flush(JSON.stringify([returnedFromService]));
                httpMock.verify();
            });

            it('should delete a AnalysisSetting', async () => {
                const rxPromise = service.delete('123').subscribe(resp => expect(resp.ok));

                const req = httpMock.expectOne({ method: 'DELETE' });
                req.flush({ status: 200 });
            });
        });

        afterEach(() => {
            httpMock.verify();
        });
    });
});
