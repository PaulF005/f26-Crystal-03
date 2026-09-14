package com.crystal.kip.content;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private Long id;
    private String title;
    private List<Scenario> scenarios;

    public Game() {
        id = (long) 0;
        title = "";
        scenarios = new ArrayList<>();
    }

    public Game(Long id, String title, List<Scenario> scenarios) {
        this.id = id;
        this.title = title;
        this.scenarios = scenarios;
    }

    public List<Game> getGamesByTopic(Topic topic, List<Game> allGames) {
        List<Game> matchingGames = new ArrayList<>();

        for (Game game : allGames) {
            for (Scenario scenario : game.getScenarios()) {
                if (scenario.getModule().equals(topic)) {
                    matchingGames.add(game);
                    break;
                }
            }
        }

        return matchingGames;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<Scenario> getScenarios() {
        return scenarios;
    }
}
