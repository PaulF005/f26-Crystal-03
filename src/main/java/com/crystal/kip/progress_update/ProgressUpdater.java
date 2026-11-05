package com.crystal.kip.progress_update;

import com.crystal.kip.domain.Concept;
import com.crystal.kip.game.GameActive;
import com.crystal.kip.game.StageResult;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ProgressUpdater {

    private final ProgressUpdaterConcept progressUpdaterConcept;
    private final ProgressUpdaterTopic progressUpdaterTopic;
    private final ProgressUpdaterGame progressUpdaterGame;

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
}
