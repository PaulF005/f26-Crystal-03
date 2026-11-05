package com.crystal.kip.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.crystal.kip.domain.Answer;
import com.crystal.kip.domain.Game;
import com.crystal.kip.domain.Question;
import com.crystal.kip.domain.Scenario;
import com.crystal.kip.domain.Stage;
import com.crystal.kip.domain.Topic;
import com.crystal.kip.domain.UserProfile;
import com.crystal.kip.domain.enumeration.ScenarioResolution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameActiveTest {

    private UserProfile user;
    private Game game;
    private Topic topic;
    private GameActive active;

    @BeforeEach
    void setUp() {
        user = new UserProfile().id(1L);
        game = new Game().id(1L);
        topic = new Topic().id(1L);

        active = new GameActive(user, game, topic);
    }

    @Test
    void testConstructorInitializesSessionState() {
        assertNotNull(active.getId());
        assertNotNull(active.getStartedAt());
        assertEquals(user, active.getUser());
        assertEquals(game, active.getGame());
        assertEquals(topic, active.getTopic());
        assertTrue(active.getScenariosPlayed().isEmpty());
        assertTrue(active.getScenarioResults().isEmpty());
        assertTrue(active.getStageResults().isEmpty());
        assertNull(active.getScenarioCurrent());
        assertNull(active.getStageCurrent());
    }

    @Test
    void testStartScenarioSetsCurrentAndTracksPlayed() {
        Scenario scenario = new Scenario().id(1L);

        active.startScenario(scenario);

        assertEquals(scenario, active.getScenarioCurrent());
        assertEquals(1, active.getScenariosPlayed().size());
        assertEquals(scenario, active.getScenariosPlayed().get(0));
    }

    @Test
    void testCannotStartSecondScenarioWhileOneIsActive() {
        Scenario scenario1 = new Scenario().id(1L);
        Scenario scenario2 = new Scenario().id(2L);

        active.startScenario(scenario1);

        assertThrows(IllegalStateException.class, () -> active.startScenario(scenario2));
        assertEquals(scenario1, active.getScenarioCurrent());
        assertEquals(1, active.getScenariosPlayed().size());
    }

    @Test
    void testCannotReplayScenarioAfterItCompletes() {
        Scenario scenario = new Scenario().id(1L);

        active.startScenario(scenario);
        active.recordScenarioResult(ScenarioResolution.POSITIVE);

        assertThrows(IllegalArgumentException.class, () -> active.startScenario(scenario));
    }

    @Test
    void testStartStageRequiresActiveScenario() {
        Scenario scenario = new Scenario().id(1L);
        Stage stage = new Stage().id(1L).scenario(scenario);

        assertThrows(IllegalStateException.class, () -> active.startStage(stage));
    }

    @Test
    void testStartStageRequiresStageFromCurrentScenario() {
        Scenario currentScenario = new Scenario().id(1L);
        Scenario otherScenario = new Scenario().id(2L);
        Stage stage = new Stage().id(1L).scenario(otherScenario);

        active.startScenario(currentScenario);

        assertThrows(IllegalArgumentException.class, () -> active.startStage(stage));
    }

    @Test
    void testStartStageSetsCurrentStage() {
        Scenario scenario = new Scenario().id(1L);
        Stage stage = new Stage().id(1L).scenario(scenario);

        active.startScenario(scenario);
        active.startStage(stage);

        assertEquals(stage, active.getStageCurrent());
    }

    @Test
    void testRecordStageResultStoresAnswerAndCorrectness() {
        Scenario scenario = new Scenario().id(1L);
        Question question = new Question().id(1L);
        Stage stage = new Stage().id(1L).scenario(scenario).question(question);
        Answer answer = new Answer().id(1L).question(question).correct(true);

        active.startScenario(scenario);
        active.startStage(stage);
        active.recordStageResult(answer);

        assertEquals(1, active.getStageResults().size());
        StageResult result = active.getStageResults().get(0);
        assertEquals(stage, result.getStage());
        assertEquals(answer, result.getSelectedAnswer());
        assertTrue(result.isCorrect());
        assertNotNull(result.getAnsweredAt());
    }

    @Test
    void testRecordStageResultRequiresActiveStage() {
        Answer answer = new Answer().id(1L);

        assertThrows(IllegalStateException.class, () -> active.recordStageResult(answer));
    }

    @Test
    void testRecordScenarioResultStoresResolutionAndClearsCurrentState() {
        Scenario scenario = new Scenario().id(1L);
        Stage stage = new Stage().id(1L).scenario(scenario);

        active.startScenario(scenario);
        active.startStage(stage);
        active.recordScenarioResult(ScenarioResolution.NEGATIVE);

        assertEquals(1, active.getScenarioResults().size());
        assertEquals(scenario, active.getScenarioResults().get(0).getScenario());
        assertEquals(ScenarioResolution.NEGATIVE, active.getScenarioResults().get(0).getResolution());
        assertNull(active.getScenarioCurrent());
        assertNull(active.getStageCurrent());
    }

    @Test
    void testRecordScenarioResultRequiresActiveScenario() {
        assertThrows(IllegalStateException.class, () -> active.recordScenarioResult(ScenarioResolution.POSITIVE));
    }

    @Test
    void testRecordScenarioResultRequiresResolution() {
        Scenario scenario = new Scenario().id(1L);
        active.startScenario(scenario);

        assertThrows(IllegalArgumentException.class, () -> active.recordScenarioResult(null));
    }

    @Test
    void testCompletedScenarioAllowsDifferentScenarioToStart() {
        Scenario scenario1 = new Scenario().id(1L);
        Scenario scenario2 = new Scenario().id(2L);

        active.startScenario(scenario1);
        active.recordScenarioResult(ScenarioResolution.INCONCLUSIVE);
        active.startScenario(scenario2);

        assertEquals(scenario2, active.getScenarioCurrent());
        assertEquals(2, active.getScenariosPlayed().size());
        assertFalse(active.getScenariosPlayed().isEmpty());
    }
}
