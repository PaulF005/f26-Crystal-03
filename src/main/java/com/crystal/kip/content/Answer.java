package com.crystal.kip.content;

public class Answer {

    private Long id;
    private String answerChoice;
    private String feedback;
    private Stage nextStage;

    public Answer() {}

    public Answer(Long id, String answerChoice, String feedback, Stage nextStage) {
        this.id = id;
        this.answerChoice = answerChoice;
        this.feedback = feedback;
        this.nextStage = nextStage;
    }

    public Long getId() {
        return id;
    }

    public String getAnswerChoice() {
        return answerChoice;
    }

    public String getFeedback() {
        return feedback;
    }

    public Stage getNextStage() {
        return nextStage;
    }
}
