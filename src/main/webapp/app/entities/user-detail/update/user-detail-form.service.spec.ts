import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../user-detail.test-samples';

import { UserDetailFormService } from './user-detail-form.service';

describe('UserDetail Form Service', () => {
  let service: UserDetailFormService;

  beforeEach(() => {
    service = TestBed.inject(UserDetailFormService);
  });

  describe('Service methods', () => {
    describe('createUserDetailFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createUserDetailFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            username: expect.any(Object),
            email: expect.any(Object),
            dataUser: expect.any(Object),
          }),
        );
      });

      it('passing IUserDetail should create a new form with FormGroup', () => {
        const formGroup = service.createUserDetailFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            username: expect.any(Object),
            email: expect.any(Object),
            dataUser: expect.any(Object),
          }),
        );
      });
    });

    describe('getUserDetail', () => {
      it('should return NewUserDetail for default UserDetail initial value', () => {
        const formGroup = service.createUserDetailFormGroup(sampleWithNewData);

        const userDetail = service.getUserDetail(formGroup);

        expect(userDetail).toMatchObject(sampleWithNewData);
      });

      it('should return NewUserDetail for empty UserDetail initial value', () => {
        const formGroup = service.createUserDetailFormGroup();

        const userDetail = service.getUserDetail(formGroup);

        expect(userDetail).toMatchObject({});
      });

      it('should return IUserDetail', () => {
        const formGroup = service.createUserDetailFormGroup(sampleWithRequiredData);

        const userDetail = service.getUserDetail(formGroup);

        expect(userDetail).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IUserDetail should not enable id FormControl', () => {
        const formGroup = service.createUserDetailFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewUserDetail should disable id FormControl', () => {
        const formGroup = service.createUserDetailFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
