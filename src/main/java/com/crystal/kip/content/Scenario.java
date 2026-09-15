package com.crystal.kip.content;

import java.util.List;

public class Scenario extends Content {

    private Game game;
    private Topic topic;
    private List<Stage> stages;

    public Scenario() {}

    public Scenario(Game game, Topic topic, List<Stage> stages) {
        this.game = game;
        this.topic = topic;
        this.stages = stages;
    }

    public Game getGame() {
        return game;
    }

    public Topic getTopic() {
        return topic;
    }

    public List<Stage> getStages() {
        return stages;
    }
}
