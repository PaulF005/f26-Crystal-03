import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { ConceptProgressDetail } from './concept-progress-detail';

describe('ConceptProgress Management Detail Component', () => {
  let comp: ConceptProgressDetail;
  let fixture: ComponentFixture<ConceptProgressDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./concept-progress-detail').then(m => m.ConceptProgressDetail),
              resolve: { conceptProgress: () => of({ id: 29965 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    });
    const library = TestBed.inject(FaIconLibrary);
    library.addIcons(faArrowLeft);
    library.addIcons(faPencilAlt);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConceptProgressDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load conceptProgress on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ConceptProgressDetail);

      // THEN
      expect(instance.conceptProgress()).toEqual(expect.objectContaining({ id: 29965 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      vi.spyOn(globalThis.history, 'back');
      comp.previousState();
      expect(globalThis.history.back).toHaveBeenCalled();
    });
  });
});
