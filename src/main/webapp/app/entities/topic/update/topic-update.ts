import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { ILegalContent } from 'app/entities/legal-content/legal-content.model';
import { LegalContentService } from 'app/entities/legal-content/service/legal-content.service';
import { IModule } from 'app/entities/module/module.model';
import { ModuleService } from 'app/entities/module/service/module.service';
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

  legalContentsSharedCollection = signal<ILegalContent[]>([]);
  modulesSharedCollection = signal<IModule[]>([]);

  protected topicService = inject(TopicService);
  protected topicFormService = inject(TopicFormService);
  protected legalContentService = inject(LegalContentService);
  protected moduleService = inject(ModuleService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TopicFormGroup = this.topicFormService.createTopicFormGroup();

  compareLegalContent = (o1: ILegalContent | null, o2: ILegalContent | null): boolean =>
    this.legalContentService.compareLegalContent(o1, o2);

  compareModule = (o1: IModule | null, o2: IModule | null): boolean => this.moduleService.compareModule(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ topic }) => {
      this.topic = topic;
      if (topic) {
        this.updateForm(topic);
      }

      this.loadRelationshipsOptions();
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

    this.legalContentsSharedCollection.update(legalContents =>
      this.legalContentService.addLegalContentToCollectionIfMissing<ILegalContent>(legalContents, topic.legalContent),
    );
    this.modulesSharedCollection.update(modules => this.moduleService.addModuleToCollectionIfMissing<IModule>(modules, topic.module));
  }

  protected loadRelationshipsOptions(): void {
    this.legalContentService
      .query()
      .pipe(map((res: HttpResponse<ILegalContent[]>) => res.body ?? []))
      .pipe(
        map((legalContents: ILegalContent[]) =>
          this.legalContentService.addLegalContentToCollectionIfMissing<ILegalContent>(legalContents, this.topic?.legalContent),
        ),
      )
      .subscribe((legalContents: ILegalContent[]) => this.legalContentsSharedCollection.set(legalContents));

    this.moduleService
      .query()
      .pipe(map((res: HttpResponse<IModule[]>) => res.body ?? []))
      .pipe(map((modules: IModule[]) => this.moduleService.addModuleToCollectionIfMissing<IModule>(modules, this.topic?.module)))
      .subscribe((modules: IModule[]) => this.modulesSharedCollection.set(modules));
  }
}
