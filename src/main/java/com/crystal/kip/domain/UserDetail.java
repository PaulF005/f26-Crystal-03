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
 * A UserDetail.
 */
@Entity
@Table(name = "user_detail")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserDetail implements Serializable {

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Set<Progress> progresses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserDetail id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public UserDetail username(String username) {
        this.setUsername(username);
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public UserDetail email(String email) {
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

    public UserDetail dataUser(User user) {
        this.setDataUser(user);
        return this;
    }

    public Set<Progress> getProgresses() {
        return this.progresses;
    }

    public void setProgresses(Set<Progress> progresses) {
        if (this.progresses != null) {
            this.progresses.forEach(i -> i.setUser(null));
        }
        if (progresses != null) {
            progresses.forEach(i -> i.setUser(this));
        }
        this.progresses = progresses;
    }

    public UserDetail progresses(Set<Progress> progresses) {
        this.setProgresses(progresses);
        return this;
    }

    public UserDetail addProgress(Progress progress) {
        this.progresses.add(progress);
        progress.setUser(this);
        return this;
    }

    public UserDetail removeProgress(Progress progress) {
        this.progresses.remove(progress);
        progress.setUser(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserDetail)) {
            return false;
        }
        return getId() != null && getId().equals(((UserDetail) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserDetail{" +
            "id=" + getId() +
            ", username='" + getUsername() + "'" +
            ", email='" + getEmail() + "'" +
            "}";
    }
}
