package com.crystal.kip.content;

public class Answer extends Content {

    private String text;
    private String outcomeText;
    private Stage nextStage;

    public Answer() {}

    public Answer(Long id, String text, String outcomeText, Stage nextStage) {
        this.id = id;
        this.text = text;
        this.outcomeText = outcomeText;
        this.nextStage = nextStage;
    }

    public String getText() {
        return text;
    }

    public String getOutcomeText() {
        return outcomeText;
    }

    public Stage getNextStage() {
        return nextStage;
    }
}
