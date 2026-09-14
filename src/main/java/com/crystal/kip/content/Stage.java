package com.crystal.kip.content;

import java.util.List;

public class Stage {

    private Long id;
    private List<Question> questions;

    public Stage() {}

    public Stage(Long id, List<Question> questions) {
        this.id = id;
        this.questions = questions;
    }

    public Long getId() {
        return id;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
