import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize } from 'rxjs';

import { AlertError } from 'app/shared/alert/alert-error';
import { IFeedback } from '../feedback.model';
import { FeedbackService } from '../service/feedback.service';

import { FeedbackFormGroup, FeedbackFormService } from './feedback-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-feedback-update',
  templateUrl: './feedback-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class FeedbackUpdate implements OnInit {
  readonly isSaving = signal(false);
  feedback: IFeedback | null = null;

  protected feedbackService = inject(FeedbackService);
  protected feedbackFormService = inject(FeedbackFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: FeedbackFormGroup = this.feedbackFormService.createFeedbackFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ feedback }) => {
      this.feedback = feedback;
      if (feedback) {
        this.updateForm(feedback);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const feedback = this.feedbackFormService.getFeedback(this.editForm);
    if (feedback.id === null) {
      this.subscribeToSaveResponse(this.feedbackService.create(feedback));
    } else {
      this.subscribeToSaveResponse(this.feedbackService.update(feedback));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IFeedback | null>): void {
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

  protected updateForm(feedback: IFeedback): void {
    this.feedback = feedback;
    this.feedbackFormService.resetForm(this.editForm, feedback);
  }
}
