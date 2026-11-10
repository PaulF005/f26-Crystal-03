package com.crystal.kip.progress_update;

import com.crystal.kip.domain.Concept;
import com.crystal.kip.domain.ConceptProgress;
import com.crystal.kip.domain.GameProgress;
import com.crystal.kip.domain.Topic;
import com.crystal.kip.domain.TopicProgress;
import com.crystal.kip.domain.UserProfile;
import com.crystal.kip.game.GameActive;
import com.crystal.kip.game.StageResult;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProgressUpdater {

    private final ProgressUpdaterConcept progressUpdaterConcept;
    private final ProgressUpdaterTopic progressUpdaterTopic;
    private final ProgressUpdaterGame progressUpdaterGame;

    @PersistenceContext
    private EntityManager entityManager;

    public ProgressUpdater(
        ProgressUpdaterConcept progressUpdaterConcept,
        ProgressUpdaterTopic progressUpdaterTopic,
        ProgressUpdaterGame progressUpdaterGame
    ) {
        this.progressUpdaterConcept = progressUpdaterConcept;
        this.progressUpdaterTopic = progressUpdaterTopic;
        this.progressUpdaterGame = progressUpdaterGame;
    }

    public void updateProgress(GameActive active) {
        List<StageResult> stageResults = active.getStageResults();

        if (stageResults.isEmpty()) throw new IllegalStateException("Cannot update progress from a Session with no Stage results");

        Map<Concept, List<StageResult>> resultsByConcept = stageResults.stream().collect(
            Collectors.groupingBy(result -> {
                Concept concept = result.getStage().getQuestion().getConcept();

                if (concept == null) throw new IllegalStateException("Stage Question does not have an associated Concept");

                return concept;
            })
        );

        float topicImprovement = 0.0f;

        for (Map.Entry<Concept, List<StageResult>> entry : resultsByConcept.entrySet()) {
            Concept concept = entry.getKey();
            List<StageResult> conceptResults = entry.getValue();

            int questionsRight = (int) conceptResults.stream().filter(StageResult::isCorrect).count();

            topicImprovement += progressUpdaterConcept.updateConceptProgress(concept.getId(), questionsRight);
        }

        progressUpdaterTopic.updateTopicProgress(active.getTopic().getId(), topicImprovement);

        int questionsRight = (int) stageResults.stream().filter(StageResult::isCorrect).count();

        float performance = (float) questionsRight / stageResults.size();

        progressUpdaterGame.updateGameProgress(active.getGame().getId(), performance);
    }

    @Transactional
    public void initializeProgress(GameActive active) {
        UserProfile player = entityManager.find(UserProfile.class, active.getUser().getId(), LockModeType.PESSIMISTIC_WRITE);

        Topic topic = entityManager.find(Topic.class, active.getTopic().getId());

        boolean hasGame = player
            .getGameProgresseses()
            .stream()
            .anyMatch(progress -> progress.getGame().getId().equals(active.getGame().getId()));

        if (!hasGame) {
            GameProgress progress = new GameProgress().game(active.getGame()).sessionsPlayed(0).performance(0F).lastPlayedAt(Instant.EPOCH);

            player.addGameProgresses(progress);
            entityManager.persist(progress);
        }

        boolean hasTopic = player
            .getTopicProgresseses()
            .stream()
            .anyMatch(progress -> progress.getTopic().getId().equals(topic.getId()));

        if (!hasTopic) {
            TopicProgress progress = new TopicProgress().topic(topic).competency(0F).improvement(0F).lastPracticedAt(Instant.EPOCH);

            player.addTopicProgresses(progress);
            entityManager.persist(progress);
        }

        for (Concept concept : topic.getConcepts()) {
            boolean hasConcept = player
                .getConceptProgresseses()
                .stream()
                .anyMatch(progress -> progress.getConcept().getId().equals(concept.getId()));

            if (hasConcept) continue;

            int questionCount = concept.getQuestions().size();
            if (questionCount == 0) continue;

            if (questionCount < 5) {
                throw new IllegalStateException("Concept progress requires at least five questions per concept");
            }

            ConceptProgress progress = new ConceptProgress()
                .concept(concept)
                .competency(0F)
                .improvement(0F)
                .lastPracticedAt(Instant.EPOCH)
                .maxQuestions(questionCount);

            player.addConceptProgresses(progress);
            entityManager.persist(progress);
        }
    }
}
