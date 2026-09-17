package com.crystal.kip.game;

import static org.junit.jupiter.api.Assertions.*;

import com.crystal.kip.content.Game;
import com.crystal.kip.content.Scenario;
import com.crystal.kip.content.Topic;
import com.crystal.kip.domain.User;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class GameEngineTest {

    private GameEngine gameEngine;
    private ScenarioSelector scenarioSelector;
    private GameSession session;

    private User user;
    private Game game;
    private Topic topic;

    @BeforeEach
    void setUp() {
        scenarioSelector = new ScenarioSelector();
        gameEngine = new GameEngine(scenarioSelector);

        user = new User();
        game = new Game();
        topic = new Topic();

        session = gameEngine.startSession(user, game, topic);
    }

    @Test
    void testStartSession() {
        assertNotNull(session);
        assertTrue(game.equals(session.getGame()));
        assertTrue(topic.equals(session.getTopic()));
    }

    @Test
    @Disabled("Not yet implemented")
    void testEndSession() {}

    @Test
    @Disabled("Not yet implemented")
    void testCompleteSession() {}

    @Test
    @Disabled("Not yet implemented")
    void testAbandonSession() {}

    @Test
    @Disabled("Not yet implemented")
    void testStartScenario() {
        Scenario scenario = new Scenario(/*game,*/ topic, new ArrayList<>());
        game.getScenarios().add(scenario);

        gameEngine.startScenario(session);
        assertNotNull(session.getCurrentScenario());
    }

    @Test
    @Disabled("Not yet implemented")
    void testCompleteScenario() {}

    @Test
    @Disabled("Not yet implemented")
    void testAdvanceScenario() {}

    @Test
    @Disabled("Not yet implemented")
    void testAdvanceStage() {}

    @Test
    @Disabled("Not yet implemented")
    void testSelectAnswer() {}

    @Test
    @Disabled("Not yet implemented")
    void testSubmitAnswer() {}
}
