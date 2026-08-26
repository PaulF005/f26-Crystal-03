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
 * A Module.
 */
@Entity
@Table(name = "module")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Module implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "module")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "questions", "legalContent", "module" }, allowSetters = true)
    private Set<Topic> topics = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Module id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Module name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Topic> getTopics() {
        return this.topics;
    }

    public void setTopics(Set<Topic> topics) {
        if (this.topics != null) {
            this.topics.forEach(i -> i.setModule(null));
        }
        if (topics != null) {
            topics.forEach(i -> i.setModule(this));
        }
        this.topics = topics;
    }

    public Module topics(Set<Topic> topics) {
        this.setTopics(topics);
        return this;
    }

    public Module addTopic(Topic topic) {
        this.topics.add(topic);
        topic.setModule(this);
        return this;
    }

    public Module removeTopic(Topic topic) {
        this.topics.remove(topic);
        topic.setModule(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Module)) {
            return false;
        }
        return getId() != null && getId().equals(((Module) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Module{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
