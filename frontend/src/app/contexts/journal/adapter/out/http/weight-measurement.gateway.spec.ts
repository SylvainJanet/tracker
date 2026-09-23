import { HttpClient, provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { WeightMeasurementGateway } from './weight-measurement.gateway';
import type {
  GetWeightMeasurementByDateCriteria,
  GetWeightMeasurementByDateOutcome,
  LogWeightMeasurementInstruction,
  LogWeightMeasurementOutcome,
} from '../../../application/port/out/weight-measurement.store';

describe('WeightMeasurementGateway', () => {
  let gateway: WeightMeasurementGateway;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    gateway = new WeightMeasurementGateway(TestBed.inject(HttpClient));

    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  describe('getByDate', () => {
    it('gets a weight measurement by date', async () => {
      const date = '2026-08-27';
      const weightInKg = 70;

      const criteria: GetWeightMeasurementByDateCriteria = {
        date,
      };
      const expectedOutcome: GetWeightMeasurementByDateOutcome = {
        kind: 'found',
        outcomeData: {
          date,
          weightInKg,
        },
      };

      const outcomePromise = gateway.getByDate(criteria);

      const request = httpTestingController.expectOne('/api/journal/weight-measurement/' + date);
      expect(request.request.method).toBe('GET');

      request.flush({
        date,
        weightInKg,
      });

      await expect(outcomePromise).resolves.toEqual(expectedOutcome);
    });

    it('does not find a missing weight measurement', async () => {
      const date = '2026-08-27';

      const outcomePromise = gateway.getByDate({ date });

      const request = httpTestingController.expectOne('/api/journal/weight-measurement/' + date);

      request.flush(
        {
          type: 'about:blank',
          title: 'Not Found',
          status: 404,
          detail: 'No weight measurement was found for date ' + date,
        },
        { status: 404, statusText: 'Not Found' },
      );

      await expect(outcomePromise).resolves.toEqual({
        kind: 'not-found',
      });
    });

    it.each([
      null,
      'not an object',
      [],
      {},
      { date: '2026-08-27' },
      { weightInKg: 70 },
      { date: 42, weightInKg: 70 },
      { date: '2026-08-27', weightInKg: '70' },
      { date: '2026-08-27', weightInKg: Number.NaN },
      { date: '2026-08-27', weightInKg: Number.POSITIVE_INFINITY },
    ])('rejects a malformed successful response', async (response) => {
      const date = '2026-08-27';

      const outcomePromise = gateway.getByDate({ date });

      const request = httpTestingController.expectOne('/api/journal/weight-measurement/' + date);
      request.flush(response);

      await expect(outcomePromise).resolves.toEqual({
        kind: 'failed',
        outcomeData: {
          errorMessage: 'The backend returned an invalid response.',
        },
      });
    });

    it('rejects a measurement returned for a different date', async () => {
      const requestedDate = '2026-08-27';

      const outcomePromise = gateway.getByDate({
        date: requestedDate,
      });

      const request = httpTestingController.expectOne(
        '/api/journal/weight-measurement/' + requestedDate,
      );
      request.flush({
        date: '2026-08-26',
        weightInKg: 70,
      });

      await expect(outcomePromise).resolves.toEqual({
        kind: 'failed',
        outcomeData: {
          errorMessage: 'The backend returned a weight measurement for a different date.',
        },
      });
    });

    it('returns the Problem Details message for a non-404 error', async () => {
      const date = '2026-08-27';

      const outcomePromise = gateway.getByDate({ date });

      const request = httpTestingController.expectOne('/api/journal/weight-measurement/' + date);
      request.flush(
        {
          type: 'about:blank',
          title: 'Internal Server Error',
          status: 500,
          detail: 'Database unavailable',
        },
        { status: 500, statusText: 'Internal Server Error' },
      );

      await expect(outcomePromise).resolves.toEqual({
        kind: 'failed',
        outcomeData: {
          errorMessage: 'Database unavailable',
        },
      });
    });
  });

  describe('log', () => {
    it('log weight measurement', async () => {
      const date = '2026-08-27';
      const weightInKg = 70;

      const instruction: LogWeightMeasurementInstruction = {
        date,
        weightInKg,
      };
      const expectedOutcome: LogWeightMeasurementOutcome = {
        kind: 'logged',
        outcomeData: {
          date,
          weightInKg,
        },
      };

      const outcomePromise = gateway.log(instruction);

      const request = httpTestingController.expectOne('/api/journal/weight-measurement');
      expect(request.request.method).toBe('POST');
      expect(request.request.body).toEqual({ date, weightInKg });

      request.flush({
        date,
        weightInKg,
      });

      await expect(outcomePromise).resolves.toEqual(expectedOutcome);
    });

    it.each([
      null,
      'not an object',
      [],
      {},
      { date: '2026-08-27' },
      { weightInKg: 70 },
      { date: 42, weightInKg: 70 },
      { date: '2026-08-27', weightInKg: '70' },
      { date: '2026-08-27', weightInKg: Number.NaN },
      { date: '2026-08-27', weightInKg: Number.POSITIVE_INFINITY },
    ])('returns a failure for a malformed successful response', async (response) => {
      const instruction: LogWeightMeasurementInstruction = {
        date: '2026-08-27',
        weightInKg: 70,
      };

      const outcomePromise = gateway.log(instruction);

      const request = httpTestingController.expectOne('/api/journal/weight-measurement');
      request.flush(response);

      await expect(outcomePromise).resolves.toEqual({
        kind: 'failed',
        outcomeData: {
          errorMessage: 'The backend returned an invalid response.',
        },
      });
    });

    it('returns a failure when the backend returns a different date', async () => {
      const instruction: LogWeightMeasurementInstruction = {
        date: '2026-08-27',
        weightInKg: 70,
      };

      const outcomePromise = gateway.log(instruction);

      const request = httpTestingController.expectOne('/api/journal/weight-measurement');
      request.flush({
        date: '2026-08-26',
        weightInKg: 70,
      });

      await expect(outcomePromise).resolves.toEqual({
        kind: 'failed',
        outcomeData: {
          errorMessage: 'The backend returned a weight measurement for a different date.',
        },
      });
    });

    it('returns the Problem Details message for an HTTP error', async () => {
      const instruction: LogWeightMeasurementInstruction = {
        date: '2026-08-27',
        weightInKg: 70,
      };

      const outcomePromise = gateway.log(instruction);

      const request = httpTestingController.expectOne('/api/journal/weight-measurement');
      request.flush(
        {
          type: 'about:blank',
          title: 'Internal Server Error',
          status: 500,
          detail: 'Database unavailable',
        },
        { status: 500, statusText: 'Internal Server Error' },
      );

      await expect(outcomePromise).resolves.toEqual({
        kind: 'failed',
        outcomeData: {
          errorMessage: 'Database unavailable',
        },
      });
    });

    it('rejects an error backend response', async () => {
      const date = '2026-08-27';
      const weightInKg = 70;

      const instruction: LogWeightMeasurementInstruction = {
        date,
        weightInKg,
      };
      const expectedOutcome: LogWeightMeasurementOutcome = {
        kind: 'failed',
        outcomeData: {
          errorMessage: 'An unexpected gateway error occurred.',
        },
      };

      const outcomePromise = gateway.log(instruction);

      const request = httpTestingController.expectOne('/api/journal/weight-measurement');

      request.flush(
        {
          unknownField: 'unexpected-value',
        },
        { status: 500, statusText: 'Internal Server Error' },
      );

      await expect(outcomePromise).resolves.toEqual(expectedOutcome);
    });
  });
});
