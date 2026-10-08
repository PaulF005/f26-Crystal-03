import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ILegalContent } from '../legal-content.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../legal-content.test-samples';

import { LegalContentService } from './legal-content.service';

const requireRestSample: ILegalContent = {
  ...sampleWithRequiredData,
};

describe('LegalContent Service', () => {
  let service: LegalContentService;
  let httpMock: HttpTestingController;
  let expectedResult: ILegalContent | ILegalContent[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(LegalContentService);
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

    it('should create a LegalContent', () => {
      const legalContent = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(legalContent).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a LegalContent', () => {
      const legalContent = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(legalContent).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a LegalContent', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of LegalContent', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a LegalContent', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addLegalContentToCollectionIfMissing', () => {
      it('should add a LegalContent to an empty array', () => {
        const legalContent: ILegalContent = sampleWithRequiredData;
        expectedResult = service.addLegalContentToCollectionIfMissing([], legalContent);
        expect(expectedResult).toEqual([legalContent]);
      });

      it('should not add a LegalContent to an array that contains it', () => {
        const legalContent: ILegalContent = sampleWithRequiredData;
        const legalContentCollection: ILegalContent[] = [
          {
            ...legalContent,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addLegalContentToCollectionIfMissing(legalContentCollection, legalContent);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a LegalContent to an array that doesn't contain it", () => {
        const legalContent: ILegalContent = sampleWithRequiredData;
        const legalContentCollection: ILegalContent[] = [sampleWithPartialData];
        expectedResult = service.addLegalContentToCollectionIfMissing(legalContentCollection, legalContent);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(legalContent);
      });

      it('should add only unique LegalContent to an array', () => {
        const legalContentArray: ILegalContent[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const legalContentCollection: ILegalContent[] = [sampleWithRequiredData];
        expectedResult = service.addLegalContentToCollectionIfMissing(legalContentCollection, ...legalContentArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const legalContent: ILegalContent = sampleWithRequiredData;
        const legalContent2: ILegalContent = sampleWithPartialData;
        expectedResult = service.addLegalContentToCollectionIfMissing([], legalContent, legalContent2);
        expect(expectedResult).toEqual([legalContent, legalContent2]);
      });

      it('should accept null and undefined values', () => {
        const legalContent: ILegalContent = sampleWithRequiredData;
        expectedResult = service.addLegalContentToCollectionIfMissing([], null, legalContent, undefined);
        expect(expectedResult).toEqual([legalContent]);
      });

      it('should return initial array if no LegalContent is added', () => {
        const legalContentCollection: ILegalContent[] = [sampleWithRequiredData];
        expectedResult = service.addLegalContentToCollectionIfMissing(legalContentCollection, undefined, null);
        expect(expectedResult).toEqual(legalContentCollection);
      });
    });

    describe('compareLegalContent', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareLegalContent(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 7620 };
        const entity2 = null;

        const compareResult1 = service.compareLegalContent(entity1, entity2);
        const compareResult2 = service.compareLegalContent(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 7620 };
        const entity2 = { id: 29225 };

        const compareResult1 = service.compareLegalContent(entity1, entity2);
        const compareResult2 = service.compareLegalContent(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 7620 };
        const entity2 = { id: 7620 };

        const compareResult1 = service.compareLegalContent(entity1, entity2);
        const compareResult2 = service.compareLegalContent(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
