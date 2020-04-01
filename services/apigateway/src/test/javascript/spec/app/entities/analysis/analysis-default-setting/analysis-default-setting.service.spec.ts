/* tslint:disable max-line-length */
import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { take, map } from 'rxjs/operators';
import { AnalysisDefaultSettingService } from 'app/entities/analysis/analysis-default-setting/analysis-default-setting.service';
import { IAnalysisDefaultSetting, AnalysisDefaultSetting } from 'app/shared/model/analysis/analysis-default-setting.model';
import { AnalysisInputType, AnalysisType } from 'app/shared/model/analysis/analysis.model';

describe('Service Tests', () => {
    describe('AnalysisDefaultSetting Service', () => {
        let injector: TestBed;
        let service: AnalysisDefaultSettingService;
        let httpMock: HttpTestingController;
        let elemDefault: IAnalysisDefaultSetting;
        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [HttpClientTestingModule]
            });
            injector = getTestBed();
            service = injector.get(AnalysisDefaultSettingService);
            httpMock = injector.get(HttpTestingController);

            elemDefault = new AnalysisDefaultSetting(
                'ID',
                'AAAAAAA',
                AnalysisType.TWITTER_NEEL,
                AnalysisInputType.QUERY,
                'AAAAAAA',
                false,
                0
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

            it('should create a AnalysisDefaultSetting', async () => {
                const returnedFromService = Object.assign(
                    {
                        id: 'ID'
                    },
                    elemDefault
                );
                const expected = Object.assign({}, returnedFromService);
                service
                    .create(new AnalysisDefaultSetting(null))
                    .pipe(take(1))
                    .subscribe(resp => expect(resp).toMatchObject({ body: expected }));
                const req = httpMock.expectOne({ method: 'POST' });
                req.flush(JSON.stringify(returnedFromService));
            });

            it('should update a AnalysisDefaultSetting', async () => {
                const returnedFromService = Object.assign(
                    {
                        defaultValue: 'BBBBBB',
                        analysisType: 'BBBBBB',
                        analysisInputTypes: 'BBBBBB',
                        userRoles: 'BBBBBB',
                        userCanOverride: true,
                        priority: 1
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

            it('should return a list of AnalysisDefaultSetting', async () => {
                const returnedFromService = Object.assign(
                    {
                        defaultValue: 'BBBBBB',
                        analysisType: 'BBBBBB',
                        analysisInputTypes: 'BBBBBB',
                        userRoles: 'BBBBBB',
                        userCanOverride: true,
                        priority: 1
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

            it('should delete a AnalysisDefaultSetting', async () => {
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
