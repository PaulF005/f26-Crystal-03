import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { UserDetailService } from 'app/entities/user-detail/service/user-detail.service';
import { IUserDetail } from 'app/entities/user-detail/user-detail.model';
import { IProgress } from '../progress.model';
import { ProgressService } from '../service/progress.service';

import { ProgressFormService } from './progress-form.service';
import { ProgressUpdate } from './progress-update';

describe('Progress Management Update Component', () => {
  let comp: ProgressUpdate;
  let fixture: ComponentFixture<ProgressUpdate>;
  let activatedRoute: ActivatedRoute;
  let progressFormService: ProgressFormService;
  let progressService: ProgressService;
  let userDetailService: UserDetailService;

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

    fixture = TestBed.createComponent(ProgressUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    progressFormService = TestBed.inject(ProgressFormService);
    progressService = TestBed.inject(ProgressService);
    userDetailService = TestBed.inject(UserDetailService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call UserDetail query and add missing value', () => {
      const progress: IProgress = { id: 5829 };
      const user: IUserDetail = { id: 9537 };
      progress.user = user;

      const userDetailCollection: IUserDetail[] = [{ id: 9537 }];
      vitest.spyOn(userDetailService, 'query').mockReturnValue(of(new HttpResponse({ body: userDetailCollection })));
      const additionalUserDetails = [user];
      const expectedCollection: IUserDetail[] = [...additionalUserDetails, ...userDetailCollection];
      vitest.spyOn(userDetailService, 'addUserDetailToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ progress });
      comp.ngOnInit();

      expect(userDetailService.query).toHaveBeenCalled();
      expect(userDetailService.addUserDetailToCollectionIfMissing).toHaveBeenCalledWith(
        userDetailCollection,
        ...additionalUserDetails.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.userDetailsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const progress: IProgress = { id: 5829 };
      const user: IUserDetail = { id: 9537 };
      progress.user = user;

      activatedRoute.data = of({ progress });
      comp.ngOnInit();

      expect(comp.userDetailsSharedCollection()).toContainEqual(user);
      expect(comp.progress).toEqual(progress);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IProgress>();
      const progress = { id: 9991 };
      vitest.spyOn(progressFormService, 'getProgress').mockReturnValue(progress);
      vitest.spyOn(progressService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ progress });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(progress);
      saveSubject.complete();

      // THEN
      expect(progressFormService.getProgress).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(progressService.update).toHaveBeenCalledWith(expect.objectContaining(progress));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IProgress>();
      const progress = { id: 9991 };
      vitest.spyOn(progressFormService, 'getProgress').mockReturnValue({ id: null });
      vitest.spyOn(progressService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ progress: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(progress);
      saveSubject.complete();

      // THEN
      expect(progressFormService.getProgress).toHaveBeenCalled();
      expect(progressService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IProgress>();
      const progress = { id: 9991 };
      vitest.spyOn(progressService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ progress });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(progressService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUserDetail', () => {
      it('should forward to userDetailService', () => {
        const entity = { id: 9537 };
        const entity2 = { id: 23168 };
        vitest.spyOn(userDetailService, 'compareUserDetail');
        comp.compareUserDetail(entity, entity2);
        expect(userDetailService.compareUserDetail).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
