import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize } from 'rxjs';

import { AlertError } from 'app/shared/alert/alert-error';
import { ILegalContent } from '../legal-content.model';
import { LegalContentService } from '../service/legal-content.service';

import { LegalContentFormGroup, LegalContentFormService } from './legal-content-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-legal-content-update',
  templateUrl: './legal-content-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class LegalContentUpdate implements OnInit {
  readonly isSaving = signal(false);
  legalContent: ILegalContent | null = null;

  protected legalContentService = inject(LegalContentService);
  protected legalContentFormService = inject(LegalContentFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: LegalContentFormGroup = this.legalContentFormService.createLegalContentFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ legalContent }) => {
      this.legalContent = legalContent;
      if (legalContent) {
        this.updateForm(legalContent);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const legalContent = this.legalContentFormService.getLegalContent(this.editForm);
    if (legalContent.id === null) {
      this.subscribeToSaveResponse(this.legalContentService.create(legalContent));
    } else {
      this.subscribeToSaveResponse(this.legalContentService.update(legalContent));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ILegalContent | null>): void {
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

  protected updateForm(legalContent: ILegalContent): void {
    this.legalContent = legalContent;
    this.legalContentFormService.resetForm(this.editForm, legalContent);
  }
}
