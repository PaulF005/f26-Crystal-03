import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { ILegalContent } from 'app/entities/legal-content/legal-content.model';
import { LegalContentService } from 'app/entities/legal-content/service/legal-content.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { SourceService } from '../service/source.service';
import { ISource } from '../source.model';

import { SourceFormGroup, SourceFormService } from './source-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-source-update',
  templateUrl: './source-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class SourceUpdate implements OnInit {
  readonly isSaving = signal(false);
  source: ISource | null = null;

  legalContentsSharedCollection = signal<ILegalContent[]>([]);

  protected sourceService = inject(SourceService);
  protected sourceFormService = inject(SourceFormService);
  protected legalContentService = inject(LegalContentService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SourceFormGroup = this.sourceFormService.createSourceFormGroup();

  compareLegalContent = (o1: ILegalContent | null, o2: ILegalContent | null): boolean =>
    this.legalContentService.compareLegalContent(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ source }) => {
      this.source = source;
      if (source) {
        this.updateForm(source);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const source = this.sourceFormService.getSource(this.editForm);
    if (source.id === null) {
      this.subscribeToSaveResponse(this.sourceService.create(source));
    } else {
      this.subscribeToSaveResponse(this.sourceService.update(source));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ISource | null>): void {
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

  protected updateForm(source: ISource): void {
    this.source = source;
    this.sourceFormService.resetForm(this.editForm, source);

    this.legalContentsSharedCollection.update(legalContents =>
      this.legalContentService.addLegalContentToCollectionIfMissing<ILegalContent>(legalContents, source.legalContent),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.legalContentService
      .query()
      .pipe(map((res: HttpResponse<ILegalContent[]>) => res.body ?? []))
      .pipe(
        map((legalContents: ILegalContent[]) =>
          this.legalContentService.addLegalContentToCollectionIfMissing<ILegalContent>(legalContents, this.source?.legalContent),
        ),
      )
      .subscribe((legalContents: ILegalContent[]) => this.legalContentsSharedCollection.set(legalContents));
  }
}
