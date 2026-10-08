import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IAnswer } from '../answer.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../answer.test-samples';

import { AnswerService } from './answer.service';

const requireRestSample: IAnswer = {
  ...sampleWithRequiredData,
};

describe('Answer Service', () => {
  let service: AnswerService;
  let httpMock: HttpTestingController;
  let expectedResult: IAnswer | IAnswer[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(AnswerService);
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

    it('should create a Answer', () => {
      const answer = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(answer).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Answer', () => {
      const answer = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(answer).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Answer', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Answer', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Answer', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addAnswerToCollectionIfMissing', () => {
      it('should add a Answer to an empty array', () => {
        const answer: IAnswer = sampleWithRequiredData;
        expectedResult = service.addAnswerToCollectionIfMissing([], answer);
        expect(expectedResult).toEqual([answer]);
      });

      it('should not add a Answer to an array that contains it', () => {
        const answer: IAnswer = sampleWithRequiredData;
        const answerCollection: IAnswer[] = [
          {
            ...answer,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addAnswerToCollectionIfMissing(answerCollection, answer);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Answer to an array that doesn't contain it", () => {
        const answer: IAnswer = sampleWithRequiredData;
        const answerCollection: IAnswer[] = [sampleWithPartialData];
        expectedResult = service.addAnswerToCollectionIfMissing(answerCollection, answer);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(answer);
      });

      it('should add only unique Answer to an array', () => {
        const answerArray: IAnswer[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const answerCollection: IAnswer[] = [sampleWithRequiredData];
        expectedResult = service.addAnswerToCollectionIfMissing(answerCollection, ...answerArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const answer: IAnswer = sampleWithRequiredData;
        const answer2: IAnswer = sampleWithPartialData;
        expectedResult = service.addAnswerToCollectionIfMissing([], answer, answer2);
        expect(expectedResult).toEqual([answer, answer2]);
      });

      it('should accept null and undefined values', () => {
        const answer: IAnswer = sampleWithRequiredData;
        expectedResult = service.addAnswerToCollectionIfMissing([], null, answer, undefined);
        expect(expectedResult).toEqual([answer]);
      });

      it('should return initial array if no Answer is added', () => {
        const answerCollection: IAnswer[] = [sampleWithRequiredData];
        expectedResult = service.addAnswerToCollectionIfMissing(answerCollection, undefined, null);
        expect(expectedResult).toEqual(answerCollection);
      });
    });

    describe('compareAnswer', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareAnswer(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 19540 };
        const entity2 = null;

        const compareResult1 = service.compareAnswer(entity1, entity2);
        const compareResult2 = service.compareAnswer(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 19540 };
        const entity2 = { id: 25690 };

        const compareResult1 = service.compareAnswer(entity1, entity2);
        const compareResult2 = service.compareAnswer(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 19540 };
        const entity2 = { id: 19540 };

        const compareResult1 = service.compareAnswer(entity1, entity2);
        const compareResult2 = service.compareAnswer(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
