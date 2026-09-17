package com.crystal.kip.game;

import com.crystal.kip.content.Game;
import com.crystal.kip.content.Scenario;
import com.crystal.kip.content.Topic;
import com.crystal.kip.domain.User;
import org.springframework.stereotype.Service;

@Service
public class GameEngine {

    private final ScenarioSelector scenarioSelector;

    public GameEngine(ScenarioSelector scenarioSelector) {
        this.scenarioSelector = scenarioSelector;
    }

    public GameSession startSession(User user, Game game, Topic topic) {
        return new GameSession(user, game, topic);
    }

    private void endSession(GameSession session) {}

    public void completeSession(GameSession session) {
        // commit results to db
        endSession(session);
    }

    public void abandonSession(GameSession session) {
        // discard results
        endSession(session);
    }

    public void startScenario(GameSession session) {
        Scenario scenario = scenarioSelector.selectNext(session);

        // TODO: actually deal with this
        if (scenario == null) {
            throw new NullPointerException("Scenario is null for this game session");
        }

        GameScenario gameScenario = new GameScenario(scenario);
        session.startScenario(gameScenario);
    }

    public void completeScenario(GameSession session) {}

    public void advanceScenario(GameSession session) {}

    public void advanceStage(GameSession session) {}

    public void selectAnswer(GameSession session, int answerIndex) {
        submitAnswer(session);
    }

    public void submitAnswer(GameSession session) {}
}
