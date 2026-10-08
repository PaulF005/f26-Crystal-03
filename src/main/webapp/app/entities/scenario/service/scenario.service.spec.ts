import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IScenario } from '../scenario.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../scenario.test-samples';

import { ScenarioService } from './scenario.service';

const requireRestSample: IScenario = {
  ...sampleWithRequiredData,
};

describe('Scenario Service', () => {
  let service: ScenarioService;
  let httpMock: HttpTestingController;
  let expectedResult: IScenario | IScenario[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ScenarioService);
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

    it('should create a Scenario', () => {
      const scenario = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(scenario).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Scenario', () => {
      const scenario = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(scenario).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Scenario', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Scenario', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Scenario', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addScenarioToCollectionIfMissing', () => {
      it('should add a Scenario to an empty array', () => {
        const scenario: IScenario = sampleWithRequiredData;
        expectedResult = service.addScenarioToCollectionIfMissing([], scenario);
        expect(expectedResult).toEqual([scenario]);
      });

      it('should not add a Scenario to an array that contains it', () => {
        const scenario: IScenario = sampleWithRequiredData;
        const scenarioCollection: IScenario[] = [
          {
            ...scenario,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addScenarioToCollectionIfMissing(scenarioCollection, scenario);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Scenario to an array that doesn't contain it", () => {
        const scenario: IScenario = sampleWithRequiredData;
        const scenarioCollection: IScenario[] = [sampleWithPartialData];
        expectedResult = service.addScenarioToCollectionIfMissing(scenarioCollection, scenario);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(scenario);
      });

      it('should add only unique Scenario to an array', () => {
        const scenarioArray: IScenario[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const scenarioCollection: IScenario[] = [sampleWithRequiredData];
        expectedResult = service.addScenarioToCollectionIfMissing(scenarioCollection, ...scenarioArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const scenario: IScenario = sampleWithRequiredData;
        const scenario2: IScenario = sampleWithPartialData;
        expectedResult = service.addScenarioToCollectionIfMissing([], scenario, scenario2);
        expect(expectedResult).toEqual([scenario, scenario2]);
      });

      it('should accept null and undefined values', () => {
        const scenario: IScenario = sampleWithRequiredData;
        expectedResult = service.addScenarioToCollectionIfMissing([], null, scenario, undefined);
        expect(expectedResult).toEqual([scenario]);
      });

      it('should return initial array if no Scenario is added', () => {
        const scenarioCollection: IScenario[] = [sampleWithRequiredData];
        expectedResult = service.addScenarioToCollectionIfMissing(scenarioCollection, undefined, null);
        expect(expectedResult).toEqual(scenarioCollection);
      });
    });

    describe('compareScenario', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareScenario(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 10879 };
        const entity2 = null;

        const compareResult1 = service.compareScenario(entity1, entity2);
        const compareResult2 = service.compareScenario(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 10879 };
        const entity2 = { id: 10024 };

        const compareResult1 = service.compareScenario(entity1, entity2);
        const compareResult2 = service.compareScenario(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 10879 };
        const entity2 = { id: 10879 };

        const compareResult1 = service.compareScenario(entity1, entity2);
        const compareResult2 = service.compareScenario(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
