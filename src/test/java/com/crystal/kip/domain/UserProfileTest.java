package com.crystal.kip.domain;

import static com.crystal.kip.domain.ConceptProgressTestSamples.*;
import static com.crystal.kip.domain.GameProgressTestSamples.*;
import static com.crystal.kip.domain.TopicProgressTestSamples.*;
import static com.crystal.kip.domain.UserProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.crystal.kip.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UserProfileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserProfile.class);
        UserProfile userProfile1 = getUserProfileSample1();
        UserProfile userProfile2 = new UserProfile();
        assertThat(userProfile1).isNotEqualTo(userProfile2);

        userProfile2.setId(userProfile1.getId());
        assertThat(userProfile1).isEqualTo(userProfile2);

        userProfile2 = getUserProfileSample2();
        assertThat(userProfile1).isNotEqualTo(userProfile2);
    }

    @Test
    void topicProgressesTest() {
        UserProfile userProfile = getUserProfileRandomSampleGenerator();
        TopicProgress topicProgressBack = getTopicProgressRandomSampleGenerator();

        userProfile.addTopicProgresses(topicProgressBack);
        assertThat(userProfile.getTopicProgresseses()).containsOnly(topicProgressBack);
        assertThat(topicProgressBack.getUserProfile()).isEqualTo(userProfile);

        userProfile.removeTopicProgresses(topicProgressBack);
        assertThat(userProfile.getTopicProgresseses()).doesNotContain(topicProgressBack);
        assertThat(topicProgressBack.getUserProfile()).isNull();

        userProfile.topicProgresseses(new HashSet<>(Set.of(topicProgressBack)));
        assertThat(userProfile.getTopicProgresseses()).containsOnly(topicProgressBack);
        assertThat(topicProgressBack.getUserProfile()).isEqualTo(userProfile);

        userProfile.setTopicProgresseses(new HashSet<>());
        assertThat(userProfile.getTopicProgresseses()).doesNotContain(topicProgressBack);
        assertThat(topicProgressBack.getUserProfile()).isNull();
    }

    @Test
    void gameProgressesTest() {
        UserProfile userProfile = getUserProfileRandomSampleGenerator();
        GameProgress gameProgressBack = getGameProgressRandomSampleGenerator();

        userProfile.addGameProgresses(gameProgressBack);
        assertThat(userProfile.getGameProgresseses()).containsOnly(gameProgressBack);
        assertThat(gameProgressBack.getUserProfile()).isEqualTo(userProfile);

        userProfile.removeGameProgresses(gameProgressBack);
        assertThat(userProfile.getGameProgresseses()).doesNotContain(gameProgressBack);
        assertThat(gameProgressBack.getUserProfile()).isNull();

        userProfile.gameProgresseses(new HashSet<>(Set.of(gameProgressBack)));
        assertThat(userProfile.getGameProgresseses()).containsOnly(gameProgressBack);
        assertThat(gameProgressBack.getUserProfile()).isEqualTo(userProfile);

        userProfile.setGameProgresseses(new HashSet<>());
        assertThat(userProfile.getGameProgresseses()).doesNotContain(gameProgressBack);
        assertThat(gameProgressBack.getUserProfile()).isNull();
    }

    @Test
    void conceptProgressesTest() {
        UserProfile userProfile = getUserProfileRandomSampleGenerator();
        ConceptProgress conceptProgressBack = getConceptProgressRandomSampleGenerator();

        userProfile.addConceptProgresses(conceptProgressBack);
        assertThat(userProfile.getConceptProgresseses()).containsOnly(conceptProgressBack);
        assertThat(conceptProgressBack.getUserProfile()).isEqualTo(userProfile);

        userProfile.removeConceptProgresses(conceptProgressBack);
        assertThat(userProfile.getConceptProgresseses()).doesNotContain(conceptProgressBack);
        assertThat(conceptProgressBack.getUserProfile()).isNull();

        userProfile.conceptProgresseses(new HashSet<>(Set.of(conceptProgressBack)));
        assertThat(userProfile.getConceptProgresseses()).containsOnly(conceptProgressBack);
        assertThat(conceptProgressBack.getUserProfile()).isEqualTo(userProfile);

        userProfile.setConceptProgresseses(new HashSet<>());
        assertThat(userProfile.getConceptProgresseses()).doesNotContain(conceptProgressBack);
        assertThat(conceptProgressBack.getUserProfile()).isNull();
    }
}
