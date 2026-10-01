package com.crystal.kip.feedback_game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import com.crystal.kip.progress_update.ProgressUpdaterTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FeedbackTest {

    private ProgressUpdaterTopic progressUpdaterTopicTest;
    private Feedback feedbackTest;

    /**
     * Basic set for tests, uses Mockito so it can run independently of a database
     */
    @BeforeEach
    void setUp() {
        progressUpdaterTopicTest = mock(ProgressUpdaterTopic.class);
        feedbackTest = new Feedback(4, progressUpdaterTopicTest);
    }

    /**
     * This test all methods for Feedback class in a condense fasion
     */
    @Test
    void testFeedback() {
        String correct1 = feedbackTest.questionDecider(1, 1, "Explanation");
        assertThat(correct1).isEqualTo("Corrrect || Explanation");
        verify(progressUpdaterTopicTest, never()).updateTopicProgress(anyInt());

        String incorrect1 = feedbackTest.questionDecider(3, 2, "Wrong");
        assertThat(incorrect1).isEqualTo("Incorrect  || Wrong");
        verify(progressUpdaterTopicTest, never()).updateTopicProgress(anyInt());

        String correct2 = feedbackTest.questionDecider(3, 3, "Explanation 1");
        assertThat(correct2).isEqualTo("Corrrect || Explanation 1");
        verify(progressUpdaterTopicTest, never()).updateTopicProgress(anyInt());

        String incorrect3 = feedbackTest.questionDecider(4, 2, "Wrong 1");
        assertThat(incorrect3).isEqualTo("Incorrect  || Wrong 1");

        verify(progressUpdaterTopicTest, times(1)).updateTopicProgress(2);
    }
}
