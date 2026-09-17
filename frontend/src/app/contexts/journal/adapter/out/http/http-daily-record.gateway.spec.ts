import { HttpClient, provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';

import { HttpDailyRecordGateway } from './http-daily-record.gateway';
import { JournalDailyRecordUnexpectedGatewayError } from './errors/journal-daily-record-unexpected-gateway.error';
import { dailyRecord } from '../../../domain/daily-record';

describe('HttpDailyRecordGateway', () => {
  let gateway: HttpDailyRecordGateway;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    gateway = new HttpDailyRecordGateway(TestBed.inject(HttpClient));

    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  describe('create', () => {
    it('creates and maps a daily record', async () => {
      const instruction = dailyRecord('2026-08-27', 70);

      const outcomePromise = gateway.create(instruction);

      const request = httpTestingController.expectOne('/api/daily-records');

      expect(request.request.method).toBe('POST');
      expect(request.request.body).toEqual({ date: '2026-08-27', weight: 70 });

      request.flush({
        date: '2026-08-27',
        weight: 70,
      });

      await expect(outcomePromise).resolves.toEqual({
        kind: 'created',
        outcomeData: dailyRecord('2026-08-27', 70),
      });
    });

    it('maps HTTP 409 to already-exists', async () => {
      const instruction = dailyRecord('2026-08-27', 70);

      const outcomePromise = gateway.create(instruction);

      const request = httpTestingController.expectOne('/api/daily-records');

      request.flush(
        {
          title: 'Daily record already exists',
          status: 409,
        },
        {
          status: 409,
          statusText: 'Conflict',
        },
      );

      await expect(outcomePromise).resolves.toEqual({
        kind: 'already-exists',
      });
    });

    it('rejects an invalid backend response', async () => {
      const instruction = dailyRecord('2026-08-27', 70);

      const outcomePromise = gateway.create(instruction);

      const assertion = expect(outcomePromise).rejects.toBeInstanceOf(
        JournalDailyRecordUnexpectedGatewayError,
      );

      const request = httpTestingController.expectOne('/api/daily-records');

      request.flush({
        unknownField: 'unexpected-value',
      });

      await assertion;
    });
  });
});
