package com.crystal.kip.domain;

import static com.crystal.kip.domain.QuestionTestSamples.*;
import static com.crystal.kip.domain.ScenarioTestSamples.*;
import static com.crystal.kip.domain.StageTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.crystal.kip.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Stage.class);
        Stage stage1 = getStageSample1();
        Stage stage2 = new Stage();
        assertThat(stage1).isNotEqualTo(stage2);

        stage2.setId(stage1.getId());
        assertThat(stage1).isEqualTo(stage2);

        stage2 = getStageSample2();
        assertThat(stage1).isNotEqualTo(stage2);
    }

    @Test
    void questionTest() {
        Stage stage = getStageRandomSampleGenerator();
        Question questionBack = getQuestionRandomSampleGenerator();

        stage.setQuestion(questionBack);
        assertThat(stage.getQuestion()).isEqualTo(questionBack);

        stage.question(null);
        assertThat(stage.getQuestion()).isNull();
    }

    @Test
    void scenarioTest() {
        Stage stage = getStageRandomSampleGenerator();
        Scenario scenarioBack = getScenarioRandomSampleGenerator();

        stage.setScenario(scenarioBack);
        assertThat(stage.getScenario()).isEqualTo(scenarioBack);

        stage.scenario(null);
        assertThat(stage.getScenario()).isNull();
    }
}
