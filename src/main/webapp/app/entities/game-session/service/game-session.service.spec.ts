import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IGameSession } from '../game-session.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../game-session.test-samples';

import { GameSessionService, RestGameSession } from './game-session.service';

const requireRestSample: RestGameSession = {
  ...sampleWithRequiredData,
  startedAt: sampleWithRequiredData.startedAt?.toJSON(),
  completedAt: sampleWithRequiredData.completedAt?.toJSON(),
};

describe('GameSession Service', () => {
  let service: GameSessionService;
  let httpMock: HttpTestingController;
  let expectedResult: IGameSession | IGameSession[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(GameSessionService);
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

    it('should create a GameSession', () => {
      const gameSession = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(gameSession).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a GameSession', () => {
      const gameSession = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(gameSession).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a GameSession', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of GameSession', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a GameSession', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addGameSessionToCollectionIfMissing', () => {
      it('should add a GameSession to an empty array', () => {
        const gameSession: IGameSession = sampleWithRequiredData;
        expectedResult = service.addGameSessionToCollectionIfMissing([], gameSession);
        expect(expectedResult).toEqual([gameSession]);
      });

      it('should not add a GameSession to an array that contains it', () => {
        const gameSession: IGameSession = sampleWithRequiredData;
        const gameSessionCollection: IGameSession[] = [
          {
            ...gameSession,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addGameSessionToCollectionIfMissing(gameSessionCollection, gameSession);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a GameSession to an array that doesn't contain it", () => {
        const gameSession: IGameSession = sampleWithRequiredData;
        const gameSessionCollection: IGameSession[] = [sampleWithPartialData];
        expectedResult = service.addGameSessionToCollectionIfMissing(gameSessionCollection, gameSession);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(gameSession);
      });

      it('should add only unique GameSession to an array', () => {
        const gameSessionArray: IGameSession[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const gameSessionCollection: IGameSession[] = [sampleWithRequiredData];
        expectedResult = service.addGameSessionToCollectionIfMissing(gameSessionCollection, ...gameSessionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const gameSession: IGameSession = sampleWithRequiredData;
        const gameSession2: IGameSession = sampleWithPartialData;
        expectedResult = service.addGameSessionToCollectionIfMissing([], gameSession, gameSession2);
        expect(expectedResult).toEqual([gameSession, gameSession2]);
      });

      it('should accept null and undefined values', () => {
        const gameSession: IGameSession = sampleWithRequiredData;
        expectedResult = service.addGameSessionToCollectionIfMissing([], null, gameSession, undefined);
        expect(expectedResult).toEqual([gameSession]);
      });

      it('should return initial array if no GameSession is added', () => {
        const gameSessionCollection: IGameSession[] = [sampleWithRequiredData];
        expectedResult = service.addGameSessionToCollectionIfMissing(gameSessionCollection, undefined, null);
        expect(expectedResult).toEqual(gameSessionCollection);
      });
    });

    describe('compareGameSession', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareGameSession(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 30007 };
        const entity2 = null;

        const compareResult1 = service.compareGameSession(entity1, entity2);
        const compareResult2 = service.compareGameSession(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 30007 };
        const entity2 = { id: 5692 };

        const compareResult1 = service.compareGameSession(entity1, entity2);
        const compareResult2 = service.compareGameSession(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 30007 };
        const entity2 = { id: 30007 };

        const compareResult1 = service.compareGameSession(entity1, entity2);
        const compareResult2 = service.compareGameSession(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
