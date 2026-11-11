import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { NgbAccordionModule, NgbProgressbarModule } from '@ng-bootstrap/ng-bootstrap';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { AccountService } from 'app/core/auth/account.service';
import { UserProfileService } from 'app/entities/user-profile/service/user-profile.service';
import { TopicService } from 'app/entities/topic/service/topic.service';
import { ConceptService } from 'app/entities/concept/service/concept.service';
import { TopicProgressService } from 'app/entities/topic-progress/service/topic-progress.service';
import { ConceptProgressService } from 'app/entities/concept-progress/service/concept-progress.service';
import { ITopic } from 'app/entities/topic/topic.model';
import { IConcept } from 'app/entities/concept/concept.model';
import { ITopicProgress } from 'app/entities/topic-progress/topic-progress.model';
import { IConceptProgress } from 'app/entities/concept-progress/concept-progress.model';

export interface ConceptProgressView {
  concept: IConcept;
  progress?: IConceptProgress;
}

export interface TopicProgressView {
  topic: ITopic;
  progress?: ITopicProgress;
  concepts: ConceptProgressView[];
}

@Component({
  selector: 'jhi-progress',
  imports: [CommonModule, FontAwesomeModule],
  templateUrl: './progress.html',
  styleUrl: './progress.css',
})
export class ProgressComponent implements OnInit {
  private accountService = inject(AccountService);
  private userProfileService = inject(UserProfileService);
  private topicService = inject(TopicService);
  private conceptService = inject(ConceptService);
  private topicProgressService = inject(TopicProgressService);
  private conceptProgressService = inject(ConceptProgressService);

  topicViews = signal<TopicProgressView[]>([]);
  isLoading = signal<boolean>(true);
  collapsedTopics = signal<Set<number>>(new Set());

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => {
      if (!account) {
        this.isLoading.set(false);
        return;
      }

      forkJoin({
        profiles: this.userProfileService.query({ size: 1000 }),
        topics: this.topicService.query({ size: 1000 }),
        concepts: this.conceptService.query({ size: 1000 }),
        topicProgresses: this.topicProgressService.query({ size: 1000 }),
        conceptProgresses: this.conceptProgressService.query({ size: 1000 }),
      }).subscribe({
        next: ({ profiles, topics, concepts, topicProgresses, conceptProgresses }) => {
          const myProfile = (profiles.body ?? []).find(p => p.username === account.login || p.email === account.email);

          if (!myProfile) {
            this.isLoading.set(false);
            return;
          }

          const myTopicProgressMap = new Map<number, ITopicProgress>();
          (topicProgresses.body ?? [])
            .filter(tp => tp.userProfile?.id === myProfile.id && tp.topic?.id != null)
            .forEach(tp => myTopicProgressMap.set(tp.topic!.id, tp));

          const myConceptProgressMap = new Map<number, IConceptProgress>();
          (conceptProgresses.body ?? [])
            .filter(cp => cp.userProfile?.id === myProfile.id && cp.concept?.id != null)
            .forEach(cp => myConceptProgressMap.set(cp.concept!.id, cp));

          const allConcepts = concepts.body ?? [];
          const views: TopicProgressView[] = (topics.body ?? []).map(topic => {
            const childConcepts: ConceptProgressView[] = allConcepts
              .filter(c => c.topic?.id === topic.id)
              .map(concept => ({
                concept,
                progress: myConceptProgressMap.get(concept.id),
              }));

            return {
              topic,
              progress: myTopicProgressMap.get(topic.id),
              concepts: childConcepts,
            };
          });

          this.topicViews.set(views);
          this.isLoading.set(false);
        },
        error: () => this.isLoading.set(false),
      });
    });
  }

  toggleTopic(topicId?: number): void {
    if (topicId == null) return;
    this.collapsedTopics.update(prev => {
      const next = new Set(prev);
      if (next.has(topicId)) {
        next.delete(topicId);
      } else {
        next.add(topicId);
      }
      return next;
    });
  }

  isCollapsed(topicId?: number): boolean {
    return topicId != null && this.collapsedTopics().has(topicId);
  }

  getCompetencyPercent(value?: number | null): number {
    return value != null ? Math.round(value * 100) : 0;
  }

  getProgressBarType(value?: number | null): string {
    if (value == null) return 'secondary';
    if (value >= 0.8) return 'success';
    if (value >= 0.5) return 'info';
    return 'warning';
  }

  getProgressColor(value?: number | null): string {
    if (value == null) {
      return '#d9534f'; // Red (Not started)
    }

    const percent = value * 100;

    if (percent >= 80) {
      return '#337ab7'; // Blue (80%+)
    }
    if (percent >= 60) {
      return '#5cb85c'; // Green (60% - 79%)
    }
    if (percent >= 30) {
      return '#f0ad4e'; // Yellow (30% - 60%)
    }
    return '#d9534f'; // Red (Under 30%)
  }
}
