package com.crystal.kip.progress_update;

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

    public void updateAllProgress(int questionsRight, float performance) {
        this.progressUpdaterConcept.updateConceptProgress(questionsRight);
        this.progressUpdaterTopic.updateTopicProgress(this.progressUpdaterConcept.getImprovement());
        this.progressUpdaterGame.updateGameProgress(performance);
    }
}
