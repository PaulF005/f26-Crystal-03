import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ITopicProgress } from '../topic-progress.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../topic-progress.test-samples';

import { RestTopicProgress, TopicProgressService } from './topic-progress.service';

const requireRestSample: RestTopicProgress = {
  ...sampleWithRequiredData,
  lastPracticedAt: sampleWithRequiredData.lastPracticedAt?.toJSON(),
};

describe('TopicProgress Service', () => {
  let service: TopicProgressService;
  let httpMock: HttpTestingController;
  let expectedResult: ITopicProgress | ITopicProgress[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TopicProgressService);
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

    it('should create a TopicProgress', () => {
      const topicProgress = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(topicProgress).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TopicProgress', () => {
      const topicProgress = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(topicProgress).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TopicProgress', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TopicProgress', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TopicProgress', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addTopicProgressToCollectionIfMissing', () => {
      it('should add a TopicProgress to an empty array', () => {
        const topicProgress: ITopicProgress = sampleWithRequiredData;
        expectedResult = service.addTopicProgressToCollectionIfMissing([], topicProgress);
        expect(expectedResult).toEqual([topicProgress]);
      });

      it('should not add a TopicProgress to an array that contains it', () => {
        const topicProgress: ITopicProgress = sampleWithRequiredData;
        const topicProgressCollection: ITopicProgress[] = [
          {
            ...topicProgress,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTopicProgressToCollectionIfMissing(topicProgressCollection, topicProgress);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TopicProgress to an array that doesn't contain it", () => {
        const topicProgress: ITopicProgress = sampleWithRequiredData;
        const topicProgressCollection: ITopicProgress[] = [sampleWithPartialData];
        expectedResult = service.addTopicProgressToCollectionIfMissing(topicProgressCollection, topicProgress);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(topicProgress);
      });

      it('should add only unique TopicProgress to an array', () => {
        const topicProgressArray: ITopicProgress[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const topicProgressCollection: ITopicProgress[] = [sampleWithRequiredData];
        expectedResult = service.addTopicProgressToCollectionIfMissing(topicProgressCollection, ...topicProgressArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const topicProgress: ITopicProgress = sampleWithRequiredData;
        const topicProgress2: ITopicProgress = sampleWithPartialData;
        expectedResult = service.addTopicProgressToCollectionIfMissing([], topicProgress, topicProgress2);
        expect(expectedResult).toEqual([topicProgress, topicProgress2]);
      });

      it('should accept null and undefined values', () => {
        const topicProgress: ITopicProgress = sampleWithRequiredData;
        expectedResult = service.addTopicProgressToCollectionIfMissing([], null, topicProgress, undefined);
        expect(expectedResult).toEqual([topicProgress]);
      });

      it('should return initial array if no TopicProgress is added', () => {
        const topicProgressCollection: ITopicProgress[] = [sampleWithRequiredData];
        expectedResult = service.addTopicProgressToCollectionIfMissing(topicProgressCollection, undefined, null);
        expect(expectedResult).toEqual(topicProgressCollection);
      });
    });

    describe('compareTopicProgress', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTopicProgress(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 18198 };
        const entity2 = null;

        const compareResult1 = service.compareTopicProgress(entity1, entity2);
        const compareResult2 = service.compareTopicProgress(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 18198 };
        const entity2 = { id: 31262 };

        const compareResult1 = service.compareTopicProgress(entity1, entity2);
        const compareResult2 = service.compareTopicProgress(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 18198 };
        const entity2 = { id: 18198 };

        const compareResult1 = service.compareTopicProgress(entity1, entity2);
        const compareResult2 = service.compareTopicProgress(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
