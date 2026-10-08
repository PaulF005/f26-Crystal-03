import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize } from 'rxjs';

import { AlertError } from 'app/shared/alert/alert-error';
import { TopicService } from '../service/topic.service';
import { ITopic } from '../topic.model';

import { TopicFormGroup, TopicFormService } from './topic-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-topic-update',
  templateUrl: './topic-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class TopicUpdate implements OnInit {
  readonly isSaving = signal(false);
  topic: ITopic | null = null;

  protected topicService = inject(TopicService);
  protected topicFormService = inject(TopicFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TopicFormGroup = this.topicFormService.createTopicFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ topic }) => {
      this.topic = topic;
      if (topic) {
        this.updateForm(topic);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const topic = this.topicFormService.getTopic(this.editForm);
    if (topic.id === null) {
      this.subscribeToSaveResponse(this.topicService.create(topic));
    } else {
      this.subscribeToSaveResponse(this.topicService.update(topic));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ITopic | null>): void {
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

  protected updateForm(topic: ITopic): void {
    this.topic = topic;
    this.topicFormService.resetForm(this.editForm, topic);
  }
}
