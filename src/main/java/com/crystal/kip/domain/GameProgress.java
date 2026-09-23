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
 * A GameProgress.
 */
@Entity
@Table(name = "game_progress")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GameProgress implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(value = 0)
    @Column(name = "sessions_played", nullable = false)
    private Integer sessionsPlayed;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "1")
    @Column(name = "performance", nullable = false)
    private Float performance;

    @NotNull
    @Min(value = 0)
    @Column(name = "evidence_count", nullable = false)
    private Integer evidenceCount;

    @NotNull
    @Column(name = "last_played_at", nullable = false)
    private Instant lastPlayedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "scenarios" }, allowSetters = true)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "dataUser", "topicProgresseses", "gameProgresseses", "conceptProgresseses" }, allowSetters = true)
    private UserProfile userProfile;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public GameProgress id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSessionsPlayed() {
        return this.sessionsPlayed;
    }

    public GameProgress sessionsPlayed(Integer sessionsPlayed) {
        this.setSessionsPlayed(sessionsPlayed);
        return this;
    }

    public void setSessionsPlayed(Integer sessionsPlayed) {
        this.sessionsPlayed = sessionsPlayed;
    }

    public Float getPerformance() {
        return this.performance;
    }

    public GameProgress performance(Float performance) {
        this.setPerformance(performance);
        return this;
    }

    public void setPerformance(Float performance) {
        this.performance = performance;
    }

    public Integer getEvidenceCount() {
        return this.evidenceCount;
    }

    public GameProgress evidenceCount(Integer evidenceCount) {
        this.setEvidenceCount(evidenceCount);
        return this;
    }

    public void setEvidenceCount(Integer evidenceCount) {
        this.evidenceCount = evidenceCount;
    }

    public Instant getLastPlayedAt() {
        return this.lastPlayedAt;
    }

    public GameProgress lastPlayedAt(Instant lastPlayedAt) {
        this.setLastPlayedAt(lastPlayedAt);
        return this;
    }

    public void setLastPlayedAt(Instant lastPlayedAt) {
        this.lastPlayedAt = lastPlayedAt;
    }

    public Game getGame() {
        return this.game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public GameProgress game(Game game) {
        this.setGame(game);
        return this;
    }

    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public GameProgress userProfile(UserProfile userProfile) {
        this.setUserProfile(userProfile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GameProgress)) {
            return false;
        }
        return getId() != null && getId().equals(((GameProgress) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GameProgress{" +
            "id=" + getId() +
            ", sessionsPlayed=" + getSessionsPlayed() +
            ", performance=" + getPerformance() +
            ", evidenceCount=" + getEvidenceCount() +
            ", lastPlayedAt='" + getLastPlayedAt() + "'" +
            "}";
    }
}
