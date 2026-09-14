package com.crystal.kip.content;

import java.util.List;

public class Stage {

    private Long id;
    private String question;
    private List<AnswerChoice> answerChoices;

    public Stage() {}

    public Stage(Long id, String question, List<AnswerChoice> answerChoices) {
        this.id = id;
        this.question = question;
        this.answerChoices = answerChoices;
    }

    public Long getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public List<AnswerChoice> getAnswerChoices() {
        return answerChoices;
    }
}
