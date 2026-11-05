package com.crystal.kip.game;

import com.crystal.kip.domain.Answer;
import com.crystal.kip.domain.Game;
import com.crystal.kip.domain.Scenario;
import com.crystal.kip.domain.Stage;
import com.crystal.kip.domain.Topic;
import com.crystal.kip.domain.UserProfile;
import com.crystal.kip.domain.enumeration.ScenarioResolution;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameActive {

    UUID id;
    UserProfile user;

    Game game;
    Topic topic;

    Instant startedAt;

    List<Scenario> scenariosPlayed;
    Scenario scenarioCurrent;
    Stage stageCurrent;

    List<ScenarioResult> scenarioResults;
    List<StageResult> stageResults;

    public GameActive(UserProfile user, Game game, Topic topic) {
        this.user = user;
        this.game = game;
        this.topic = topic;

        this.id = UUID.randomUUID();
        this.startedAt = Instant.now();

        this.scenariosPlayed = new ArrayList<>();
        this.scenarioResults = new ArrayList<>();
        this.stageResults = new ArrayList<>();
    }

    public void startScenario(Scenario scenario) {
        if (scenarioCurrent != null) throw new IllegalStateException("Cannot start a new Scenario while another Scenario is active");

        if (scenariosPlayed.contains(scenario)) throw new IllegalArgumentException(
            "Scenario has already been played before. Did the ScenarioSelector or Curation make a mistake?"
        );

        this.scenarioCurrent = scenario;

        // Upon starting a new Scenario, remember it having been seen to avoid selecting it again
        scenariosPlayed.add(scenario);
    }

    public void recordScenarioResult(ScenarioResolution resolution) {
        if (scenarioCurrent == null) throw new IllegalStateException("Cannot record a Scenario result when no Scenario is active");

        if (resolution == null) throw new IllegalArgumentException("A completed Scenario must have a resolution");

        scenarioResults.add(new ScenarioResult(scenarioCurrent, resolution));

        scenarioCurrent = null;
        stageCurrent = null;
    }

    public void recordStageResult(Answer selectedAnswer) {
        if (stageCurrent == null) throw new IllegalStateException("Cannot record a Stage result when no Stage is active");

        stageResults.add(new StageResult(stageCurrent, selectedAnswer));
    }

    public void startStage(Stage stage) {
        if (scenarioCurrent == null) throw new IllegalStateException("Cannot start a Stage when no Scenario is active");

        if (!scenarioCurrent.equals(stage.getScenario())) throw new IllegalArgumentException(
            "Stage does not belong to the current Scenario"
        );

        this.stageCurrent = stage;
    }

    public UUID getId() {
        return id;
    }

    public UserProfile getUser() {
        return user;
    }

    public Game getGame() {
        return game;
    }

    public Topic getTopic() {
        return topic;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Stage getStageCurrent() {
        return stageCurrent;
    }

    public Scenario getScenarioCurrent() {
        return scenarioCurrent;
    }

    public List<Scenario> getScenariosPlayed() {
        return scenariosPlayed;
    }

    public List<ScenarioResult> getScenarioResults() {
        return scenarioResults;
    }

    public List<StageResult> getStageResults() {
        return stageResults;
    }
}
