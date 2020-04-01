/* tslint:disable max-line-length */
import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { take, map } from 'rxjs/operators';
import { AnalysisSettingCollectionService } from 'app/entities/analysis/analysis-setting-collection/analysis-setting-collection.service';
import { IAnalysisSettingCollection, AnalysisSettingCollection } from 'app/shared/model/analysis/analysis-setting-collection.model';
import { AnalysisInputType, AnalysisType } from 'app/shared/model/analysis/analysis.model';

describe('Service Tests', () => {
    describe('AnalysisSettingCollection Service', () => {
        let injector: TestBed;
        let service: AnalysisSettingCollectionService;
        let httpMock: HttpTestingController;
        let elemDefault: IAnalysisSettingCollection;
        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [HttpClientTestingModule]
            });
            injector = getTestBed();
            service = injector.get(AnalysisSettingCollectionService);
            httpMock = injector.get(HttpTestingController);

            elemDefault = new AnalysisSettingCollection('ID', AnalysisType.TWITTER_NEEL, AnalysisInputType.QUERY);
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

            it('should create a AnalysisSettingCollection', async () => {
                const returnedFromService = Object.assign(
                    {
                        id: 'ID'
                    },
                    elemDefault
                );
                const expected = Object.assign({}, returnedFromService);
                service
                    .create(new AnalysisSettingCollection(null))
                    .pipe(take(1))
                    .subscribe(resp => expect(resp).toMatchObject({ body: expected }));
                const req = httpMock.expectOne({ method: 'POST' });
                req.flush(JSON.stringify(returnedFromService));
            });

            it('should update a AnalysisSettingCollection', async () => {
                const returnedFromService = Object.assign(
                    {
                        analysisType: 'BBBBBB',
                        analysisInputTypes: 'BBBBBB'
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

            it('should return a list of AnalysisSettingCollection', async () => {
                const returnedFromService = Object.assign(
                    {
                        analysisType: 'BBBBBB',
                        analysisInputTypes: 'BBBBBB'
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

            it('should delete a AnalysisSettingCollection', async () => {
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
