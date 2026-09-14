package com.crystal.kip.content;

import java.util.List;

public class Question {

    private Long id;
    private String concept;
    private List<Answer> answerChoices;

    public Question() {}

    public Question(Long id, String concept, List<Answer> answerChoices) {
        this.id = id;
        this.concept = concept;
        this.answerChoices = answerChoices;
    }

    public Long getId() {
        return id;
    }

    public String getConcept() {
        return concept;
    }

    public List<Answer> getAnswerChoices() {
        return answerChoices;
    }
}
