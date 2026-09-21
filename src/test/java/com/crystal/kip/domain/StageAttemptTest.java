package com.crystal.kip.domain;

import static com.crystal.kip.domain.AnswerTestSamples.*;
import static com.crystal.kip.domain.GameSessionTestSamples.*;
import static com.crystal.kip.domain.StageAttemptTestSamples.*;
import static com.crystal.kip.domain.StageTestSamples.*;
import static com.crystal.kip.domain.UserProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.crystal.kip.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StageAttemptTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StageAttempt.class);
        StageAttempt stageAttempt1 = getStageAttemptSample1();
        StageAttempt stageAttempt2 = new StageAttempt();
        assertThat(stageAttempt1).isNotEqualTo(stageAttempt2);

        stageAttempt2.setId(stageAttempt1.getId());
        assertThat(stageAttempt1).isEqualTo(stageAttempt2);

        stageAttempt2 = getStageAttemptSample2();
        assertThat(stageAttempt1).isNotEqualTo(stageAttempt2);
    }

    @Test
    void userTest() {
        StageAttempt stageAttempt = getStageAttemptRandomSampleGenerator();
        UserProfile userProfileBack = getUserProfileRandomSampleGenerator();

        stageAttempt.setUser(userProfileBack);
        assertThat(stageAttempt.getUser()).isEqualTo(userProfileBack);

        stageAttempt.user(null);
        assertThat(stageAttempt.getUser()).isNull();
    }

    @Test
    void stageTest() {
        StageAttempt stageAttempt = getStageAttemptRandomSampleGenerator();
        Stage stageBack = getStageRandomSampleGenerator();

        stageAttempt.setStage(stageBack);
        assertThat(stageAttempt.getStage()).isEqualTo(stageBack);

        stageAttempt.stage(null);
        assertThat(stageAttempt.getStage()).isNull();
    }

    @Test
    void selectedAnswerTest() {
        StageAttempt stageAttempt = getStageAttemptRandomSampleGenerator();
        Answer answerBack = getAnswerRandomSampleGenerator();

        stageAttempt.setSelectedAnswer(answerBack);
        assertThat(stageAttempt.getSelectedAnswer()).isEqualTo(answerBack);

        stageAttempt.selectedAnswer(null);
        assertThat(stageAttempt.getSelectedAnswer()).isNull();
    }

    @Test
    void gameSessionTest() {
        StageAttempt stageAttempt = getStageAttemptRandomSampleGenerator();
        GameSession gameSessionBack = getGameSessionRandomSampleGenerator();

        stageAttempt.setGameSession(gameSessionBack);
        assertThat(stageAttempt.getGameSession()).isEqualTo(gameSessionBack);

        stageAttempt.gameSession(null);
        assertThat(stageAttempt.getGameSession()).isNull();
    }
}
