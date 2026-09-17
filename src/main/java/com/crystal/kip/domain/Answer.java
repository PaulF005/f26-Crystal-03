package com.crystal.kip.domain;

import com.crystal.kip.domain.enumeration.ScenarioResolution;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Answer.
 */
@Entity
@Table(name = "answer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Answer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "outcome_text")
    private String outcomeText;

    @Column(name = "correct")
    private Boolean correct;

    @Enumerated(EnumType.STRING)
    @Column(name = "terminal_resolution")
    private ScenarioResolution terminalResolution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "question", "scenario" }, allowSetters = true)
    private Stage nextStage;

    @ManyToOne(fetch = FetchType.LAZY)
    private Feedback feedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "answers", "concept" }, allowSetters = true)
    private Question question;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Answer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return this.text;
    }

    public Answer text(String text) {
        this.setText(text);
        return this;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOutcomeText() {
        return this.outcomeText;
    }

    public void setOutcomeText(String outcomeText) {
        this.outcomeText = outcomeText;
    }

    public Answer outcomeText(String outcomeText) {
        this.setOutcomeText(outcomeText);
        return this;
    }

    public ScenarioResolution getTerminalResolution() {
        return this.terminalResolution;
    }

    public void setTerminalResolution(ScenarioResolution terminalResolution) {
        this.terminalResolution = terminalResolution;
    }

    public Answer terminalResolution(ScenarioResolution terminalResolution) {
        this.setTerminalResolution(terminalResolution);
        return this;
    }

    public Boolean getCorrect() {
        return this.correct;
    }

    public Answer correct(Boolean correct) {
        this.setCorrect(correct);
        return this;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    public Stage getNextStage() {
        return this.nextStage;
    }

    public void setNextStage(Stage stage) {
        this.nextStage = stage;
    }

    public Answer nextStage(Stage stage) {
        this.setNextStage(stage);
        return this;
    }

    public Feedback getFeedback() {
        return this.feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public Answer feedback(Feedback feedback) {
        this.setFeedback(feedback);
        return this;
    }

    public Question getQuestion() {
        return this.question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Answer question(Question question) {
        this.setQuestion(question);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Answer)) {
            return false;
        }
        return getId() != null && getId().equals(((Answer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Answer{" +
            "id=" + getId() +
            ", text='" + getText() + "'" +
            ", correct='" + getCorrect() + "'" +
            ", terminalResolution='" + getTerminalResolution() + "'" +
            "}";
    }
}
