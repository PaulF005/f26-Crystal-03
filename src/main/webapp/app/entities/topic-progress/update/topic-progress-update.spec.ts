import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { TopicService } from 'app/entities/topic/service/topic.service';
import { ITopic } from 'app/entities/topic/topic.model';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { TopicProgressService } from '../service/topic-progress.service';
import { ITopicProgress } from '../topic-progress.model';

import { TopicProgressFormService } from './topic-progress-form.service';
import { TopicProgressUpdate } from './topic-progress-update';

describe('TopicProgress Management Update Component', () => {
  let comp: TopicProgressUpdate;
  let fixture: ComponentFixture<TopicProgressUpdate>;
  let activatedRoute: ActivatedRoute;
  let topicProgressFormService: TopicProgressFormService;
  let topicProgressService: TopicProgressService;
  let topicService: TopicService;
  let userProfileService: UserProfileService;

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

    fixture = TestBed.createComponent(TopicProgressUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    topicProgressFormService = TestBed.inject(TopicProgressFormService);
    topicProgressService = TestBed.inject(TopicProgressService);
    topicService = TestBed.inject(TopicService);
    userProfileService = TestBed.inject(UserProfileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Topic query and add missing value', () => {
      const topicProgress: ITopicProgress = { id: 31262 };
      const topic: ITopic = { id: 29581 };
      topicProgress.topic = topic;

      const topicCollection: ITopic[] = [{ id: 29581 }];
      vitest.spyOn(topicService, 'query').mockReturnValue(of(new HttpResponse({ body: topicCollection })));
      const additionalTopics = [topic];
      const expectedCollection: ITopic[] = [...additionalTopics, ...topicCollection];
      vitest.spyOn(topicService, 'addTopicToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ topicProgress });
      comp.ngOnInit();

      expect(topicService.query).toHaveBeenCalled();
      expect(topicService.addTopicToCollectionIfMissing).toHaveBeenCalledWith(
        topicCollection,
        ...additionalTopics.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.topicsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call UserProfile query and add missing value', () => {
      const topicProgress: ITopicProgress = { id: 31262 };
      const user: IUserProfile = { id: 22058 };
      topicProgress.user = user;
      const userProfile: IUserProfile = { id: 22058 };
      topicProgress.userProfile = userProfile;

      const userProfileCollection: IUserProfile[] = [{ id: 22058 }];
      vitest.spyOn(userProfileService, 'query').mockReturnValue(of(new HttpResponse({ body: userProfileCollection })));
      const additionalUserProfiles = [user, userProfile];
      const expectedCollection: IUserProfile[] = [...additionalUserProfiles, ...userProfileCollection];
      vitest.spyOn(userProfileService, 'addUserProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ topicProgress });
      comp.ngOnInit();

      expect(userProfileService.query).toHaveBeenCalled();
      expect(userProfileService.addUserProfileToCollectionIfMissing).toHaveBeenCalledWith(
        userProfileCollection,
        ...additionalUserProfiles.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.userProfilesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const topicProgress: ITopicProgress = { id: 31262 };
      const topic: ITopic = { id: 29581 };
      topicProgress.topic = topic;
      const user: IUserProfile = { id: 22058 };
      topicProgress.user = user;
      const userProfile: IUserProfile = { id: 22058 };
      topicProgress.userProfile = userProfile;

      activatedRoute.data = of({ topicProgress });
      comp.ngOnInit();

      expect(comp.topicsSharedCollection()).toContainEqual(topic);
      expect(comp.userProfilesSharedCollection()).toContainEqual(user);
      expect(comp.userProfilesSharedCollection()).toContainEqual(userProfile);
      expect(comp.topicProgress).toEqual(topicProgress);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITopicProgress>();
      const topicProgress = { id: 18198 };
      vitest.spyOn(topicProgressFormService, 'getTopicProgress').mockReturnValue(topicProgress);
      vitest.spyOn(topicProgressService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ topicProgress });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(topicProgress);
      saveSubject.complete();

      // THEN
      expect(topicProgressFormService.getTopicProgress).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(topicProgressService.update).toHaveBeenCalledWith(expect.objectContaining(topicProgress));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITopicProgress>();
      const topicProgress = { id: 18198 };
      vitest.spyOn(topicProgressFormService, 'getTopicProgress').mockReturnValue({ id: null });
      vitest.spyOn(topicProgressService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ topicProgress: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(topicProgress);
      saveSubject.complete();

      // THEN
      expect(topicProgressFormService.getTopicProgress).toHaveBeenCalled();
      expect(topicProgressService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ITopicProgress>();
      const topicProgress = { id: 18198 };
      vitest.spyOn(topicProgressService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ topicProgress });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(topicProgressService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTopic', () => {
      it('should forward to topicService', () => {
        const entity = { id: 29581 };
        const entity2 = { id: 14122 };
        vitest.spyOn(topicService, 'compareTopic');
        comp.compareTopic(entity, entity2);
        expect(topicService.compareTopic).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareUserProfile', () => {
      it('should forward to userProfileService', () => {
        const entity = { id: 22058 };
        const entity2 = { id: 9009 };
        vitest.spyOn(userProfileService, 'compareUserProfile');
        comp.compareUserProfile(entity, entity2);
        expect(userProfileService.compareUserProfile).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
