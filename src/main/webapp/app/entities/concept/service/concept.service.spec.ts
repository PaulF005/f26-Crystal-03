import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IConcept } from '../concept.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../concept.test-samples';

import { ConceptService } from './concept.service';

const requireRestSample: IConcept = {
  ...sampleWithRequiredData,
};

describe('Concept Service', () => {
  let service: ConceptService;
  let httpMock: HttpTestingController;
  let expectedResult: IConcept | IConcept[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ConceptService);
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

    it('should create a Concept', () => {
      const concept = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(concept).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Concept', () => {
      const concept = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(concept).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Concept', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Concept', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Concept', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addConceptToCollectionIfMissing', () => {
      it('should add a Concept to an empty array', () => {
        const concept: IConcept = sampleWithRequiredData;
        expectedResult = service.addConceptToCollectionIfMissing([], concept);
        expect(expectedResult).toEqual([concept]);
      });

      it('should not add a Concept to an array that contains it', () => {
        const concept: IConcept = sampleWithRequiredData;
        const conceptCollection: IConcept[] = [
          {
            ...concept,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addConceptToCollectionIfMissing(conceptCollection, concept);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Concept to an array that doesn't contain it", () => {
        const concept: IConcept = sampleWithRequiredData;
        const conceptCollection: IConcept[] = [sampleWithPartialData];
        expectedResult = service.addConceptToCollectionIfMissing(conceptCollection, concept);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(concept);
      });

      it('should add only unique Concept to an array', () => {
        const conceptArray: IConcept[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const conceptCollection: IConcept[] = [sampleWithRequiredData];
        expectedResult = service.addConceptToCollectionIfMissing(conceptCollection, ...conceptArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const concept: IConcept = sampleWithRequiredData;
        const concept2: IConcept = sampleWithPartialData;
        expectedResult = service.addConceptToCollectionIfMissing([], concept, concept2);
        expect(expectedResult).toEqual([concept, concept2]);
      });

      it('should accept null and undefined values', () => {
        const concept: IConcept = sampleWithRequiredData;
        expectedResult = service.addConceptToCollectionIfMissing([], null, concept, undefined);
        expect(expectedResult).toEqual([concept]);
      });

      it('should return initial array if no Concept is added', () => {
        const conceptCollection: IConcept[] = [sampleWithRequiredData];
        expectedResult = service.addConceptToCollectionIfMissing(conceptCollection, undefined, null);
        expect(expectedResult).toEqual(conceptCollection);
      });
    });

    describe('compareConcept', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareConcept(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 29097 };
        const entity2 = null;

        const compareResult1 = service.compareConcept(entity1, entity2);
        const compareResult2 = service.compareConcept(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 29097 };
        const entity2 = { id: 14426 };

        const compareResult1 = service.compareConcept(entity1, entity2);
        const compareResult2 = service.compareConcept(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 29097 };
        const entity2 = { id: 29097 };

        const compareResult1 = service.compareConcept(entity1, entity2);
        const compareResult2 = service.compareConcept(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
