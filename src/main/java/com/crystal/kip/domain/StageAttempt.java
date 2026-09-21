package com.crystal.kip.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A StageAttempt.
 */
@Entity
@Table(name = "stage_attempt")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StageAttempt implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "answered_at", nullable = false)
    private Instant answeredAt;

    @NotNull
    @Column(name = "correct", nullable = false)
    private Boolean correct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "dataUser", "topicProgresseses", "gameProgresseses", "conceptProgresseses" }, allowSetters = true)
    private UserProfile user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "question", "scenario" }, allowSetters = true)
    private Stage stage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "nextStage", "feedback", "question" }, allowSetters = true)
    private Answer selectedAnswer;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "user", "game", "stageAttempts" }, allowSetters = true)
    private GameSession gameSession;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StageAttempt id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getAnsweredAt() {
        return this.answeredAt;
    }

    public StageAttempt answeredAt(Instant answeredAt) {
        this.setAnsweredAt(answeredAt);
        return this;
    }

    public void setAnsweredAt(Instant answeredAt) {
        this.answeredAt = answeredAt;
    }

    public Boolean getCorrect() {
        return this.correct;
    }

    public StageAttempt correct(Boolean correct) {
        this.setCorrect(correct);
        return this;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    public UserProfile getUser() {
        return this.user;
    }

    public void setUser(UserProfile userProfile) {
        this.user = userProfile;
    }

    public StageAttempt user(UserProfile userProfile) {
        this.setUser(userProfile);
        return this;
    }

    public Stage getStage() {
        return this.stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public StageAttempt stage(Stage stage) {
        this.setStage(stage);
        return this;
    }

    public Answer getSelectedAnswer() {
        return this.selectedAnswer;
    }

    public void setSelectedAnswer(Answer answer) {
        this.selectedAnswer = answer;
    }

    public StageAttempt selectedAnswer(Answer answer) {
        this.setSelectedAnswer(answer);
        return this;
    }

    public GameSession getGameSession() {
        return this.gameSession;
    }

    public void setGameSession(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public StageAttempt gameSession(GameSession gameSession) {
        this.setGameSession(gameSession);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StageAttempt)) {
            return false;
        }
        return getId() != null && getId().equals(((StageAttempt) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StageAttempt{" +
            "id=" + getId() +
            ", answeredAt='" + getAnsweredAt() + "'" +
            ", correct='" + getCorrect() + "'" +
            "}";
    }
}
