package com.myapp.domain;

import static com.myapp.domain.LegalContentTestSamples.*;
import static com.myapp.domain.SourceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
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

    @Test
    void sourceTest() {
        LegalContent legalContent = getLegalContentRandomSampleGenerator();
        Source sourceBack = getSourceRandomSampleGenerator();

        legalContent.addSource(sourceBack);
        assertThat(legalContent.getSources()).containsOnly(sourceBack);
        assertThat(sourceBack.getLegalContent()).isEqualTo(legalContent);

        legalContent.removeSource(sourceBack);
        assertThat(legalContent.getSources()).doesNotContain(sourceBack);
        assertThat(sourceBack.getLegalContent()).isNull();

        legalContent.sources(new HashSet<>(Set.of(sourceBack)));
        assertThat(legalContent.getSources()).containsOnly(sourceBack);
        assertThat(sourceBack.getLegalContent()).isEqualTo(legalContent);

        legalContent.setSources(new HashSet<>());
        assertThat(legalContent.getSources()).doesNotContain(sourceBack);
        assertThat(sourceBack.getLegalContent()).isNull();
    }
}
