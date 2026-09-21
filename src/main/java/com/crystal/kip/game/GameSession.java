package com.crystal.kip.game;

import com.crystal.kip.domain.Game;
import com.crystal.kip.domain.Topic;
import com.crystal.kip.domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameSession {

    UUID id;
    User user;

    Game game;
    Topic topic;

    List<GameScenario> scenarios;
    GameScenario currentScenario;

    public GameSession(User user, Game game, Topic topic) {
        this.user = user;
        this.game = game;
        this.topic = topic;

        this.id = UUID.randomUUID();
        scenarios = new ArrayList<>();
    }

    public void addScenario(GameScenario scenario) {
        scenarios.add(scenario);
    }

    public void addScenarios(List<GameScenario> scenarios) {
        this.scenarios.addAll(scenarios);
    }

    public void startScenario(GameScenario scenario) {
        this.currentScenario = scenario;
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

    public List<GameScenario> getScenarios() {
        return scenarios;
    }

    public GameScenario getCurrentScenario() {
        return currentScenario;
    }
}
