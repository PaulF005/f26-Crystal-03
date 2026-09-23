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
 * A UserProfile.
 */
@Entity
@Table(name = "user_profile")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserProfile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "username", nullable = false)
    private String username;

    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private User dataUser;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userProfile")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "topic", "userProfile" }, allowSetters = true)
    private Set<TopicProgress> topicProgresseses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userProfile")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "game", "userProfile" }, allowSetters = true)
    private Set<GameProgress> gameProgresseses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userProfile")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "concept", "userProfile" }, allowSetters = true)
    private Set<ConceptProgress> conceptProgresseses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserProfile id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public UserProfile username(String username) {
        this.setUsername(username);
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public UserProfile email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User getDataUser() {
        return this.dataUser;
    }

    public void setDataUser(User user) {
        this.dataUser = user;
    }

    public UserProfile dataUser(User user) {
        this.setDataUser(user);
        return this;
    }

    public Set<TopicProgress> getTopicProgresseses() {
        return this.topicProgresseses;
    }

    public void setTopicProgresseses(Set<TopicProgress> topicProgresses) {
        if (this.topicProgresseses != null) {
            this.topicProgresseses.forEach(i -> i.setUserProfile(null));
        }
        if (topicProgresses != null) {
            topicProgresses.forEach(i -> i.setUserProfile(this));
        }
        this.topicProgresseses = topicProgresses;
    }

    public UserProfile topicProgresseses(Set<TopicProgress> topicProgresses) {
        this.setTopicProgresseses(topicProgresses);
        return this;
    }

    public UserProfile addTopicProgresses(TopicProgress topicProgress) {
        this.topicProgresseses.add(topicProgress);
        topicProgress.setUserProfile(this);
        return this;
    }

    public UserProfile removeTopicProgresses(TopicProgress topicProgress) {
        this.topicProgresseses.remove(topicProgress);
        topicProgress.setUserProfile(null);
        return this;
    }

    public Set<GameProgress> getGameProgresseses() {
        return this.gameProgresseses;
    }

    public void setGameProgresseses(Set<GameProgress> gameProgresses) {
        if (this.gameProgresseses != null) {
            this.gameProgresseses.forEach(i -> i.setUserProfile(null));
        }
        if (gameProgresses != null) {
            gameProgresses.forEach(i -> i.setUserProfile(this));
        }
        this.gameProgresseses = gameProgresses;
    }

    public UserProfile gameProgresseses(Set<GameProgress> gameProgresses) {
        this.setGameProgresseses(gameProgresses);
        return this;
    }

    public UserProfile addGameProgresses(GameProgress gameProgress) {
        this.gameProgresseses.add(gameProgress);
        gameProgress.setUserProfile(this);
        return this;
    }

    public UserProfile removeGameProgresses(GameProgress gameProgress) {
        this.gameProgresseses.remove(gameProgress);
        gameProgress.setUserProfile(null);
        return this;
    }

    public Set<ConceptProgress> getConceptProgresseses() {
        return this.conceptProgresseses;
    }

    public void setConceptProgresseses(Set<ConceptProgress> conceptProgresses) {
        if (this.conceptProgresseses != null) {
            this.conceptProgresseses.forEach(i -> i.setUserProfile(null));
        }
        if (conceptProgresses != null) {
            conceptProgresses.forEach(i -> i.setUserProfile(this));
        }
        this.conceptProgresseses = conceptProgresses;
    }

    public UserProfile conceptProgresseses(Set<ConceptProgress> conceptProgresses) {
        this.setConceptProgresseses(conceptProgresses);
        return this;
    }

    public UserProfile addConceptProgresses(ConceptProgress conceptProgress) {
        this.conceptProgresseses.add(conceptProgress);
        conceptProgress.setUserProfile(this);
        return this;
    }

    public UserProfile removeConceptProgresses(ConceptProgress conceptProgress) {
        this.conceptProgresseses.remove(conceptProgress);
        conceptProgress.setUserProfile(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserProfile)) {
            return false;
        }
        return getId() != null && getId().equals(((UserProfile) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserProfile{" +
            "id=" + getId() +
            ", username='" + getUsername() + "'" +
            ", email='" + getEmail() + "'" +
            "}";
    }
}
