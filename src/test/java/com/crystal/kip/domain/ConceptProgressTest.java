package com.crystal.kip.domain;

import static com.crystal.kip.domain.ConceptProgressTestSamples.*;
import static com.crystal.kip.domain.ConceptTestSamples.*;
import static com.crystal.kip.domain.UserProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.crystal.kip.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ConceptProgressTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ConceptProgress.class);
        ConceptProgress conceptProgress1 = getConceptProgressSample1();
        ConceptProgress conceptProgress2 = new ConceptProgress();
        assertThat(conceptProgress1).isNotEqualTo(conceptProgress2);

        conceptProgress2.setId(conceptProgress1.getId());
        assertThat(conceptProgress1).isEqualTo(conceptProgress2);

        conceptProgress2 = getConceptProgressSample2();
        assertThat(conceptProgress1).isNotEqualTo(conceptProgress2);
    }

    @Test
    void userProfileTest() {
        ConceptProgress conceptProgress = getConceptProgressRandomSampleGenerator();
        UserProfile userProfileBack = getUserProfileRandomSampleGenerator();

        conceptProgress.setUserProfile(userProfileBack);
        assertThat(conceptProgress.getUserProfile()).isEqualTo(userProfileBack);

        conceptProgress.userProfile(null);
        assertThat(conceptProgress.getUserProfile()).isNull();
    }

    @Test
    void conceptTest() {
        ConceptProgress conceptProgress = getConceptProgressRandomSampleGenerator();
        Concept conceptBack = getConceptRandomSampleGenerator();

        conceptProgress.setConcept(conceptBack);
        assertThat(conceptProgress.getConcept()).isEqualTo(conceptBack);

        conceptProgress.concept(null);
        assertThat(conceptProgress.getConcept()).isNull();
    }
}
