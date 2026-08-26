package com.myapp.domain;

import static com.myapp.domain.QuestionTestSamples.*;
import static com.myapp.domain.TopicTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class QuestionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Question.class);
        Question question1 = getQuestionSample1();
        Question question2 = new Question();
        assertThat(question1).isNotEqualTo(question2);

        question2.setId(question1.getId());
        assertThat(question1).isEqualTo(question2);

        question2 = getQuestionSample2();
        assertThat(question1).isNotEqualTo(question2);
    }

    @Test
    void topicTest() {
        Question question = getQuestionRandomSampleGenerator();
        Topic topicBack = getTopicRandomSampleGenerator();

        question.setTopic(topicBack);
        assertThat(question.getTopic()).isEqualTo(topicBack);

        question.topic(null);
        assertThat(question.getTopic()).isNull();
    }
}
