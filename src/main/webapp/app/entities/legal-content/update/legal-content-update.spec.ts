import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { ILegalContent } from '../legal-content.model';
import { LegalContentService } from '../service/legal-content.service';

import { LegalContentFormService } from './legal-content-form.service';
import { LegalContentUpdate } from './legal-content-update';

describe('LegalContent Management Update Component', () => {
  let comp: LegalContentUpdate;
  let fixture: ComponentFixture<LegalContentUpdate>;
  let activatedRoute: ActivatedRoute;
  let legalContentFormService: LegalContentFormService;
  let legalContentService: LegalContentService;

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

    fixture = TestBed.createComponent(LegalContentUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    legalContentFormService = TestBed.inject(LegalContentFormService);
    legalContentService = TestBed.inject(LegalContentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const legalContent: ILegalContent = { id: 29225 };

      activatedRoute.data = of({ legalContent });
      comp.ngOnInit();

      expect(comp.legalContent).toEqual(legalContent);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILegalContent>();
      const legalContent = { id: 7620 };
      vitest.spyOn(legalContentFormService, 'getLegalContent').mockReturnValue(legalContent);
      vitest.spyOn(legalContentService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ legalContent });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(legalContent);
      saveSubject.complete();

      // THEN
      expect(legalContentFormService.getLegalContent).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(legalContentService.update).toHaveBeenCalledWith(expect.objectContaining(legalContent));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILegalContent>();
      const legalContent = { id: 7620 };
      vitest.spyOn(legalContentFormService, 'getLegalContent').mockReturnValue({ id: null });
      vitest.spyOn(legalContentService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ legalContent: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(legalContent);
      saveSubject.complete();

      // THEN
      expect(legalContentFormService.getLegalContent).toHaveBeenCalled();
      expect(legalContentService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ILegalContent>();
      const legalContent = { id: 7620 };
      vitest.spyOn(legalContentService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ legalContent });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(legalContentService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
