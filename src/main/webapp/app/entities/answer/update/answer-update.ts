import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { IFeedback } from 'app/entities/feedback/feedback.model';
import { FeedbackService } from 'app/entities/feedback/service/feedback.service';
import { IQuestion } from 'app/entities/question/question.model';
import { QuestionService } from 'app/entities/question/service/question.service';
import { StageService } from 'app/entities/stage/service/stage.service';
import { IStage } from 'app/entities/stage/stage.model';
import { AlertError } from 'app/shared/alert';
import { IAnswer } from '../answer.model';
import { AnswerService } from '../service/answer.service';

import { AnswerFormGroup, AnswerFormService } from './answer-form.service';

@Component({
  selector: 'jhi-answer-update',
  templateUrl: './answer-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class AnswerUpdate implements OnInit {
  readonly isSaving = signal(false);
  answer: IAnswer | null = null;

  stagesSharedCollection = signal<IStage[]>([]);
  feedbacksSharedCollection = signal<IFeedback[]>([]);
  questionsSharedCollection = signal<IQuestion[]>([]);

  protected answerService = inject(AnswerService);
  protected answerFormService = inject(AnswerFormService);
  protected stageService = inject(StageService);
  protected feedbackService = inject(FeedbackService);
  protected questionService = inject(QuestionService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AnswerFormGroup = this.answerFormService.createAnswerFormGroup();

  compareStage = (o1: IStage | null, o2: IStage | null): boolean => this.stageService.compareStage(o1, o2);

  compareFeedback = (o1: IFeedback | null, o2: IFeedback | null): boolean => this.feedbackService.compareFeedback(o1, o2);

  compareQuestion = (o1: IQuestion | null, o2: IQuestion | null): boolean => this.questionService.compareQuestion(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ answer }) => {
      this.answer = answer;
      if (answer) {
        this.updateForm(answer);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const answer = this.answerFormService.getAnswer(this.editForm);
    if (answer.id === null) {
      this.subscribeToSaveResponse(this.answerService.create(answer));
    } else {
      this.subscribeToSaveResponse(this.answerService.update(answer));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IAnswer | null>): void {
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

  protected updateForm(answer: IAnswer): void {
    this.answer = answer;
    this.answerFormService.resetForm(this.editForm, answer);

    this.stagesSharedCollection.update(stages => this.stageService.addStageToCollectionIfMissing<IStage>(stages, answer.nextStage));
    this.feedbacksSharedCollection.update(feedbacks =>
      this.feedbackService.addFeedbackToCollectionIfMissing<IFeedback>(feedbacks, answer.feedback),
    );
    this.questionsSharedCollection.update(questions =>
      this.questionService.addQuestionToCollectionIfMissing<IQuestion>(questions, answer.question),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.stageService
      .query()
      .pipe(map((res: HttpResponse<IStage[]>) => res.body ?? []))
      .pipe(map((stages: IStage[]) => this.stageService.addStageToCollectionIfMissing<IStage>(stages, this.answer?.nextStage)))
      .subscribe((stages: IStage[]) => this.stagesSharedCollection.set(stages));

    this.feedbackService
      .query()
      .pipe(map((res: HttpResponse<IFeedback[]>) => res.body ?? []))
      .pipe(
        map((feedbacks: IFeedback[]) => this.feedbackService.addFeedbackToCollectionIfMissing<IFeedback>(feedbacks, this.answer?.feedback)),
      )
      .subscribe((feedbacks: IFeedback[]) => this.feedbacksSharedCollection.set(feedbacks));

    this.questionService
      .query()
      .pipe(map((res: HttpResponse<IQuestion[]>) => res.body ?? []))
      .pipe(
        map((questions: IQuestion[]) => this.questionService.addQuestionToCollectionIfMissing<IQuestion>(questions, this.answer?.question)),
      )
      .subscribe((questions: IQuestion[]) => this.questionsSharedCollection.set(questions));
  }
}
