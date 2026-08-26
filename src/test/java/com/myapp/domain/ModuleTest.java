package com.myapp.domain;

import static com.myapp.domain.ModuleTestSamples.*;
import static com.myapp.domain.TopicTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ModuleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Module.class);
        Module module1 = getModuleSample1();
        Module module2 = new Module();
        assertThat(module1).isNotEqualTo(module2);

        module2.setId(module1.getId());
        assertThat(module1).isEqualTo(module2);

        module2 = getModuleSample2();
        assertThat(module1).isNotEqualTo(module2);
    }

    @Test
    void topicTest() {
        Module module = getModuleRandomSampleGenerator();
        Topic topicBack = getTopicRandomSampleGenerator();

        module.addTopic(topicBack);
        assertThat(module.getTopics()).containsOnly(topicBack);
        assertThat(topicBack.getModule()).isEqualTo(module);

        module.removeTopic(topicBack);
        assertThat(module.getTopics()).doesNotContain(topicBack);
        assertThat(topicBack.getModule()).isNull();

        module.topics(new HashSet<>(Set.of(topicBack)));
        assertThat(module.getTopics()).containsOnly(topicBack);
        assertThat(topicBack.getModule()).isEqualTo(module);

        module.setTopics(new HashSet<>());
        assertThat(module.getTopics()).doesNotContain(topicBack);
        assertThat(topicBack.getModule()).isNull();
    }
}
