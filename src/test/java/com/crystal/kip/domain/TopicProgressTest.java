package com.crystal.kip.domain;

import static com.crystal.kip.domain.TopicProgressTestSamples.*;
import static com.crystal.kip.domain.TopicTestSamples.*;
import static com.crystal.kip.domain.UserProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.crystal.kip.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TopicProgressTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TopicProgress.class);
        TopicProgress topicProgress1 = getTopicProgressSample1();
        TopicProgress topicProgress2 = new TopicProgress();
        assertThat(topicProgress1).isNotEqualTo(topicProgress2);

        topicProgress2.setId(topicProgress1.getId());
        assertThat(topicProgress1).isEqualTo(topicProgress2);

        topicProgress2 = getTopicProgressSample2();
        assertThat(topicProgress1).isNotEqualTo(topicProgress2);
    }

    @Test
    void topicTest() {
        TopicProgress topicProgress = getTopicProgressRandomSampleGenerator();
        Topic topicBack = getTopicRandomSampleGenerator();

        topicProgress.setTopic(topicBack);
        assertThat(topicProgress.getTopic()).isEqualTo(topicBack);

        topicProgress.topic(null);
        assertThat(topicProgress.getTopic()).isNull();
    }

    @Test
    void userProfileTest() {
        TopicProgress topicProgress = getTopicProgressRandomSampleGenerator();
        UserProfile userProfileBack = getUserProfileRandomSampleGenerator();

        topicProgress.setUserProfile(userProfileBack);
        assertThat(topicProgress.getUserProfile()).isEqualTo(userProfileBack);

        topicProgress.userProfile(null);
        assertThat(topicProgress.getUserProfile()).isNull();
    }
}
