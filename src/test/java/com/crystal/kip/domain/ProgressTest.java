package com.crystal.kip.domain;

import static com.crystal.kip.domain.ProgressTestSamples.*;
import static com.crystal.kip.domain.UserDetailTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.crystal.kip.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProgressTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Progress.class);
        Progress progress1 = getProgressSample1();
        Progress progress2 = new Progress();
        assertThat(progress1).isNotEqualTo(progress2);

        progress2.setId(progress1.getId());
        assertThat(progress1).isEqualTo(progress2);

        progress2 = getProgressSample2();
        assertThat(progress1).isNotEqualTo(progress2);
    }

    @Test
    void userTest() {
        Progress progress = getProgressRandomSampleGenerator();
        UserDetail userDetailBack = getUserDetailRandomSampleGenerator();

        progress.setUser(userDetailBack);
        assertThat(progress.getUser()).isEqualTo(userDetailBack);

        progress.user(null);
        assertThat(progress.getUser()).isNull();
    }
}
