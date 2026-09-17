package com.crystal.kip.domain;

import static com.crystal.kip.domain.GameTestSamples.*;
import static com.crystal.kip.domain.ScenarioTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.crystal.kip.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class GameTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Game.class);
        Game game1 = getGameSample1();
        Game game2 = new Game();
        assertThat(game1).isNotEqualTo(game2);

        game2.setId(game1.getId());
        assertThat(game1).isEqualTo(game2);

        game2 = getGameSample2();
        assertThat(game1).isNotEqualTo(game2);
    }

    @Test
    void scenarioTest() {
        Game game = getGameRandomSampleGenerator();
        Scenario scenarioBack = getScenarioRandomSampleGenerator();

        game.addScenario(scenarioBack);
        assertThat(game.getScenarios()).containsOnly(scenarioBack);
        assertThat(scenarioBack.getGame()).isEqualTo(game);

        game.removeScenario(scenarioBack);
        assertThat(game.getScenarios()).doesNotContain(scenarioBack);
        assertThat(scenarioBack.getGame()).isNull();

        game.scenarios(new HashSet<>(Set.of(scenarioBack)));
        assertThat(game.getScenarios()).containsOnly(scenarioBack);
        assertThat(scenarioBack.getGame()).isEqualTo(game);

        game.setScenarios(new HashSet<>());
        assertThat(game.getScenarios()).doesNotContain(scenarioBack);
        assertThat(scenarioBack.getGame()).isNull();
    }
}
