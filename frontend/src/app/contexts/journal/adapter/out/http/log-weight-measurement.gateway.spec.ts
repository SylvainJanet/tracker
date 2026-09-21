import { HttpClient, provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { LogWeightMeasurementGateway } from './log-wright-measurement.gateway';
import type {
  LogWeightMeasurementInstruction,
  LogWeightMeasurementOutcome,
} from '../../../application/port/out/log-weight-measurement.store';

describe('LogWeightMeasurementGateway', () => {
  let gateway: LogWeightMeasurementGateway;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    gateway = new LogWeightMeasurementGateway(TestBed.inject(HttpClient));

    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
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
