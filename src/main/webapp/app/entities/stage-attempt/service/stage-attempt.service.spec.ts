import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IStageAttempt } from '../stage-attempt.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../stage-attempt.test-samples';

import { RestStageAttempt, StageAttemptService } from './stage-attempt.service';

const requireRestSample: RestStageAttempt = {
  ...sampleWithRequiredData,
  answeredAt: sampleWithRequiredData.answeredAt?.toJSON(),
};

describe('StageAttempt Service', () => {
  let service: StageAttemptService;
  let httpMock: HttpTestingController;
  let expectedResult: IStageAttempt | IStageAttempt[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(StageAttemptService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a StageAttempt', () => {
      const stageAttempt = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(stageAttempt).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a StageAttempt', () => {
      const stageAttempt = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(stageAttempt).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a StageAttempt', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of StageAttempt', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a StageAttempt', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addStageAttemptToCollectionIfMissing', () => {
      it('should add a StageAttempt to an empty array', () => {
        const stageAttempt: IStageAttempt = sampleWithRequiredData;
        expectedResult = service.addStageAttemptToCollectionIfMissing([], stageAttempt);
        expect(expectedResult).toEqual([stageAttempt]);
      });

      it('should not add a StageAttempt to an array that contains it', () => {
        const stageAttempt: IStageAttempt = sampleWithRequiredData;
        const stageAttemptCollection: IStageAttempt[] = [
          {
            ...stageAttempt,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addStageAttemptToCollectionIfMissing(stageAttemptCollection, stageAttempt);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a StageAttempt to an array that doesn't contain it", () => {
        const stageAttempt: IStageAttempt = sampleWithRequiredData;
        const stageAttemptCollection: IStageAttempt[] = [sampleWithPartialData];
        expectedResult = service.addStageAttemptToCollectionIfMissing(stageAttemptCollection, stageAttempt);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(stageAttempt);
      });

      it('should add only unique StageAttempt to an array', () => {
        const stageAttemptArray: IStageAttempt[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const stageAttemptCollection: IStageAttempt[] = [sampleWithRequiredData];
        expectedResult = service.addStageAttemptToCollectionIfMissing(stageAttemptCollection, ...stageAttemptArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const stageAttempt: IStageAttempt = sampleWithRequiredData;
        const stageAttempt2: IStageAttempt = sampleWithPartialData;
        expectedResult = service.addStageAttemptToCollectionIfMissing([], stageAttempt, stageAttempt2);
        expect(expectedResult).toEqual([stageAttempt, stageAttempt2]);
      });

      it('should accept null and undefined values', () => {
        const stageAttempt: IStageAttempt = sampleWithRequiredData;
        expectedResult = service.addStageAttemptToCollectionIfMissing([], null, stageAttempt, undefined);
        expect(expectedResult).toEqual([stageAttempt]);
      });

      it('should return initial array if no StageAttempt is added', () => {
        const stageAttemptCollection: IStageAttempt[] = [sampleWithRequiredData];
        expectedResult = service.addStageAttemptToCollectionIfMissing(stageAttemptCollection, undefined, null);
        expect(expectedResult).toEqual(stageAttemptCollection);
      });
    });

    describe('compareStageAttempt', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareStageAttempt(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 23225 };
        const entity2 = null;

        const compareResult1 = service.compareStageAttempt(entity1, entity2);
        const compareResult2 = service.compareStageAttempt(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 23225 };
        const entity2 = { id: 30692 };

        const compareResult1 = service.compareStageAttempt(entity1, entity2);
        const compareResult2 = service.compareStageAttempt(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 23225 };
        const entity2 = { id: 23225 };

        const compareResult1 = service.compareStageAttempt(entity1, entity2);
        const compareResult2 = service.compareStageAttempt(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
