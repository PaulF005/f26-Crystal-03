package com.crystal.kip.game;

import com.crystal.kip.content.Game;
import com.crystal.kip.content.Scenario;
import com.crystal.kip.content.Topic;

public class ScenarioSelector {

    public Scenario selectNext(GameSession session) {
        Game game = session.getGame();
        Topic topic = session.getTopic();

        // choose from scenarios that are eligible with the given game and topic
        // TODO: what else determines eligibility tbd
        for (Scenario scenario : game.getScenarios()) {
            if (scenario.getTopic().equals(topic)) {
                return scenario;
            }
        }

        return null;
    }
}
