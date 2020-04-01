/* tslint:disable max-line-length */
import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { take, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { AnalysisService } from 'app/entities/analysis/analysis/analysis.service';
import {
    IAnalysis,
    Analysis,
    AnalysisType,
    AnalysisInputType,
    AnalysisStatus,
    AnalysisVisibility
} from 'app/shared/model/analysis/analysis.model';

describe('Service Tests', () => {
    describe('Analysis Service', () => {
        let injector: TestBed;
        let service: AnalysisService;
        let httpMock: HttpTestingController;
        let elemDefault: IAnalysis;
        let currentDate: moment.Moment;
        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [HttpClientTestingModule]
            });
            injector = getTestBed();
            service = injector.get(AnalysisService);
            httpMock = injector.get(HttpTestingController);
            currentDate = moment();

            elemDefault = new Analysis(
                'ID',
                AnalysisType.TWITTER_NEEL,
                AnalysisInputType.QUERY,
                AnalysisStatus.READY,
                AnalysisVisibility.PRIVATE,
                'AAAAAAA',
                0,
                currentDate,
                currentDate,
                'AAAAAAA',
                'AAAAAAA',
                0
            );
        });

        describe('Service methods', async () => {
            it('should find an element', async () => {
                const returnedFromService = Object.assign(
                    {
                        createDate: currentDate.format(DATE_TIME_FORMAT),
                        updateDate: currentDate.format(DATE_TIME_FORMAT)
                    },
                    elemDefault
                );
                service
                    .find('123')
                    .pipe(take(1))
                    .subscribe(resp => expect(resp).toMatchObject({ body: elemDefault }));

                const req = httpMock.expectOne({ method: 'GET' });
                req.flush(JSON.stringify(returnedFromService));
            });

            it('should create a Analysis', async () => {
                const returnedFromService = Object.assign(
                    {
                        id: 'ID',
                        createDate: currentDate.format(DATE_TIME_FORMAT),
                        updateDate: currentDate.format(DATE_TIME_FORMAT)
                    },
                    elemDefault
                );
                const expected = Object.assign(
                    {
                        createDate: currentDate,
                        updateDate: currentDate
                    },
                    returnedFromService
                );
                service
                    .create(new Analysis(null))
                    .pipe(take(1))
                    .subscribe(resp => expect(resp).toMatchObject({ body: expected }));
                const req = httpMock.expectOne({ method: 'POST' });
                req.flush(JSON.stringify(returnedFromService));
            });

            it('should update a Analysis', async () => {
                const returnedFromService = Object.assign(
                    {
                        type: 'BBBBBB',
                        inputType: 'BBBBBB',
                        status: 'BBBBBB',
                        visibility: 'BBBBBB',
                        owner: 'BBBBBB',
                        progress: 1,
                        createDate: currentDate.format(DATE_TIME_FORMAT),
                        updateDate: currentDate.format(DATE_TIME_FORMAT),
                        input: 'BBBBBB',
                        settings: 'BBBBBB',
                        resultsCount: 1
                    },
                    elemDefault
                );

                const expected = Object.assign(
                    {
                        createDate: currentDate,
                        updateDate: currentDate
                    },
                    returnedFromService
                );
                service
                    .update(expected)
                    .pipe(take(1))
                    .subscribe(resp => expect(resp).toMatchObject({ body: expected }));
                const req = httpMock.expectOne({ method: 'PUT' });
                req.flush(JSON.stringify(returnedFromService));
            });

            it('should return a list of Analysis', async () => {
                const returnedFromService = Object.assign(
                    {
                        type: 'BBBBBB',
                        inputType: 'BBBBBB',
                        status: 'BBBBBB',
                        visibility: 'BBBBBB',
                        owner: 'BBBBBB',
                        progress: 1,
                        createDate: currentDate.format(DATE_TIME_FORMAT),
                        updateDate: currentDate.format(DATE_TIME_FORMAT),
                        input: 'BBBBBB',
                        settings: 'BBBBBB',
                        resultsCount: 1
                    },
                    elemDefault
                );
                const expected = Object.assign(
                    {
                        createDate: currentDate,
                        updateDate: currentDate
                    },
                    returnedFromService
                );
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

            it('should delete a Analysis', async () => {
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
