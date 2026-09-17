package com.crystal.kip.domain;

import static com.crystal.kip.domain.AnswerTestSamples.*;
import static com.crystal.kip.domain.FeedbackTestSamples.*;
import static com.crystal.kip.domain.QuestionTestSamples.*;
import static com.crystal.kip.domain.StageTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.crystal.kip.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AnswerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Answer.class);
        Answer answer1 = getAnswerSample1();
        Answer answer2 = new Answer();
        assertThat(answer1).isNotEqualTo(answer2);

        answer2.setId(answer1.getId());
        assertThat(answer1).isEqualTo(answer2);

        answer2 = getAnswerSample2();
        assertThat(answer1).isNotEqualTo(answer2);
    }

    @Test
    void nextStageTest() {
        Answer answer = getAnswerRandomSampleGenerator();
        Stage stageBack = getStageRandomSampleGenerator();

        answer.setNextStage(stageBack);
        assertThat(answer.getNextStage()).isEqualTo(stageBack);

        answer.nextStage(null);
        assertThat(answer.getNextStage()).isNull();
    }

    @Test
    void feedbackTest() {
        Answer answer = getAnswerRandomSampleGenerator();
        Feedback feedbackBack = getFeedbackRandomSampleGenerator();

        answer.setFeedback(feedbackBack);
        assertThat(answer.getFeedback()).isEqualTo(feedbackBack);

        answer.feedback(null);
        assertThat(answer.getFeedback()).isNull();
    }

    @Test
    void questionTest() {
        Answer answer = getAnswerRandomSampleGenerator();
        Question questionBack = getQuestionRandomSampleGenerator();

        answer.setQuestion(questionBack);
        assertThat(answer.getQuestion()).isEqualTo(questionBack);

        answer.question(null);
        assertThat(answer.getQuestion()).isNull();
    }
}
