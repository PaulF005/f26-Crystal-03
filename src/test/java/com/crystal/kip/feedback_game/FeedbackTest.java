package com.crystal.kip.feedback_game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import com.crystal.kip.progress_update.ProgressUpdater;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FeedbackTest {

    private ProgressUpdater progressUpdater;
    private Feedback feedbackTest;

    /**
     * Basic set for tests, uses Mockito so it can run independently of a database
     */
    @BeforeEach
    void setUp() {
        progressUpdater = mock(ProgressUpdater.class);
        feedbackTest = new Feedback(4, progressUpdater);
    }

    /**
     * This test all methods for Feedback class in a condense fashion
     */
    /*
    @Test
    void testFeedback() {
        String correct1 = feedbackTest.questionDecider(1, 1, "Explanation");
        assertThat(correct1).isEqualTo("Corrrect || Explanation");
        verify(progressUpdater, never()).updateConceptProgress(anyInt());

        String incorrect1 = feedbackTest.questionDecider(3, 2, "Wrong");
        assertThat(incorrect1).isEqualTo("Incorrect  || Wrong");
        verify(progressUpdater, never()).updateConceptProgress(anyInt());

        String correct2 = feedbackTest.questionDecider(3, 3, "Explanation 1");
        assertThat(correct2).isEqualTo("Corrrect || Explanation 1");
        verify(progressUpdater, never()).updateConceptProgress(anyInt());

        String incorrect3 = feedbackTest.questionDecider(4, 2, "Wrong 1");
        assertThat(incorrect3).isEqualTo("Incorrect  || Wrong 1");

        verify(progressUpdater, times(1)).updateConceptProgress(2);
    }
        */
}
