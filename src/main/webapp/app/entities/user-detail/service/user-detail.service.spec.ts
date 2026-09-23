import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IUserDetail } from '../user-detail.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../user-detail.test-samples';

import { UserDetailService } from './user-detail.service';

const requireRestSample: IUserDetail = {
  ...sampleWithRequiredData,
};

describe('UserDetail Service', () => {
  let service: UserDetailService;
  let httpMock: HttpTestingController;
  let expectedResult: IUserDetail | IUserDetail[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(UserDetailService);
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

    it('should create a UserDetail', () => {
      const userDetail = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(userDetail).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a UserDetail', () => {
      const userDetail = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(userDetail).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a UserDetail', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of UserDetail', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a UserDetail', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addUserDetailToCollectionIfMissing', () => {
      it('should add a UserDetail to an empty array', () => {
        const userDetail: IUserDetail = sampleWithRequiredData;
        expectedResult = service.addUserDetailToCollectionIfMissing([], userDetail);
        expect(expectedResult).toEqual([userDetail]);
      });

      it('should not add a UserDetail to an array that contains it', () => {
        const userDetail: IUserDetail = sampleWithRequiredData;
        const userDetailCollection: IUserDetail[] = [
          {
            ...userDetail,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addUserDetailToCollectionIfMissing(userDetailCollection, userDetail);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a UserDetail to an array that doesn't contain it", () => {
        const userDetail: IUserDetail = sampleWithRequiredData;
        const userDetailCollection: IUserDetail[] = [sampleWithPartialData];
        expectedResult = service.addUserDetailToCollectionIfMissing(userDetailCollection, userDetail);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userDetail);
      });

      it('should add only unique UserDetail to an array', () => {
        const userDetailArray: IUserDetail[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const userDetailCollection: IUserDetail[] = [sampleWithRequiredData];
        expectedResult = service.addUserDetailToCollectionIfMissing(userDetailCollection, ...userDetailArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const userDetail: IUserDetail = sampleWithRequiredData;
        const userDetail2: IUserDetail = sampleWithPartialData;
        expectedResult = service.addUserDetailToCollectionIfMissing([], userDetail, userDetail2);
        expect(expectedResult).toEqual([userDetail, userDetail2]);
      });

      it('should accept null and undefined values', () => {
        const userDetail: IUserDetail = sampleWithRequiredData;
        expectedResult = service.addUserDetailToCollectionIfMissing([], null, userDetail, undefined);
        expect(expectedResult).toEqual([userDetail]);
      });

      it('should return initial array if no UserDetail is added', () => {
        const userDetailCollection: IUserDetail[] = [sampleWithRequiredData];
        expectedResult = service.addUserDetailToCollectionIfMissing(userDetailCollection, undefined, null);
        expect(expectedResult).toEqual(userDetailCollection);
      });
    });

    describe('compareUserDetail', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareUserDetail(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 9537 };
        const entity2 = null;

        const compareResult1 = service.compareUserDetail(entity1, entity2);
        const compareResult2 = service.compareUserDetail(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 9537 };
        const entity2 = { id: 23168 };

        const compareResult1 = service.compareUserDetail(entity1, entity2);
        const compareResult2 = service.compareUserDetail(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 9537 };
        const entity2 = { id: 9537 };

        const compareResult1 = service.compareUserDetail(entity1, entity2);
        const compareResult2 = service.compareUserDetail(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
