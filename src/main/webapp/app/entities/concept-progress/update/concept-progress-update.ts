import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { IConcept } from 'app/entities/concept/concept.model';
import { ConceptService } from 'app/entities/concept/service/concept.service';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { AlertError } from 'app/shared/alert';
import { IConceptProgress } from '../concept-progress.model';
import { ConceptProgressService } from '../service/concept-progress.service';

import { ConceptProgressFormGroup, ConceptProgressFormService } from './concept-progress-form.service';

@Component({
  selector: 'jhi-concept-progress-update',
  templateUrl: './concept-progress-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ConceptProgressUpdate implements OnInit {
  readonly isSaving = signal(false);
  conceptProgress: IConceptProgress | null = null;

  conceptsSharedCollection = signal<IConcept[]>([]);
  userProfilesSharedCollection = signal<IUserProfile[]>([]);

  protected conceptProgressService = inject(ConceptProgressService);
  protected conceptProgressFormService = inject(ConceptProgressFormService);
  protected conceptService = inject(ConceptService);
  protected userProfileService = inject(UserProfileService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ConceptProgressFormGroup = this.conceptProgressFormService.createConceptProgressFormGroup();

  compareConcept = (o1: IConcept | null, o2: IConcept | null): boolean => this.conceptService.compareConcept(o1, o2);

  compareUserProfile = (o1: IUserProfile | null, o2: IUserProfile | null): boolean => this.userProfileService.compareUserProfile(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ conceptProgress }) => {
      this.conceptProgress = conceptProgress;
      if (conceptProgress) {
        this.updateForm(conceptProgress);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const conceptProgress = this.conceptProgressFormService.getConceptProgress(this.editForm);
    if (conceptProgress.id === null) {
      this.subscribeToSaveResponse(this.conceptProgressService.create(conceptProgress));
    } else {
      this.subscribeToSaveResponse(this.conceptProgressService.update(conceptProgress));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IConceptProgress | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(conceptProgress: IConceptProgress): void {
    this.conceptProgress = conceptProgress;
    this.conceptProgressFormService.resetForm(this.editForm, conceptProgress);

    this.conceptsSharedCollection.update(concepts =>
      this.conceptService.addConceptToCollectionIfMissing<IConcept>(concepts, conceptProgress.concept),
    );
    this.userProfilesSharedCollection.update(userProfiles =>
      this.userProfileService.addUserProfileToCollectionIfMissing<IUserProfile>(userProfiles, conceptProgress.userProfile),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.conceptService
      .query()
      .pipe(map((res: HttpResponse<IConcept[]>) => res.body ?? []))
      .pipe(
        map((concepts: IConcept[]) =>
          this.conceptService.addConceptToCollectionIfMissing<IConcept>(concepts, this.conceptProgress?.concept),
        ),
      )
      .subscribe((concepts: IConcept[]) => this.conceptsSharedCollection.set(concepts));

    this.userProfileService
      .query()
      .pipe(map((res: HttpResponse<IUserProfile[]>) => res.body ?? []))
      .pipe(
        map((userProfiles: IUserProfile[]) =>
          this.userProfileService.addUserProfileToCollectionIfMissing<IUserProfile>(userProfiles, this.conceptProgress?.userProfile),
        ),
      )
      .subscribe((userProfiles: IUserProfile[]) => this.userProfilesSharedCollection.set(userProfiles));
  }
}
