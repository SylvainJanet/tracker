import { HttpClient, provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';

import { calendarDate } from '../../../domain/calendar-date';

import { HttpDailyRecordGateway } from './http-daily-record.gateway';
import { DailyRecordUnexpectedGatewayError } from './errors/daily-record-unexpected-gateway.error';

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

  describe('findByDate', () => {
    it('retrieves and maps a daily record', async () => {
      const criteria = calendarDate('2026-08-27');

      const outcomePromise = gateway.findByDate(criteria);

      const request = httpTestingController.expectOne('/api/daily-records/2026-08-27');

      expect(request.request.method).toBe('GET');

      request.flush({
        date: criteria,
        status: 'IN_PROGRESS',
      });

      await expect(outcomePromise).resolves.toEqual({
        kind: 'found',
        outcomeData: {
          date: criteria,
          status: 'IN_PROGRESS',
        },
      });
    });

    it('maps HTTP 404 to an absent record', async () => {
      const criteria = calendarDate('2026-08-27');

      const outcomePromise = gateway.findByDate(criteria);

      const request = httpTestingController.expectOne('/api/daily-records/2026-08-27');

      request.flush(
        {
          title: 'Daily record not found',
          status: 404,
        },
        {
          status: 404,
          statusText: 'Not Found',
        },
      );

      await expect(outcomePromise).resolves.toEqual({
        kind: 'not-found',
      });
    });

    it('rejects an invalid backend response', async () => {
      const criteria = calendarDate('2026-08-27');

      const outcomePromise = gateway.findByDate(criteria);

      const assertion = expect(outcomePromise).rejects.toBeInstanceOf(
        DailyRecordUnexpectedGatewayError,
      );

      const request = httpTestingController.expectOne('/api/daily-records/2026-08-27');

      request.flush({
        date: 'not-a-date',
        status: 'IN_PROGRESS',
      });

      await assertion;
    });
  });

  describe('create', () => {
    it('creates and maps a daily record', async () => {
      const instruction = calendarDate('2026-08-27');

      const outcomePromise = gateway.create(instruction);

      const request = httpTestingController.expectOne('/api/daily-records');

      expect(request.request.method).toBe('POST');
      expect(request.request.body).toEqual({
        date: instruction,
      });

      request.flush({
        date: instruction,
        status: 'IN_PROGRESS',
      });

      await expect(outcomePromise).resolves.toEqual({
        kind: 'created',
        outcomeData: {
          date: instruction,
          status: 'IN_PROGRESS',
        },
      });
    });

    it('maps HTTP 409 to already-exists', async () => {
      const instruction = calendarDate('2026-08-27');

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
      const instruction = calendarDate('2026-08-27');

      const outcomePromise = gateway.create(instruction);

      const assertion = expect(outcomePromise).rejects.toBeInstanceOf(
        DailyRecordUnexpectedGatewayError,
      );

      const request = httpTestingController.expectOne('/api/daily-records');

      request.flush({
        unknownField: 'unexpected-value',
      });

      await assertion;
    });
  });
});
