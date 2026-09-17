package com.crystal.kip.domain;

import static com.crystal.kip.domain.ConceptTestSamples.*;
import static com.crystal.kip.domain.TopicTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.crystal.kip.web.rest.TestUtil;
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
    void conceptTest() {
        Topic topic = getTopicRandomSampleGenerator();
        Concept conceptBack = getConceptRandomSampleGenerator();

        topic.addConcept(conceptBack);
        assertThat(topic.getConcepts()).containsOnly(conceptBack);
        assertThat(conceptBack.getTopic()).isEqualTo(topic);

        topic.removeConcept(conceptBack);
        assertThat(topic.getConcepts()).doesNotContain(conceptBack);
        assertThat(conceptBack.getTopic()).isNull();

        topic.concepts(new HashSet<>(Set.of(conceptBack)));
        assertThat(topic.getConcepts()).containsOnly(conceptBack);
        assertThat(conceptBack.getTopic()).isEqualTo(topic);

        topic.setConcepts(new HashSet<>());
        assertThat(topic.getConcepts()).doesNotContain(conceptBack);
        assertThat(conceptBack.getTopic()).isNull();
    }
}
