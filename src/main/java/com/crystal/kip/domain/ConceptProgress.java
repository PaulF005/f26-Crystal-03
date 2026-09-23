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
 * A ConceptProgress.
 */
@Entity
@Table(name = "concept_progress")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ConceptProgress implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "1")
    @Column(name = "competency", nullable = false)
    private Float competency;

    @NotNull
    @Column(name = "improvement", nullable = false)
    private Float improvement;

    @NotNull
    @Min(value = 0)
    @Column(name = "evidence_count", nullable = false)
    private Integer evidenceCount;

    @NotNull
    @Column(name = "last_practiced_at", nullable = false)
    private Instant lastPracticedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "questions", "legalContent", "topic" }, allowSetters = true)
    private Concept concept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "dataUser", "topicProgresseses", "gameProgresseses", "conceptProgresseses" }, allowSetters = true)
    private UserProfile userProfile;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ConceptProgress id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getCompetency() {
        return this.competency;
    }

    public ConceptProgress competency(Float competency) {
        this.setCompetency(competency);
        return this;
    }

    public void setCompetency(Float competency) {
        this.competency = competency;
    }

    public Float getImprovement() {
        return this.improvement;
    }

    public ConceptProgress improvement(Float improvement) {
        this.setImprovement(improvement);
        return this;
    }

    public void setImprovement(Float improvement) {
        this.improvement = improvement;
    }

    public Integer getEvidenceCount() {
        return this.evidenceCount;
    }

    public ConceptProgress evidenceCount(Integer evidenceCount) {
        this.setEvidenceCount(evidenceCount);
        return this;
    }

    public void setEvidenceCount(Integer evidenceCount) {
        this.evidenceCount = evidenceCount;
    }

    public Instant getLastPracticedAt() {
        return this.lastPracticedAt;
    }

    public ConceptProgress lastPracticedAt(Instant lastPracticedAt) {
        this.setLastPracticedAt(lastPracticedAt);
        return this;
    }

    public void setLastPracticedAt(Instant lastPracticedAt) {
        this.lastPracticedAt = lastPracticedAt;
    }

    public Concept getConcept() {
        return this.concept;
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    public ConceptProgress concept(Concept concept) {
        this.setConcept(concept);
        return this;
    }

    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public ConceptProgress userProfile(UserProfile userProfile) {
        this.setUserProfile(userProfile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConceptProgress)) {
            return false;
        }
        return getId() != null && getId().equals(((ConceptProgress) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ConceptProgress{" +
            "id=" + getId() +
            ", competency=" + getCompetency() +
            ", improvement=" + getImprovement() +
            ", evidenceCount=" + getEvidenceCount() +
            ", lastPracticedAt='" + getLastPracticedAt() + "'" +
            "}";
    }
}
