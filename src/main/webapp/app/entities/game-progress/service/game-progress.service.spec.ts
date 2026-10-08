import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IGameProgress } from '../game-progress.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../game-progress.test-samples';

import { GameProgressService, RestGameProgress } from './game-progress.service';

const requireRestSample: RestGameProgress = {
  ...sampleWithRequiredData,
  lastPlayedAt: sampleWithRequiredData.lastPlayedAt?.toJSON(),
};

describe('GameProgress Service', () => {
  let service: GameProgressService;
  let httpMock: HttpTestingController;
  let expectedResult: IGameProgress | IGameProgress[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(GameProgressService);
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

    it('should create a GameProgress', () => {
      const gameProgress = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(gameProgress).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a GameProgress', () => {
      const gameProgress = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(gameProgress).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a GameProgress', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of GameProgress', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a GameProgress', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addGameProgressToCollectionIfMissing', () => {
      it('should add a GameProgress to an empty array', () => {
        const gameProgress: IGameProgress = sampleWithRequiredData;
        expectedResult = service.addGameProgressToCollectionIfMissing([], gameProgress);
        expect(expectedResult).toEqual([gameProgress]);
      });

      it('should not add a GameProgress to an array that contains it', () => {
        const gameProgress: IGameProgress = sampleWithRequiredData;
        const gameProgressCollection: IGameProgress[] = [
          {
            ...gameProgress,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addGameProgressToCollectionIfMissing(gameProgressCollection, gameProgress);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a GameProgress to an array that doesn't contain it", () => {
        const gameProgress: IGameProgress = sampleWithRequiredData;
        const gameProgressCollection: IGameProgress[] = [sampleWithPartialData];
        expectedResult = service.addGameProgressToCollectionIfMissing(gameProgressCollection, gameProgress);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(gameProgress);
      });

      it('should add only unique GameProgress to an array', () => {
        const gameProgressArray: IGameProgress[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const gameProgressCollection: IGameProgress[] = [sampleWithRequiredData];
        expectedResult = service.addGameProgressToCollectionIfMissing(gameProgressCollection, ...gameProgressArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const gameProgress: IGameProgress = sampleWithRequiredData;
        const gameProgress2: IGameProgress = sampleWithPartialData;
        expectedResult = service.addGameProgressToCollectionIfMissing([], gameProgress, gameProgress2);
        expect(expectedResult).toEqual([gameProgress, gameProgress2]);
      });

      it('should accept null and undefined values', () => {
        const gameProgress: IGameProgress = sampleWithRequiredData;
        expectedResult = service.addGameProgressToCollectionIfMissing([], null, gameProgress, undefined);
        expect(expectedResult).toEqual([gameProgress]);
      });

      it('should return initial array if no GameProgress is added', () => {
        const gameProgressCollection: IGameProgress[] = [sampleWithRequiredData];
        expectedResult = service.addGameProgressToCollectionIfMissing(gameProgressCollection, undefined, null);
        expect(expectedResult).toEqual(gameProgressCollection);
      });
    });

    describe('compareGameProgress', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareGameProgress(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 24479 };
        const entity2 = null;

        const compareResult1 = service.compareGameProgress(entity1, entity2);
        const compareResult2 = service.compareGameProgress(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 24479 };
        const entity2 = { id: 2611 };

        const compareResult1 = service.compareGameProgress(entity1, entity2);
        const compareResult2 = service.compareGameProgress(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 24479 };
        const entity2 = { id: 24479 };

        const compareResult1 = service.compareGameProgress(entity1, entity2);
        const compareResult2 = service.compareGameProgress(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
