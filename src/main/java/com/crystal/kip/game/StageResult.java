package com.crystal.kip.game;

import com.crystal.kip.domain.Answer;
import com.crystal.kip.domain.Stage;
import java.time.Instant;

public class StageResult {

    private final Stage stage;
    private final Answer selectedAnswer;
    private final Instant answeredAt;
    private final boolean correct;

    public StageResult(Stage stage, Answer selectedAnswer) {
        this.stage = stage;
        this.selectedAnswer = selectedAnswer;
        this.answeredAt = Instant.now();
        this.correct = Boolean.TRUE.equals(selectedAnswer.getCorrect());
    }

    public Stage getStage() {
        return stage;
    }

    public Answer getSelectedAnswer() {
        return selectedAnswer;
    }

    public Instant getAnsweredAt() {
        return answeredAt;
    }

    public boolean isCorrect() {
        return correct;
    }
}
