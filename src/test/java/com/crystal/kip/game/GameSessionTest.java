package com.crystal.kip.game;

import static org.junit.jupiter.api.Assertions.*;

import com.crystal.kip.domain.Game;
import com.crystal.kip.domain.Scenario;
import com.crystal.kip.domain.Topic;
import com.crystal.kip.domain.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameSessionTest {

    private User user;
    private Game game;
    private Topic topic;
    private GameSession session;

    @BeforeEach
    void setUp() {
        user = new User();
        game = new Game();
        topic = new Topic();

        session = new GameSession(user, game, topic);
    }

    @Test
    void testAddScenario() {
        GameScenario scenario = new GameScenario(new Scenario());
        session.addScenario(scenario);

        assertEquals(scenario, session.getScenarios().get(0));
    }

    @Test
    void testAddScenarios() {
        GameScenario scenario1 = new GameScenario(new Scenario());
        GameScenario scenario2 = new GameScenario(new Scenario());
        session.addScenarios(List.of(scenario1, scenario2));

        assertEquals(2, session.getScenarios().size());
        assertEquals(scenario1, session.getScenarios().get(0));
        assertEquals(scenario2, session.getScenarios().get(1));
    }

    @Test
    void testStartScenario() {
        GameScenario scenario = new GameScenario(new Scenario());
        session.startScenario(scenario);

        assertEquals(scenario, session.getCurrentScenario());
    }
}
