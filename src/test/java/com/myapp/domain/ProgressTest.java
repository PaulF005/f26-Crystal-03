package com.myapp.domain;

import static com.myapp.domain.ModuleTestSamples.*;
import static com.myapp.domain.ProgressTestSamples.*;
import static com.myapp.domain.UserDetailTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.web.rest.TestUtil;
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
    void moduleTest() {
        Progress progress = getProgressRandomSampleGenerator();
        Module moduleBack = getModuleRandomSampleGenerator();

        progress.setModule(moduleBack);
        assertThat(progress.getModule()).isEqualTo(moduleBack);

        progress.module(null);
        assertThat(progress.getModule()).isNull();
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
