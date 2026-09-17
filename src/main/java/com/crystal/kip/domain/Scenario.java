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
 * A Scenario.
 */
@Entity
@Table(name = "scenario")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Scenario implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "scenario")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "question", "scenario" }, allowSetters = true)
    private Set<Stage> stages = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "concepts" }, allowSetters = true)
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "scenarios" }, allowSetters = true)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "question", "scenario" }, allowSetters = true)
    private Stage startingStage;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Scenario id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Scenario name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Stage> getStages() {
        return this.stages;
    }

    public void setStages(Set<Stage> stages) {
        if (this.stages != null) {
            this.stages.forEach(i -> i.setScenario(null));
        }
        if (stages != null) {
            stages.forEach(i -> i.setScenario(this));
        }
        this.stages = stages;
    }

    public Scenario stages(Set<Stage> stages) {
        this.setStages(stages);
        return this;
    }

    public Scenario addStage(Stage stage) {
        this.stages.add(stage);
        stage.setScenario(this);
        return this;
    }

    public Scenario removeStage(Stage stage) {
        this.stages.remove(stage);
        stage.setScenario(null);
        return this;
    }

    public Topic getTopic() {
        return this.topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Scenario topic(Topic topic) {
        this.setTopic(topic);
        return this;
    }

    public Game getGame() {
        return this.game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Scenario game(Game game) {
        this.setGame(game);
        return this;
    }

    public Stage getStartingStage() {
    return this.startingStage;
}

    public void setStartingStage(Stage startingStage) {
        this.startingStage = startingStage;
    }

    public Scenario startingStage(Stage startingStage) {
        this.setStartingStage(startingStage);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Scenario)) {
            return false;
        }
        return getId() != null && getId().equals(((Scenario) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Scenario{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
