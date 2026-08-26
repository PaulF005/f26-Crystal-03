package com.myapp.domain;

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
 * A Topic.
 */
@Entity
@Table(name = "topic")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Topic implements Serializable {

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "topic")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "topic" }, allowSetters = true)
    private Set<Question> questions = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "sources" }, allowSetters = true)
    private LegalContent legalContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "topics" }, allowSetters = true)
    private Module module;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Topic id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Topic name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExplanation() {
        return this.explanation;
    }

    public Topic explanation(String explanation) {
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
            this.questions.forEach(i -> i.setTopic(null));
        }
        if (questions != null) {
            questions.forEach(i -> i.setTopic(this));
        }
        this.questions = questions;
    }

    public Topic questions(Set<Question> questions) {
        this.setQuestions(questions);
        return this;
    }

    public Topic addQuestion(Question question) {
        this.questions.add(question);
        question.setTopic(this);
        return this;
    }

    public Topic removeQuestion(Question question) {
        this.questions.remove(question);
        question.setTopic(null);
        return this;
    }

    public LegalContent getLegalContent() {
        return this.legalContent;
    }

    public void setLegalContent(LegalContent legalContent) {
        this.legalContent = legalContent;
    }

    public Topic legalContent(LegalContent legalContent) {
        this.setLegalContent(legalContent);
        return this;
    }

    public Module getModule() {
        return this.module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Topic module(Module module) {
        this.setModule(module);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Topic)) {
            return false;
        }
        return getId() != null && getId().equals(((Topic) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Topic{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", explanation='" + getExplanation() + "'" +
            "}";
    }
}
