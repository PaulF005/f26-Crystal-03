package com.crystal.kip.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A GameSession.
 */
@Entity
@Table(name = "game_session")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GameSession implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @NotNull
    @Column(name = "completed_at", nullable = false)
    private Instant completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "dataUser", "topicProgresseses", "gameProgresseses", "conceptProgresseses" }, allowSetters = true)
    private UserProfile user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "scenarios" }, allowSetters = true)
    private Game game;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "gameSession")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user", "stage", "selectedAnswer", "gameSession" }, allowSetters = true)
    private Set<StageAttempt> stageAttempts = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public GameSession id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartedAt() {
        return this.startedAt;
    }

    public GameSession startedAt(Instant startedAt) {
        this.setStartedAt(startedAt);
        return this;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getCompletedAt() {
        return this.completedAt;
    }

    public GameSession completedAt(Instant completedAt) {
        this.setCompletedAt(completedAt);
        return this;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public UserProfile getUser() {
        return this.user;
    }

    public void setUser(UserProfile userProfile) {
        this.user = userProfile;
    }

    public GameSession user(UserProfile userProfile) {
        this.setUser(userProfile);
        return this;
    }

    public Game getGame() {
        return this.game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public GameSession game(Game game) {
        this.setGame(game);
        return this;
    }

    public Set<StageAttempt> getStageAttempts() {
        return this.stageAttempts;
    }

    public void setStageAttempts(Set<StageAttempt> stageAttempts) {
        if (this.stageAttempts != null) {
            this.stageAttempts.forEach(i -> i.setGameSession(null));
        }
        if (stageAttempts != null) {
            stageAttempts.forEach(i -> i.setGameSession(this));
        }
        this.stageAttempts = stageAttempts;
    }

    public GameSession stageAttempts(Set<StageAttempt> stageAttempts) {
        this.setStageAttempts(stageAttempts);
        return this;
    }

    public GameSession addStageAttempt(StageAttempt stageAttempt) {
        this.stageAttempts.add(stageAttempt);
        stageAttempt.setGameSession(this);
        return this;
    }

    public GameSession removeStageAttempt(StageAttempt stageAttempt) {
        this.stageAttempts.remove(stageAttempt);
        stageAttempt.setGameSession(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GameSession)) {
            return false;
        }
        return getId() != null && getId().equals(((GameSession) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GameSession{" +
            "id=" + getId() +
            ", startedAt='" + getStartedAt() + "'" +
            ", completedAt='" + getCompletedAt() + "'" +
            "}";
    }
}
