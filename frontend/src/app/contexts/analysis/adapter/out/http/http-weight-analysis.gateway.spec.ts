import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { HttpClient, provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { HttpWeightAnalysisGateway } from './http-weight-analysis.gateway';
import type { GetWeightAnalysisOutcome } from '../../../application/port/out/weight-analysis.store';

describe('HttpWeightAnalysisGateway', () => {
  let gateway: HttpWeightAnalysisGateway;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    gateway = new HttpWeightAnalysisGateway(TestBed.inject(HttpClient));
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('gets a populated weight analysis', async () => {
    const expectedOutcome: GetWeightAnalysisOutcome = {
      kind: 'data',
      outcomeData: {
        timelineStartDate: '2026-09-20',
        range: {
          startDate: '2026-09-20',
          endDate: '2026-09-25',
        },
        weightMeasurements: [
          {
            date: '2026-09-20',
            dayNumber: 1,
            weightInKg: 82.1,
          },
          {
            date: '2026-09-23',
            dayNumber: 4,
            weightInKg: 81.9,
          },
        ],
      },
    };

    const outcomePromise = gateway.get();

    const request = httpTestingController.expectOne('/api/analysis/weight');
    expect(request.request.method).toBe('GET');

    request.flush({
      timelineStartDate: '2026-09-20',
      range: {
        startDate: '2026-09-20',
        endDate: '2026-09-25',
      },
      weightMeasurements: [
        {
          date: '2026-09-20',
          dayNumber: 1,
          weightInKg: 82.1,
        },
        {
          date: '2026-09-23',
          dayNumber: 4,
          weightInKg: 81.9,
        },
      ],
      rollingAverages: [],
    });

    await expect(outcomePromise).resolves.toEqual(expectedOutcome);
  });

  it('gets a globally empty weight analysis', async () => {
    const outcomePromise = gateway.get();

    const request = httpTestingController.expectOne('/api/analysis/weight');
    request.flush({
      timelineStartDate: null,
      range: null,
      weightMeasurements: [],
      rollingAverages: [],
    });

    await expect(outcomePromise).resolves.toEqual({
      kind: 'empty',
    });
  });

  it('rejects a malformed successful response', async () => {
    const outcomePromise = gateway.get();

    const request = httpTestingController.expectOne('/api/analysis/weight');
    request.flush({
      timelineStartDate: '2026-09-20',
      range: {
        startDate: 42,
        endDate: '2026-09-20',
      },
      weightMeasurements: [],
      rollingAverages: [],
    });

    await expect(outcomePromise).resolves.toEqual({
      kind: 'failed',
      outcomeData: {
        errorMessage: 'The backend returned an invalid response.',
      },
    });
  });

  it('returns the Problem Details message for an HTTP failure', async () => {
    const outcomePromise = gateway.get();

    const request = httpTestingController.expectOne('/api/analysis/weight');
    request.flush(
      {
        type: 'about:blank',
        title: 'Internal Server Error',
        status: 500,
        detail: 'Analysis is unavailable',
      },
      {
        status: 500,
        statusText: 'Internal Server Error',
      },
    );

    await expect(outcomePromise).resolves.toEqual({
      kind: 'failed',
      outcomeData: {
        errorMessage: 'Analysis is unavailable',
      },
    });
  });

  it('rejects a measurement without a numeric day number', async () => {
    const outcomePromise = gateway.get();

    const request = httpTestingController.expectOne('/api/analysis/weight');
    request.flush({
      timelineStartDate: '2026-09-20',
      range: {
        startDate: '2026-09-20',
        endDate: '2026-09-25',
      },
      weightMeasurements: [
        {
          date: '2026-09-20',
          dayNumber: '1',
          weightInKg: 82.1,
        },
      ],
      rollingAverages: [],
    });

    await expect(outcomePromise).resolves.toEqual({
      kind: 'failed',
      outcomeData: {
        errorMessage: 'The backend returned an invalid response.',
      },
    });
  });
});
