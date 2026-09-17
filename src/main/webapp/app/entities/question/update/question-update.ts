import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { IConcept } from 'app/entities/concept/concept.model';
import { ConceptService } from 'app/entities/concept/service/concept.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { IQuestion } from '../question.model';
import { QuestionService } from '../service/question.service';

import { QuestionFormGroup, QuestionFormService } from './question-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-question-update',
  templateUrl: './question-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class QuestionUpdate implements OnInit {
  readonly isSaving = signal(false);
  question: IQuestion | null = null;

  conceptsSharedCollection = signal<IConcept[]>([]);

  protected questionService = inject(QuestionService);
  protected questionFormService = inject(QuestionFormService);
  protected conceptService = inject(ConceptService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: QuestionFormGroup = this.questionFormService.createQuestionFormGroup();

  compareConcept = (o1: IConcept | null, o2: IConcept | null): boolean => this.conceptService.compareConcept(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ question }) => {
      this.question = question;
      if (question) {
        this.updateForm(question);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const question = this.questionFormService.getQuestion(this.editForm);
    if (question.id === null) {
      this.subscribeToSaveResponse(this.questionService.create(question));
    } else {
      this.subscribeToSaveResponse(this.questionService.update(question));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IQuestion | null>): void {
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

  protected updateForm(question: IQuestion): void {
    this.question = question;
    this.questionFormService.resetForm(this.editForm, question);

    this.conceptsSharedCollection.update(concepts =>
      this.conceptService.addConceptToCollectionIfMissing<IConcept>(concepts, question.concept),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.conceptService
      .query()
      .pipe(map((res: HttpResponse<IConcept[]>) => res.body ?? []))
      .pipe(map((concepts: IConcept[]) => this.conceptService.addConceptToCollectionIfMissing<IConcept>(concepts, this.question?.concept)))
      .subscribe((concepts: IConcept[]) => this.conceptsSharedCollection.set(concepts));
  }
}
