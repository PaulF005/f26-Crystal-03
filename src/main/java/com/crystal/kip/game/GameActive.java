package com.crystal.kip.game;

import com.crystal.kip.domain.Game;
import com.crystal.kip.domain.Scenario;
import com.crystal.kip.domain.Stage;
import com.crystal.kip.domain.Topic;
import com.crystal.kip.domain.UserProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameActive {

    UUID id;
    UserProfile user;

    Game game;
    Topic topic;

    List<Scenario> scenariosPlayed;
    Scenario scenarioCurrent;
    Stage stageCurrent;

    public GameActive(UserProfile user, Game game, Topic topic) {
        this.user = user;
        this.game = game;
        this.topic = topic;

        this.id = UUID.randomUUID();
        scenariosPlayed = new ArrayList<>();
    }

    public void startScenario(Scenario scenario) {
        this.scenarioCurrent = scenario;

        if (scenariosPlayed.contains(scenario)) throw new IllegalArgumentException(
            "Scenario has already been played before. Did the ScenarioSelector or Curation make a mistake?"
        );

        // Upon starting a new Scenario, remember it having been seen to avoid selecting it again
        scenariosPlayed.add(scenario);
    }

    public void startStage(Stage stage) {
        this.stageCurrent = stage;
    }

    public UUID getId() {
        return id;
    }

    public Game getGame() {
        return game;
    }

    public Topic getTopic() {
        return topic;
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
}
