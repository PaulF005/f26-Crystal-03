package com.myapp.domain;

import static com.myapp.domain.LegalContentTestSamples.*;
import static com.myapp.domain.ModuleTestSamples.*;
import static com.myapp.domain.QuestionTestSamples.*;
import static com.myapp.domain.TopicTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TopicTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Topic.class);
        Topic topic1 = getTopicSample1();
        Topic topic2 = new Topic();
        assertThat(topic1).isNotEqualTo(topic2);

        topic2.setId(topic1.getId());
        assertThat(topic1).isEqualTo(topic2);

        topic2 = getTopicSample2();
        assertThat(topic1).isNotEqualTo(topic2);
    }

    @Test
    void questionTest() {
        Topic topic = getTopicRandomSampleGenerator();
        Question questionBack = getQuestionRandomSampleGenerator();

        topic.addQuestion(questionBack);
        assertThat(topic.getQuestions()).containsOnly(questionBack);
        assertThat(questionBack.getTopic()).isEqualTo(topic);

        topic.removeQuestion(questionBack);
        assertThat(topic.getQuestions()).doesNotContain(questionBack);
        assertThat(questionBack.getTopic()).isNull();

        topic.questions(new HashSet<>(Set.of(questionBack)));
        assertThat(topic.getQuestions()).containsOnly(questionBack);
        assertThat(questionBack.getTopic()).isEqualTo(topic);

        topic.setQuestions(new HashSet<>());
        assertThat(topic.getQuestions()).doesNotContain(questionBack);
        assertThat(questionBack.getTopic()).isNull();
    }

    @Test
    void legalContentTest() {
        Topic topic = getTopicRandomSampleGenerator();
        LegalContent legalContentBack = getLegalContentRandomSampleGenerator();

        topic.setLegalContent(legalContentBack);
        assertThat(topic.getLegalContent()).isEqualTo(legalContentBack);

        topic.legalContent(null);
        assertThat(topic.getLegalContent()).isNull();
    }

    @Test
    void moduleTest() {
        Topic topic = getTopicRandomSampleGenerator();
        Module moduleBack = getModuleRandomSampleGenerator();

        topic.setModule(moduleBack);
        assertThat(topic.getModule()).isEqualTo(moduleBack);

        topic.module(null);
        assertThat(topic.getModule()).isNull();
    }
}
