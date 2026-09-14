package com.crystal.kip.content;

import java.util.List;

public class Scenario {

    private Game game;
    private Topic module;
    private List<Stage> stages;

    public Scenario() {}

    public Scenario(Game game, Topic module, List<Stage> stages) {
        this.game = game;
        this.module = module;
        this.stages = stages;
    }

    public Game getGame() {
        return game;
    }

    public Topic getModule() {
        return module;
    }

    public List<Stage> getStages() {
        return stages;
    }
}
