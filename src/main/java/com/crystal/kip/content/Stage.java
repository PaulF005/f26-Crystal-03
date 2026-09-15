package com.crystal.kip.content;

public class Stage extends Content {

    private Question question;

    public Stage() {}

    public Stage(Long id, Question question) {
        this.id = id;
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }
}
