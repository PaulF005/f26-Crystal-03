package com.crystal.kip.domain;

import static com.crystal.kip.domain.LegalContentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.crystal.kip.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LegalContentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LegalContent.class);
        LegalContent legalContent1 = getLegalContentSample1();
        LegalContent legalContent2 = new LegalContent();
        assertThat(legalContent1).isNotEqualTo(legalContent2);

        legalContent2.setId(legalContent1.getId());
        assertThat(legalContent1).isEqualTo(legalContent2);

        legalContent2 = getLegalContentSample2();
        assertThat(legalContent1).isNotEqualTo(legalContent2);
    }
}
