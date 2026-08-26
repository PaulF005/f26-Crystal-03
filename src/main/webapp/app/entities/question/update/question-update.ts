import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { TopicService } from 'app/entities/topic/service/topic.service';
import { ITopic } from 'app/entities/topic/topic.model';
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

  topicsSharedCollection = signal<ITopic[]>([]);

  protected questionService = inject(QuestionService);
  protected questionFormService = inject(QuestionFormService);
  protected topicService = inject(TopicService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: QuestionFormGroup = this.questionFormService.createQuestionFormGroup();

  compareTopic = (o1: ITopic | null, o2: ITopic | null): boolean => this.topicService.compareTopic(o1, o2);

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

    this.topicsSharedCollection.update(topics => this.topicService.addTopicToCollectionIfMissing<ITopic>(topics, question.topic));
  }

  protected loadRelationshipsOptions(): void {
    this.topicService
      .query()
      .pipe(map((res: HttpResponse<ITopic[]>) => res.body ?? []))
      .pipe(map((topics: ITopic[]) => this.topicService.addTopicToCollectionIfMissing<ITopic>(topics, this.question?.topic)))
      .subscribe((topics: ITopic[]) => this.topicsSharedCollection.set(topics));
  }
}
