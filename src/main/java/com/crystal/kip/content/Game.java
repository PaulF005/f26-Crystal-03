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
