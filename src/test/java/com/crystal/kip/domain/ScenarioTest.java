package com.crystal.kip.domain;

import static com.crystal.kip.domain.GameTestSamples.*;
import static com.crystal.kip.domain.ScenarioTestSamples.*;
import static com.crystal.kip.domain.StageTestSamples.*;
import static com.crystal.kip.domain.TopicTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.crystal.kip.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ScenarioTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Scenario.class);
        Scenario scenario1 = getScenarioSample1();
        Scenario scenario2 = new Scenario();
        assertThat(scenario1).isNotEqualTo(scenario2);

        scenario2.setId(scenario1.getId());
        assertThat(scenario1).isEqualTo(scenario2);

        scenario2 = getScenarioSample2();
        assertThat(scenario1).isNotEqualTo(scenario2);
    }

    @Test
    void stageTest() {
        Scenario scenario = getScenarioRandomSampleGenerator();
        Stage stageBack = getStageRandomSampleGenerator();

        scenario.addStage(stageBack);
        assertThat(scenario.getStages()).containsOnly(stageBack);
        assertThat(stageBack.getScenario()).isEqualTo(scenario);

        scenario.removeStage(stageBack);
        assertThat(scenario.getStages()).doesNotContain(stageBack);
        assertThat(stageBack.getScenario()).isNull();

        scenario.stages(new HashSet<>(Set.of(stageBack)));
        assertThat(scenario.getStages()).containsOnly(stageBack);
        assertThat(stageBack.getScenario()).isEqualTo(scenario);

        scenario.setStages(new HashSet<>());
        assertThat(scenario.getStages()).doesNotContain(stageBack);
        assertThat(stageBack.getScenario()).isNull();
    }

    @Test
    void startingStageTest() {
        Scenario scenario = getScenarioRandomSampleGenerator();
        Stage stageBack = getStageRandomSampleGenerator();

        scenario.setStartingStage(stageBack);
        assertThat(scenario.getStartingStage()).isEqualTo(stageBack);

        scenario.startingStage(null);
        assertThat(scenario.getStartingStage()).isNull();
    }

    @Test
    void topicTest() {
        Scenario scenario = getScenarioRandomSampleGenerator();
        Topic topicBack = getTopicRandomSampleGenerator();

        scenario.setTopic(topicBack);
        assertThat(scenario.getTopic()).isEqualTo(topicBack);

        scenario.topic(null);
        assertThat(scenario.getTopic()).isNull();
    }

    @Test
    void gameTest() {
        Scenario scenario = getScenarioRandomSampleGenerator();
        Game gameBack = getGameRandomSampleGenerator();

        scenario.setGame(gameBack);
        assertThat(scenario.getGame()).isEqualTo(gameBack);

        scenario.game(null);
        assertThat(scenario.getGame()).isNull();
    }
}
