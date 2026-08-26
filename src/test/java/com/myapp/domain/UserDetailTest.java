package com.myapp.domain;

import static com.myapp.domain.ProgressTestSamples.*;
import static com.myapp.domain.UserDetailTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UserDetailTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserDetail.class);
        UserDetail userDetail1 = getUserDetailSample1();
        UserDetail userDetail2 = new UserDetail();
        assertThat(userDetail1).isNotEqualTo(userDetail2);

        userDetail2.setId(userDetail1.getId());
        assertThat(userDetail1).isEqualTo(userDetail2);

        userDetail2 = getUserDetailSample2();
        assertThat(userDetail1).isNotEqualTo(userDetail2);
    }

    @Test
    void progressTest() {
        UserDetail userDetail = getUserDetailRandomSampleGenerator();
        Progress progressBack = getProgressRandomSampleGenerator();

        userDetail.addProgress(progressBack);
        assertThat(userDetail.getProgresses()).containsOnly(progressBack);
        assertThat(progressBack.getUser()).isEqualTo(userDetail);

        userDetail.removeProgress(progressBack);
        assertThat(userDetail.getProgresses()).doesNotContain(progressBack);
        assertThat(progressBack.getUser()).isNull();

        userDetail.progresses(new HashSet<>(Set.of(progressBack)));
        assertThat(userDetail.getProgresses()).containsOnly(progressBack);
        assertThat(progressBack.getUser()).isEqualTo(userDetail);

        userDetail.setProgresses(new HashSet<>());
        assertThat(userDetail.getProgresses()).doesNotContain(progressBack);
        assertThat(progressBack.getUser()).isNull();
    }
}
