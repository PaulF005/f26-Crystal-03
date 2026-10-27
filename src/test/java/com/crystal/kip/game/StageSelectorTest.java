package com.crystal.kip.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.crystal.kip.domain.Answer;
import com.crystal.kip.domain.Scenario;
import com.crystal.kip.domain.Stage;
import com.crystal.kip.domain.enumeration.ScenarioResolution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StageSelectorTest {

    private StageSelector stageSelector;

    @BeforeEach
    void setUp() {
        stageSelector = new StageSelector();
    }

    @Test
    void testSelectFirstReturnsScenarioStartingStage() {
        Stage startingStage = new Stage().id(1L);
        Scenario scenario = new Scenario().id(1L).startingStage(startingStage);

        assertEquals(startingStage, stageSelector.selectFirst(scenario));
    }

    @Test
    void testSelectFirstReturnsNullWithoutStartingStage() {
        Scenario scenario = new Scenario().id(1L);

        assertNull(stageSelector.selectFirst(scenario));
    }

    @Test
    void testSelectNextReturnsAnswersNextStage() {
        Stage nextStage = new Stage().id(2L);
        Answer answer = new Answer().id(1L).nextStage(nextStage);

        assertEquals(nextStage, stageSelector.selectNext(answer));
    }

    @Test
    void testTerminalAnswerDetectedFromResolution() {
        Answer answer = new Answer().id(1L).terminalResolution(ScenarioResolution.POSITIVE);

        assertTrue(stageSelector.isTerminal(answer));
    }

    @Test
    void testNonTerminalAnswerHasNoResolution() {
        Answer answer = new Answer().id(1L).nextStage(new Stage().id(2L));

        assertFalse(stageSelector.isTerminal(answer));
    }
}
