package com.crystal.kip.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Concept.
 */
@Entity
@Table(name = "concept")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Concept implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "explanation")
    private String explanation;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "concept")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "answers", "concept" }, allowSetters = true)
    private Set<Question> questions = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private LegalContent legalContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "concepts" }, allowSetters = true)
    private Topic topic;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "concept")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "concept", "userProfile" }, allowSetters = true)
    private Set<ConceptProgress> conceptProgresseses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Concept id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Concept name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExplanation() {
        return this.explanation;
    }

    public Concept explanation(String explanation) {
        this.setExplanation(explanation);
        return this;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Set<Question> getQuestions() {
        return this.questions;
    }

    public void setQuestions(Set<Question> questions) {
        if (this.questions != null) {
            this.questions.forEach(i -> i.setConcept(null));
        }
        if (questions != null) {
            questions.forEach(i -> i.setConcept(this));
        }
        this.questions = questions;
    }

    public Concept questions(Set<Question> questions) {
        this.setQuestions(questions);
        return this;
    }

    public Concept addQuestion(Question question) {
        this.questions.add(question);
        question.setConcept(this);
        return this;
    }

    public Concept removeQuestion(Question question) {
        this.questions.remove(question);
        question.setConcept(null);
        return this;
    }

    public LegalContent getLegalContent() {
        return this.legalContent;
    }

    public void setLegalContent(LegalContent legalContent) {
        this.legalContent = legalContent;
    }

    public Concept legalContent(LegalContent legalContent) {
        this.setLegalContent(legalContent);
        return this;
    }

    public Topic getTopic() {
        return this.topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Concept topic(Topic topic) {
        this.setTopic(topic);
        return this;
    }

    public Set<ConceptProgress> getConceptProgresseses() {
        return this.conceptProgresseses;
    }

    public void setConceptProgresseses(Set<ConceptProgress> conceptProgresses) {
        if (this.conceptProgresseses != null) {
            this.conceptProgresseses.forEach(i -> i.setConcept(null));
        }
        if (conceptProgresses != null) {
            conceptProgresses.forEach(i -> i.setConcept(this));
        }
        this.conceptProgresseses = conceptProgresses;
    }

    public Concept conceptProgresseses(Set<ConceptProgress> conceptProgresses) {
        this.setConceptProgresseses(conceptProgresses);
        return this;
    }

    public Concept addConceptProgresses(ConceptProgress conceptProgress) {
        this.conceptProgresseses.add(conceptProgress);
        conceptProgress.setConcept(this);
        return this;
    }

    public Concept removeConceptProgresses(ConceptProgress conceptProgress) {
        this.conceptProgresseses.remove(conceptProgress);
        conceptProgress.setConcept(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Concept)) {
            return false;
        }
        return getId() != null && getId().equals(((Concept) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Concept{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", explanation='" + getExplanation() + "'" +
            "}";
    }
}
