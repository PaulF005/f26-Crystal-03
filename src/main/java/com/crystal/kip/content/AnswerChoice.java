package com.crystal.kip.content;

public class AnswerChoice {

    private Long id;
    private String text;
    private boolean correct;

    public AnswerChoice() {}

    public AnswerChoice(Long id, String text, boolean correct) {
        this.id = id;
        this.text = text;
        this.correct = correct;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public boolean isCorrect() {
        return correct;
    }
}
