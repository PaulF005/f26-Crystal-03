package com.crystal.kip.domain;

import static com.crystal.kip.domain.AnswerTestSamples.*;
import static com.crystal.kip.domain.ConceptTestSamples.*;
import static com.crystal.kip.domain.QuestionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.crystal.kip.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
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
    void answerTest() {
        Question question = getQuestionRandomSampleGenerator();
        Answer answerBack = getAnswerRandomSampleGenerator();

        question.addAnswer(answerBack);
        assertThat(question.getAnswers()).containsOnly(answerBack);
        assertThat(answerBack.getQuestion()).isEqualTo(question);

        question.removeAnswer(answerBack);
        assertThat(question.getAnswers()).doesNotContain(answerBack);
        assertThat(answerBack.getQuestion()).isNull();

        question.answers(new HashSet<>(Set.of(answerBack)));
        assertThat(question.getAnswers()).containsOnly(answerBack);
        assertThat(answerBack.getQuestion()).isEqualTo(question);

        question.setAnswers(new HashSet<>());
        assertThat(question.getAnswers()).doesNotContain(answerBack);
        assertThat(answerBack.getQuestion()).isNull();
    }

    @Test
    void conceptTest() {
        Question question = getQuestionRandomSampleGenerator();
        Concept conceptBack = getConceptRandomSampleGenerator();

        question.setConcept(conceptBack);
        assertThat(question.getConcept()).isEqualTo(conceptBack);

        question.concept(null);
        assertThat(question.getConcept()).isNull();
    }
}
