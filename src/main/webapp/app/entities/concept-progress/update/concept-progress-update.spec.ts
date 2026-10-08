import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IConcept } from 'app/entities/concept/concept.model';
import { ConceptService } from 'app/entities/concept/service/concept.service';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { IConceptProgress } from '../concept-progress.model';
import { ConceptProgressService } from '../service/concept-progress.service';

import { ConceptProgressFormService } from './concept-progress-form.service';
import { ConceptProgressUpdate } from './concept-progress-update';

describe('ConceptProgress Management Update Component', () => {
  let comp: ConceptProgressUpdate;
  let fixture: ComponentFixture<ConceptProgressUpdate>;
  let activatedRoute: ActivatedRoute;
  let conceptProgressFormService: ConceptProgressFormService;
  let conceptProgressService: ConceptProgressService;
  let userProfileService: UserProfileService;
  let conceptService: ConceptService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(ConceptProgressUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    conceptProgressFormService = TestBed.inject(ConceptProgressFormService);
    conceptProgressService = TestBed.inject(ConceptProgressService);
    userProfileService = TestBed.inject(UserProfileService);
    conceptService = TestBed.inject(ConceptService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call UserProfile query and add missing value', () => {
      const conceptProgress: IConceptProgress = { id: 24782 };
      const userProfile: IUserProfile = { id: 22058 };
      conceptProgress.userProfile = userProfile;

      const userProfileCollection: IUserProfile[] = [{ id: 22058 }];
      vitest.spyOn(userProfileService, 'query').mockReturnValue(of(new HttpResponse({ body: userProfileCollection })));
      const additionalUserProfiles = [userProfile];
      const expectedCollection: IUserProfile[] = [...additionalUserProfiles, ...userProfileCollection];
      vitest.spyOn(userProfileService, 'addUserProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ conceptProgress });
      comp.ngOnInit();

      expect(userProfileService.query).toHaveBeenCalled();
      expect(userProfileService.addUserProfileToCollectionIfMissing).toHaveBeenCalledWith(
        userProfileCollection,
        ...additionalUserProfiles.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.userProfilesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Concept query and add missing value', () => {
      const conceptProgress: IConceptProgress = { id: 24782 };
      const concept: IConcept = { id: 29097 };
      conceptProgress.concept = concept;

      const conceptCollection: IConcept[] = [{ id: 29097 }];
      vitest.spyOn(conceptService, 'query').mockReturnValue(of(new HttpResponse({ body: conceptCollection })));
      const additionalConcepts = [concept];
      const expectedCollection: IConcept[] = [...additionalConcepts, ...conceptCollection];
      vitest.spyOn(conceptService, 'addConceptToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ conceptProgress });
      comp.ngOnInit();

      expect(conceptService.query).toHaveBeenCalled();
      expect(conceptService.addConceptToCollectionIfMissing).toHaveBeenCalledWith(
        conceptCollection,
        ...additionalConcepts.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.conceptsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const conceptProgress: IConceptProgress = { id: 24782 };
      const userProfile: IUserProfile = { id: 22058 };
      conceptProgress.userProfile = userProfile;
      const concept: IConcept = { id: 29097 };
      conceptProgress.concept = concept;

      activatedRoute.data = of({ conceptProgress });
      comp.ngOnInit();

      expect(comp.userProfilesSharedCollection()).toContainEqual(userProfile);
      expect(comp.conceptsSharedCollection()).toContainEqual(concept);
      expect(comp.conceptProgress).toEqual(conceptProgress);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IConceptProgress>();
      const conceptProgress = { id: 29965 };
      vitest.spyOn(conceptProgressFormService, 'getConceptProgress').mockReturnValue(conceptProgress);
      vitest.spyOn(conceptProgressService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ conceptProgress });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(conceptProgress);
      saveSubject.complete();

      // THEN
      expect(conceptProgressFormService.getConceptProgress).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(conceptProgressService.update).toHaveBeenCalledWith(expect.objectContaining(conceptProgress));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IConceptProgress>();
      const conceptProgress = { id: 29965 };
      vitest.spyOn(conceptProgressFormService, 'getConceptProgress').mockReturnValue({ id: null });
      vitest.spyOn(conceptProgressService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ conceptProgress: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(conceptProgress);
      saveSubject.complete();

      // THEN
      expect(conceptProgressFormService.getConceptProgress).toHaveBeenCalled();
      expect(conceptProgressService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IConceptProgress>();
      const conceptProgress = { id: 29965 };
      vitest.spyOn(conceptProgressService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ conceptProgress });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(conceptProgressService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUserProfile', () => {
      it('should forward to userProfileService', () => {
        const entity = { id: 22058 };
        const entity2 = { id: 9009 };
        vitest.spyOn(userProfileService, 'compareUserProfile');
        comp.compareUserProfile(entity, entity2);
        expect(userProfileService.compareUserProfile).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareConcept', () => {
      it('should forward to conceptService', () => {
        const entity = { id: 29097 };
        const entity2 = { id: 14426 };
        vitest.spyOn(conceptService, 'compareConcept');
        comp.compareConcept(entity, entity2);
        expect(conceptService.compareConcept).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
