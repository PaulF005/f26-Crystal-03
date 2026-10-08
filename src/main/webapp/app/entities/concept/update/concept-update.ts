import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { ILegalContent } from 'app/entities/legal-content/legal-content.model';
import { LegalContentService } from 'app/entities/legal-content/service/legal-content.service';
import { TopicService } from 'app/entities/topic/service/topic.service';
import { ITopic } from 'app/entities/topic/topic.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { IConcept } from '../concept.model';
import { ConceptService } from '../service/concept.service';

import { ConceptFormGroup, ConceptFormService } from './concept-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-concept-update',
  templateUrl: './concept-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ConceptUpdate implements OnInit {
  readonly isSaving = signal(false);
  concept: IConcept | null = null;

  legalContentsSharedCollection = signal<ILegalContent[]>([]);
  topicsSharedCollection = signal<ITopic[]>([]);

  protected conceptService = inject(ConceptService);
  protected conceptFormService = inject(ConceptFormService);
  protected legalContentService = inject(LegalContentService);
  protected topicService = inject(TopicService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ConceptFormGroup = this.conceptFormService.createConceptFormGroup();

  compareLegalContent = (o1: ILegalContent | null, o2: ILegalContent | null): boolean =>
    this.legalContentService.compareLegalContent(o1, o2);

  compareTopic = (o1: ITopic | null, o2: ITopic | null): boolean => this.topicService.compareTopic(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ concept }) => {
      this.concept = concept;
      if (concept) {
        this.updateForm(concept);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const concept = this.conceptFormService.getConcept(this.editForm);
    if (concept.id === null) {
      this.subscribeToSaveResponse(this.conceptService.create(concept));
    } else {
      this.subscribeToSaveResponse(this.conceptService.update(concept));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IConcept | null>): void {
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

  protected updateForm(concept: IConcept): void {
    this.concept = concept;
    this.conceptFormService.resetForm(this.editForm, concept);

    this.legalContentsSharedCollection.update(legalContents =>
      this.legalContentService.addLegalContentToCollectionIfMissing<ILegalContent>(legalContents, concept.legalContent),
    );
    this.topicsSharedCollection.update(topics => this.topicService.addTopicToCollectionIfMissing<ITopic>(topics, concept.topic));
  }

  protected loadRelationshipsOptions(): void {
    this.legalContentService
      .query()
      .pipe(map((res: HttpResponse<ILegalContent[]>) => res.body ?? []))
      .pipe(
        map((legalContents: ILegalContent[]) =>
          this.legalContentService.addLegalContentToCollectionIfMissing<ILegalContent>(legalContents, this.concept?.legalContent),
        ),
      )
      .subscribe((legalContents: ILegalContent[]) => this.legalContentsSharedCollection.set(legalContents));

    this.topicService
      .query()
      .pipe(map((res: HttpResponse<ITopic[]>) => res.body ?? []))
      .pipe(map((topics: ITopic[]) => this.topicService.addTopicToCollectionIfMissing<ITopic>(topics, this.concept?.topic)))
      .subscribe((topics: ITopic[]) => this.topicsSharedCollection.set(topics));
  }
}
