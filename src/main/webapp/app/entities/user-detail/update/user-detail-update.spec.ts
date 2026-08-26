import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { UserDetailService } from '../service/user-detail.service';
import { IUserDetail } from '../user-detail.model';

import { UserDetailFormService } from './user-detail-form.service';
import { UserDetailUpdate } from './user-detail-update';

describe('UserDetail Management Update Component', () => {
  let comp: UserDetailUpdate;
  let fixture: ComponentFixture<UserDetailUpdate>;
  let activatedRoute: ActivatedRoute;
  let userDetailFormService: UserDetailFormService;
  let userDetailService: UserDetailService;
  let userService: UserService;

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

    fixture = TestBed.createComponent(UserDetailUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    userDetailFormService = TestBed.inject(UserDetailFormService);
    userDetailService = TestBed.inject(UserDetailService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const userDetail: IUserDetail = { id: 23168 };
      const dataUser: IUser = { id: 3944 };
      userDetail.dataUser = dataUser;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [dataUser];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userDetail });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const userDetail: IUserDetail = { id: 23168 };
      const dataUser: IUser = { id: 3944 };
      userDetail.dataUser = dataUser;

      activatedRoute.data = of({ userDetail });
      comp.ngOnInit();

      expect(comp.usersSharedCollection()).toContainEqual(dataUser);
      expect(comp.userDetail).toEqual(userDetail);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IUserDetail>();
      const userDetail = { id: 9537 };
      vitest.spyOn(userDetailFormService, 'getUserDetail').mockReturnValue(userDetail);
      vitest.spyOn(userDetailService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userDetail });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(userDetail);
      saveSubject.complete();

      // THEN
      expect(userDetailFormService.getUserDetail).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(userDetailService.update).toHaveBeenCalledWith(expect.objectContaining(userDetail));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IUserDetail>();
      const userDetail = { id: 9537 };
      vitest.spyOn(userDetailFormService, 'getUserDetail').mockReturnValue({ id: null });
      vitest.spyOn(userDetailService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userDetail: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(userDetail);
      saveSubject.complete();

      // THEN
      expect(userDetailFormService.getUserDetail).toHaveBeenCalled();
      expect(userDetailService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IUserDetail>();
      const userDetail = { id: 9537 };
      vitest.spyOn(userDetailService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userDetail });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(userDetailService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        vitest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
