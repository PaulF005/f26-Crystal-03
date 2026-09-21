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
 * A Question.
 */
@Entity
@Table(name = "question")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Question implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "question", nullable = false)
    private String question;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "nextStage", "feedback", "question" }, allowSetters = true)
    private Set<Answer> answers = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "questions", "legalContent", "topic", "conceptProgresseses" }, allowSetters = true)
    private Concept concept;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Question id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return this.question;
    }

    public Question question(String question) {
        this.setQuestion(question);
        return this;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Set<Answer> getAnswers() {
        return this.answers;
    }

    public void setAnswers(Set<Answer> answers) {
        if (this.answers != null) {
            this.answers.forEach(i -> i.setQuestion(null));
        }
        if (answers != null) {
            answers.forEach(i -> i.setQuestion(this));
        }
        this.answers = answers;
    }

    public Question answers(Set<Answer> answers) {
        this.setAnswers(answers);
        return this;
    }

    public Question addAnswer(Answer answer) {
        this.answers.add(answer);
        answer.setQuestion(this);
        return this;
    }

    public Question removeAnswer(Answer answer) {
        this.answers.remove(answer);
        answer.setQuestion(null);
        return this;
    }

    public Concept getConcept() {
        return this.concept;
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    public Question concept(Concept concept) {
        this.setConcept(concept);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Question)) {
            return false;
        }
        return getId() != null && getId().equals(((Question) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Question{" +
            "id=" + getId() +
            ", question='" + getQuestion() + "'" +
            "}";
    }
}
