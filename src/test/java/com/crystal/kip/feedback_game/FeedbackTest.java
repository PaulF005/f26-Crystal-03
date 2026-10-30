package com.crystal.kip.feedback_game;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FeedbackTest {

    private Feedback feedback;

    @BeforeEach
    void setUp() {
        feedback = new Feedback();
    }

    @Test
    void testCorrectAnswerReturnsCorrectFeedback() {
        String result = feedback.questionDecider(2, 2, "Because this choice is legal.");

        assertThat(result).isEqualTo("Correct || Because this choice is legal.");
    }

    @Test
    void testIncorrectAnswerReturnsIncorrectFeedback() {
        String result = feedback.questionDecider(1, 2, "The second choice is required.");

        assertThat(result).isEqualTo("Incorrect || The second choice is required.");
    }

    @Test
    void testFeedbackDoesNotRetainQuestionProgressState() {
        assertThat(feedback.questionDecider(1, 1, "First")).isEqualTo("Correct || First");
        assertThat(feedback.questionDecider(1, 2, "Second")).isEqualTo("Incorrect || Second");
        assertThat(feedback.questionDecider(3, 3, "Third")).isEqualTo("Correct || Third");
    }
}
