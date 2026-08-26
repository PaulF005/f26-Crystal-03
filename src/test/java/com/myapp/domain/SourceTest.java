package com.myapp.domain;

import static com.myapp.domain.LegalContentTestSamples.*;
import static com.myapp.domain.SourceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SourceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Source.class);
        Source source1 = getSourceSample1();
        Source source2 = new Source();
        assertThat(source1).isNotEqualTo(source2);

        source2.setId(source1.getId());
        assertThat(source1).isEqualTo(source2);

        source2 = getSourceSample2();
        assertThat(source1).isNotEqualTo(source2);
    }

    @Test
    void legalContentTest() {
        Source source = getSourceRandomSampleGenerator();
        LegalContent legalContentBack = getLegalContentRandomSampleGenerator();

        source.setLegalContent(legalContentBack);
        assertThat(source.getLegalContent()).isEqualTo(legalContentBack);

        source.legalContent(null);
        assertThat(source.getLegalContent()).isNull();
    }
}
