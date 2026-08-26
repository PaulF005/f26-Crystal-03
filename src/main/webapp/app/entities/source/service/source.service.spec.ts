import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ISource } from '../source.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../source.test-samples';

import { SourceService } from './source.service';

const requireRestSample: ISource = {
  ...sampleWithRequiredData,
};

describe('Source Service', () => {
  let service: SourceService;
  let httpMock: HttpTestingController;
  let expectedResult: ISource | ISource[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(SourceService);
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

    it('should create a Source', () => {
      const source = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(source).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Source', () => {
      const source = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(source).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Source', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Source', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Source', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addSourceToCollectionIfMissing', () => {
      it('should add a Source to an empty array', () => {
        const source: ISource = sampleWithRequiredData;
        expectedResult = service.addSourceToCollectionIfMissing([], source);
        expect(expectedResult).toEqual([source]);
      });

      it('should not add a Source to an array that contains it', () => {
        const source: ISource = sampleWithRequiredData;
        const sourceCollection: ISource[] = [
          {
            ...source,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSourceToCollectionIfMissing(sourceCollection, source);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Source to an array that doesn't contain it", () => {
        const source: ISource = sampleWithRequiredData;
        const sourceCollection: ISource[] = [sampleWithPartialData];
        expectedResult = service.addSourceToCollectionIfMissing(sourceCollection, source);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(source);
      });

      it('should add only unique Source to an array', () => {
        const sourceArray: ISource[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const sourceCollection: ISource[] = [sampleWithRequiredData];
        expectedResult = service.addSourceToCollectionIfMissing(sourceCollection, ...sourceArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const source: ISource = sampleWithRequiredData;
        const source2: ISource = sampleWithPartialData;
        expectedResult = service.addSourceToCollectionIfMissing([], source, source2);
        expect(expectedResult).toEqual([source, source2]);
      });

      it('should accept null and undefined values', () => {
        const source: ISource = sampleWithRequiredData;
        expectedResult = service.addSourceToCollectionIfMissing([], null, source, undefined);
        expect(expectedResult).toEqual([source]);
      });

      it('should return initial array if no Source is added', () => {
        const sourceCollection: ISource[] = [sampleWithRequiredData];
        expectedResult = service.addSourceToCollectionIfMissing(sourceCollection, undefined, null);
        expect(expectedResult).toEqual(sourceCollection);
      });
    });

    describe('compareSource', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSource(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 4722 };
        const entity2 = null;

        const compareResult1 = service.compareSource(entity1, entity2);
        const compareResult2 = service.compareSource(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 4722 };
        const entity2 = { id: 467 };

        const compareResult1 = service.compareSource(entity1, entity2);
        const compareResult2 = service.compareSource(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 4722 };
        const entity2 = { id: 4722 };

        const compareResult1 = service.compareSource(entity1, entity2);
        const compareResult2 = service.compareSource(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
