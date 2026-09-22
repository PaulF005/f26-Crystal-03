package com.crystal.kip.domain;

import static com.crystal.kip.domain.GameProgressTestSamples.*;
import static com.crystal.kip.domain.GameTestSamples.*;
import static com.crystal.kip.domain.UserProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.crystal.kip.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GameProgressTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GameProgress.class);
        GameProgress gameProgress1 = getGameProgressSample1();
        GameProgress gameProgress2 = new GameProgress();
        assertThat(gameProgress1).isNotEqualTo(gameProgress2);

        gameProgress2.setId(gameProgress1.getId());
        assertThat(gameProgress1).isEqualTo(gameProgress2);

        gameProgress2 = getGameProgressSample2();
        assertThat(gameProgress1).isNotEqualTo(gameProgress2);
    }

    @Test
    void gameTest() {
        GameProgress gameProgress = getGameProgressRandomSampleGenerator();
        Game gameBack = getGameRandomSampleGenerator();

        gameProgress.setGame(gameBack);
        assertThat(gameProgress.getGame()).isEqualTo(gameBack);

        gameProgress.game(null);
        assertThat(gameProgress.getGame()).isNull();
    }

    @Test
    void userProfileTest() {
        GameProgress gameProgress = getGameProgressRandomSampleGenerator();
        UserProfile userProfileBack = getUserProfileRandomSampleGenerator();

        gameProgress.setUserProfile(userProfileBack);
        assertThat(gameProgress.getUserProfile()).isEqualTo(userProfileBack);

        gameProgress.userProfile(null);
        assertThat(gameProgress.getUserProfile()).isNull();
    }
}
