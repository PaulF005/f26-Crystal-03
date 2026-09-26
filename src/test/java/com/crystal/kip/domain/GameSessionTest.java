package com.crystal.kip.domain;

import static com.crystal.kip.domain.GameSessionTestSamples.*;
import static com.crystal.kip.domain.GameTestSamples.*;
import static com.crystal.kip.domain.StageAttemptTestSamples.*;
import static com.crystal.kip.domain.UserProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.crystal.kip.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class GameSessionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GameSession.class);
        GameSession gameSession1 = getGameSessionSample1();
        GameSession gameSession2 = new GameSession();
        assertThat(gameSession1).isNotEqualTo(gameSession2);

        gameSession2.setId(gameSession1.getId());
        assertThat(gameSession1).isEqualTo(gameSession2);

        gameSession2 = getGameSessionSample2();
        assertThat(gameSession1).isNotEqualTo(gameSession2);
    }

    @Test
    void stageAttemptTest() {
        GameSession gameSession = getGameSessionRandomSampleGenerator();
        StageAttempt stageAttemptBack = getStageAttemptRandomSampleGenerator();

        gameSession.addStageAttempt(stageAttemptBack);
        assertThat(gameSession.getStageAttempts()).containsOnly(stageAttemptBack);
        assertThat(stageAttemptBack.getGameSession()).isEqualTo(gameSession);

        gameSession.removeStageAttempt(stageAttemptBack);
        assertThat(gameSession.getStageAttempts()).doesNotContain(stageAttemptBack);
        assertThat(stageAttemptBack.getGameSession()).isNull();

        gameSession.stageAttempts(new HashSet<>(Set.of(stageAttemptBack)));
        assertThat(gameSession.getStageAttempts()).containsOnly(stageAttemptBack);
        assertThat(stageAttemptBack.getGameSession()).isEqualTo(gameSession);

        gameSession.setStageAttempts(new HashSet<>());
        assertThat(gameSession.getStageAttempts()).doesNotContain(stageAttemptBack);
        assertThat(stageAttemptBack.getGameSession()).isNull();
    }

    @Test
    void userTest() {
        GameSession gameSession = getGameSessionRandomSampleGenerator();
        UserProfile userProfileBack = getUserProfileRandomSampleGenerator();

        gameSession.setUser(userProfileBack);
        assertThat(gameSession.getUser()).isEqualTo(userProfileBack);

        gameSession.user(null);
        assertThat(gameSession.getUser()).isNull();
    }

    @Test
    void gameTest() {
        GameSession gameSession = getGameSessionRandomSampleGenerator();
        Game gameBack = getGameRandomSampleGenerator();

        gameSession.setGame(gameBack);
        assertThat(gameSession.getGame()).isEqualTo(gameBack);

        gameSession.game(null);
        assertThat(gameSession.getGame()).isNull();
    }
}
