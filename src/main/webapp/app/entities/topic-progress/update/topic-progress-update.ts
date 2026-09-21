import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { TopicService } from 'app/entities/topic/service/topic.service';
import { ITopic } from 'app/entities/topic/topic.model';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { IUserProfile } from 'app/entities/user-profile/user-profile.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TopicProgressService } from '../service/topic-progress.service';
import { ITopicProgress } from '../topic-progress.model';

import { TopicProgressFormGroup, TopicProgressFormService } from './topic-progress-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-topic-progress-update',
  templateUrl: './topic-progress-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class TopicProgressUpdate implements OnInit {
  readonly isSaving = signal(false);
  topicProgress: ITopicProgress | null = null;

  topicsSharedCollection = signal<ITopic[]>([]);
  userProfilesSharedCollection = signal<IUserProfile[]>([]);

  protected topicProgressService = inject(TopicProgressService);
  protected topicProgressFormService = inject(TopicProgressFormService);
  protected topicService = inject(TopicService);
  protected userProfileService = inject(UserProfileService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TopicProgressFormGroup = this.topicProgressFormService.createTopicProgressFormGroup();

  compareTopic = (o1: ITopic | null, o2: ITopic | null): boolean => this.topicService.compareTopic(o1, o2);

  compareUserProfile = (o1: IUserProfile | null, o2: IUserProfile | null): boolean => this.userProfileService.compareUserProfile(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ topicProgress }) => {
      this.topicProgress = topicProgress;
      if (topicProgress) {
        this.updateForm(topicProgress);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const topicProgress = this.topicProgressFormService.getTopicProgress(this.editForm);
    if (topicProgress.id === null) {
      this.subscribeToSaveResponse(this.topicProgressService.create(topicProgress));
    } else {
      this.subscribeToSaveResponse(this.topicProgressService.update(topicProgress));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ITopicProgress | null>): void {
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

  protected updateForm(topicProgress: ITopicProgress): void {
    this.topicProgress = topicProgress;
    this.topicProgressFormService.resetForm(this.editForm, topicProgress);

    this.topicsSharedCollection.update(topics => this.topicService.addTopicToCollectionIfMissing<ITopic>(topics, topicProgress.topic));
    this.userProfilesSharedCollection.update(userProfiles =>
      this.userProfileService.addUserProfileToCollectionIfMissing<IUserProfile>(
        userProfiles,
        topicProgress.user,
        topicProgress.userProfile,
      ),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.topicService
      .query()
      .pipe(map((res: HttpResponse<ITopic[]>) => res.body ?? []))
      .pipe(map((topics: ITopic[]) => this.topicService.addTopicToCollectionIfMissing<ITopic>(topics, this.topicProgress?.topic)))
      .subscribe((topics: ITopic[]) => this.topicsSharedCollection.set(topics));

    this.userProfileService
      .query()
      .pipe(map((res: HttpResponse<IUserProfile[]>) => res.body ?? []))
      .pipe(
        map((userProfiles: IUserProfile[]) =>
          this.userProfileService.addUserProfileToCollectionIfMissing<IUserProfile>(
            userProfiles,
            this.topicProgress?.user,
            this.topicProgress?.userProfile,
          ),
        ),
      )
      .subscribe((userProfiles: IUserProfile[]) => this.userProfilesSharedCollection.set(userProfiles));
  }
}
