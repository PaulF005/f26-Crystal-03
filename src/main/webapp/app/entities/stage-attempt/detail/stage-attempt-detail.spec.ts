import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { StageAttemptDetail } from './stage-attempt-detail';

describe('StageAttempt Management Detail Component', () => {
  let comp: StageAttemptDetail;
  let fixture: ComponentFixture<StageAttemptDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./stage-attempt-detail').then(m => m.StageAttemptDetail),
              resolve: { stageAttempt: () => of({ id: 23225 }) },
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
    fixture = TestBed.createComponent(StageAttemptDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load stageAttempt on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', StageAttemptDetail);

      // THEN
      expect(instance.stageAttempt()).toEqual(expect.objectContaining({ id: 23225 }));
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
