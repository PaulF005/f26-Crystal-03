import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { ILegalContent } from 'app/entities/legal-content/legal-content.model';
import { LegalContentService } from 'app/entities/legal-content/service/legal-content.service';
import { SourceService } from '../service/source.service';
import { ISource } from '../source.model';

import { SourceFormService } from './source-form.service';
import { SourceUpdate } from './source-update';

describe('Source Management Update Component', () => {
  let comp: SourceUpdate;
  let fixture: ComponentFixture<SourceUpdate>;
  let activatedRoute: ActivatedRoute;
  let sourceFormService: SourceFormService;
  let sourceService: SourceService;
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

    fixture = TestBed.createComponent(SourceUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    sourceFormService = TestBed.inject(SourceFormService);
    sourceService = TestBed.inject(SourceService);
    legalContentService = TestBed.inject(LegalContentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call LegalContent query and add missing value', () => {
      const source: ISource = { id: 467 };
      const legalContent: ILegalContent = { id: 7620 };
      source.legalContent = legalContent;

      const legalContentCollection: ILegalContent[] = [{ id: 7620 }];
      vitest.spyOn(legalContentService, 'query').mockReturnValue(of(new HttpResponse({ body: legalContentCollection })));
      const additionalLegalContents = [legalContent];
      const expectedCollection: ILegalContent[] = [...additionalLegalContents, ...legalContentCollection];
      vitest.spyOn(legalContentService, 'addLegalContentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ source });
      comp.ngOnInit();

      expect(legalContentService.query).toHaveBeenCalled();
      expect(legalContentService.addLegalContentToCollectionIfMissing).toHaveBeenCalledWith(
        legalContentCollection,
        ...additionalLegalContents.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.legalContentsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const source: ISource = { id: 467 };
      const legalContent: ILegalContent = { id: 7620 };
      source.legalContent = legalContent;

      activatedRoute.data = of({ source });
      comp.ngOnInit();

      expect(comp.legalContentsSharedCollection()).toContainEqual(legalContent);
      expect(comp.source).toEqual(source);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ISource>();
      const source = { id: 4722 };
      vitest.spyOn(sourceFormService, 'getSource').mockReturnValue(source);
      vitest.spyOn(sourceService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ source });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(source);
      saveSubject.complete();

      // THEN
      expect(sourceFormService.getSource).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(sourceService.update).toHaveBeenCalledWith(expect.objectContaining(source));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ISource>();
      const source = { id: 4722 };
      vitest.spyOn(sourceFormService, 'getSource').mockReturnValue({ id: null });
      vitest.spyOn(sourceService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ source: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(source);
      saveSubject.complete();

      // THEN
      expect(sourceFormService.getSource).toHaveBeenCalled();
      expect(sourceService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ISource>();
      const source = { id: 4722 };
      vitest.spyOn(sourceService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ source });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(sourceService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareLegalContent', () => {
      it('should forward to legalContentService', () => {
        const entity = { id: 7620 };
        const entity2 = { id: 29225 };
        vitest.spyOn(legalContentService, 'compareLegalContent');
        comp.compareLegalContent(entity, entity2);
        expect(legalContentService.compareLegalContent).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
