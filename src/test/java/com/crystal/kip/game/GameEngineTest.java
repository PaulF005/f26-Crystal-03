package com.crystal.kip.game;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.crystal.kip.domain.Game;
import com.crystal.kip.domain.Scenario;
import com.crystal.kip.domain.Topic;
import com.crystal.kip.domain.User;
import com.crystal.kip.repository.LinkRepo.LinkScenarioRepository;
import com.crystal.kip.repository.ScenarioRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class GameEngineTest {

    private GameEngine gameEngine;
    private ScenarioSelector scenarioSelector;
    private LinkScenarioRepository linkScenarioRepository;
    private GameSession session;

    private User user;
    private Game game;
    private Topic topic;

    @BeforeEach
    void setUp() {
        linkScenarioRepository = mock(LinkScenarioRepository.class);

        scenarioSelector = new ScenarioSelector(linkScenarioRepository);
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
        Scenario scenario = new Scenario().name("Test Scenario").game(game).topic(topic);

        when(linkScenarioRepository.findByGameIdAndTopicId(game.getId(), topic.getId())).thenReturn(List.of(scenario));

        gameEngine.startScenario(session);

        assertEquals(scenario, session.getCurrentScenario());
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
