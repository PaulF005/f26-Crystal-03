package com.crystal.kip.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.crystal.kip.domain.Game;
import com.crystal.kip.domain.Scenario;
import com.crystal.kip.domain.Stage;
import com.crystal.kip.domain.Topic;
import com.crystal.kip.domain.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameActiveTest {

    private UserProfile user;
    private Game game;
    private Topic topic;
    private GameActive active;

    @BeforeEach
    void setUp() {
        user = new UserProfile();
        game = new Game();
        topic = new Topic();

        active = new GameActive(user, game, topic);
    }

    @Test
    void testStartScenario() {
        Scenario scenario = new Scenario();

        active.startScenario(scenario);

        assertEquals(scenario, active.getScenarioCurrent());
    }

    @Test
    void testStartScenarioAddsScenarioToPlayed() {
        Scenario scenario = new Scenario();

        active.startScenario(scenario);

        assertEquals(1, active.getScenariosPlayed().size());
        assertEquals(scenario, active.getScenariosPlayed().get(0));
    }

    @Test
    void testStartMultipleScenariosTracksPlayedScenarios() {
        Scenario scenario1 = new Scenario();
        Scenario scenario2 = new Scenario();

        active.startScenario(scenario1);
        active.startScenario(scenario2);

        assertEquals(2, active.getScenariosPlayed().size());
        assertEquals(scenario1, active.getScenariosPlayed().get(0));
        assertEquals(scenario2, active.getScenariosPlayed().get(1));

        // The most recently started Scenario should be the current Scenario
        assertEquals(scenario2, active.getScenarioCurrent());
    }

    @Test
    void testStartStage() {
        Stage stage = new Stage();

        active.startStage(stage);

        assertEquals(stage, active.getStageCurrent());
    }

    @Test
    void testGameAndTopic() {
        assertEquals(game, active.getGame());
        assertEquals(topic, active.getTopic());
    }

    @Test
    void testIdGenerated() {
        assertNotNull(active.getId());
    }
}
