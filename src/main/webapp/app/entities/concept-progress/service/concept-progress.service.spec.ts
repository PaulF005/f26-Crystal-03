import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IConceptProgress } from '../concept-progress.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../concept-progress.test-samples';

import { ConceptProgressService, RestConceptProgress } from './concept-progress.service';

const requireRestSample: RestConceptProgress = {
  ...sampleWithRequiredData,
  lastPracticedAt: sampleWithRequiredData.lastPracticedAt?.toJSON(),
};

describe('ConceptProgress Service', () => {
  let service: ConceptProgressService;
  let httpMock: HttpTestingController;
  let expectedResult: IConceptProgress | IConceptProgress[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ConceptProgressService);
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

    it('should create a ConceptProgress', () => {
      const conceptProgress = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(conceptProgress).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ConceptProgress', () => {
      const conceptProgress = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(conceptProgress).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ConceptProgress', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ConceptProgress', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ConceptProgress', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addConceptProgressToCollectionIfMissing', () => {
      it('should add a ConceptProgress to an empty array', () => {
        const conceptProgress: IConceptProgress = sampleWithRequiredData;
        expectedResult = service.addConceptProgressToCollectionIfMissing([], conceptProgress);
        expect(expectedResult).toEqual([conceptProgress]);
      });

      it('should not add a ConceptProgress to an array that contains it', () => {
        const conceptProgress: IConceptProgress = sampleWithRequiredData;
        const conceptProgressCollection: IConceptProgress[] = [
          {
            ...conceptProgress,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addConceptProgressToCollectionIfMissing(conceptProgressCollection, conceptProgress);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ConceptProgress to an array that doesn't contain it", () => {
        const conceptProgress: IConceptProgress = sampleWithRequiredData;
        const conceptProgressCollection: IConceptProgress[] = [sampleWithPartialData];
        expectedResult = service.addConceptProgressToCollectionIfMissing(conceptProgressCollection, conceptProgress);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(conceptProgress);
      });

      it('should add only unique ConceptProgress to an array', () => {
        const conceptProgressArray: IConceptProgress[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const conceptProgressCollection: IConceptProgress[] = [sampleWithRequiredData];
        expectedResult = service.addConceptProgressToCollectionIfMissing(conceptProgressCollection, ...conceptProgressArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const conceptProgress: IConceptProgress = sampleWithRequiredData;
        const conceptProgress2: IConceptProgress = sampleWithPartialData;
        expectedResult = service.addConceptProgressToCollectionIfMissing([], conceptProgress, conceptProgress2);
        expect(expectedResult).toEqual([conceptProgress, conceptProgress2]);
      });

      it('should accept null and undefined values', () => {
        const conceptProgress: IConceptProgress = sampleWithRequiredData;
        expectedResult = service.addConceptProgressToCollectionIfMissing([], null, conceptProgress, undefined);
        expect(expectedResult).toEqual([conceptProgress]);
      });

      it('should return initial array if no ConceptProgress is added', () => {
        const conceptProgressCollection: IConceptProgress[] = [sampleWithRequiredData];
        expectedResult = service.addConceptProgressToCollectionIfMissing(conceptProgressCollection, undefined, null);
        expect(expectedResult).toEqual(conceptProgressCollection);
      });
    });

    describe('compareConceptProgress', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareConceptProgress(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 29965 };
        const entity2 = null;

        const compareResult1 = service.compareConceptProgress(entity1, entity2);
        const compareResult2 = service.compareConceptProgress(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 29965 };
        const entity2 = { id: 24782 };

        const compareResult1 = service.compareConceptProgress(entity1, entity2);
        const compareResult2 = service.compareConceptProgress(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 29965 };
        const entity2 = { id: 29965 };

        const compareResult1 = service.compareConceptProgress(entity1, entity2);
        const compareResult2 = service.compareConceptProgress(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
