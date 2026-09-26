package com.crystal.kip.domain;

import static com.crystal.kip.domain.ConceptProgressTestSamples.*;
import static com.crystal.kip.domain.ConceptTestSamples.*;
import static com.crystal.kip.domain.LegalContentTestSamples.*;
import static com.crystal.kip.domain.QuestionTestSamples.*;
import static com.crystal.kip.domain.TopicTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.crystal.kip.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ConceptTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Concept.class);
        Concept concept1 = getConceptSample1();
        Concept concept2 = new Concept();
        assertThat(concept1).isNotEqualTo(concept2);

        concept2.setId(concept1.getId());
        assertThat(concept1).isEqualTo(concept2);

        concept2 = getConceptSample2();
        assertThat(concept1).isNotEqualTo(concept2);
    }

    @Test
    void questionTest() {
        Concept concept = getConceptRandomSampleGenerator();
        Question questionBack = getQuestionRandomSampleGenerator();

        concept.addQuestion(questionBack);
        assertThat(concept.getQuestions()).containsOnly(questionBack);
        assertThat(questionBack.getConcept()).isEqualTo(concept);

        concept.removeQuestion(questionBack);
        assertThat(concept.getQuestions()).doesNotContain(questionBack);
        assertThat(questionBack.getConcept()).isNull();

        concept.questions(new HashSet<>(Set.of(questionBack)));
        assertThat(concept.getQuestions()).containsOnly(questionBack);
        assertThat(questionBack.getConcept()).isEqualTo(concept);

        concept.setQuestions(new HashSet<>());
        assertThat(concept.getQuestions()).doesNotContain(questionBack);
        assertThat(questionBack.getConcept()).isNull();
    }

    @Test
    void conceptProgressesTest() {
        Concept concept = getConceptRandomSampleGenerator();
        ConceptProgress conceptProgressBack = getConceptProgressRandomSampleGenerator();

        concept.addConceptProgresses(conceptProgressBack);
        assertThat(concept.getConceptProgresseses()).containsOnly(conceptProgressBack);
        assertThat(conceptProgressBack.getConcept()).isEqualTo(concept);

        concept.removeConceptProgresses(conceptProgressBack);
        assertThat(concept.getConceptProgresseses()).doesNotContain(conceptProgressBack);
        assertThat(conceptProgressBack.getConcept()).isNull();

        concept.conceptProgresseses(new HashSet<>(Set.of(conceptProgressBack)));
        assertThat(concept.getConceptProgresseses()).containsOnly(conceptProgressBack);
        assertThat(conceptProgressBack.getConcept()).isEqualTo(concept);

        concept.setConceptProgresseses(new HashSet<>());
        assertThat(concept.getConceptProgresseses()).doesNotContain(conceptProgressBack);
        assertThat(conceptProgressBack.getConcept()).isNull();
    }

    @Test
    void legalContentTest() {
        Concept concept = getConceptRandomSampleGenerator();
        LegalContent legalContentBack = getLegalContentRandomSampleGenerator();

        concept.setLegalContent(legalContentBack);
        assertThat(concept.getLegalContent()).isEqualTo(legalContentBack);

        concept.legalContent(null);
        assertThat(concept.getLegalContent()).isNull();
    }

    @Test
    void topicTest() {
        Concept concept = getConceptRandomSampleGenerator();
        Topic topicBack = getTopicRandomSampleGenerator();

        concept.setTopic(topicBack);
        assertThat(concept.getTopic()).isEqualTo(topicBack);

        concept.topic(null);
        assertThat(concept.getTopic()).isNull();
    }
}
