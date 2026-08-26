import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IModule } from '../module.model';
import { ModuleService } from '../service/module.service';

import { ModuleFormService } from './module-form.service';
import { ModuleUpdate } from './module-update';

describe('Module Management Update Component', () => {
  let comp: ModuleUpdate;
  let fixture: ComponentFixture<ModuleUpdate>;
  let activatedRoute: ActivatedRoute;
  let moduleFormService: ModuleFormService;
  let moduleService: ModuleService;

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

    fixture = TestBed.createComponent(ModuleUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    moduleFormService = TestBed.inject(ModuleFormService);
    moduleService = TestBed.inject(ModuleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const module: IModule = { id: 10579 };

      activatedRoute.data = of({ module });
      comp.ngOnInit();

      expect(comp.module).toEqual(module);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IModule>();
      const module = { id: 9460 };
      vitest.spyOn(moduleFormService, 'getModule').mockReturnValue(module);
      vitest.spyOn(moduleService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ module });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(module);
      saveSubject.complete();

      // THEN
      expect(moduleFormService.getModule).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(moduleService.update).toHaveBeenCalledWith(expect.objectContaining(module));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IModule>();
      const module = { id: 9460 };
      vitest.spyOn(moduleFormService, 'getModule').mockReturnValue({ id: null });
      vitest.spyOn(moduleService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ module: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(module);
      saveSubject.complete();

      // THEN
      expect(moduleFormService.getModule).toHaveBeenCalled();
      expect(moduleService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IModule>();
      const module = { id: 9460 };
      vitest.spyOn(moduleService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ module });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(moduleService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
