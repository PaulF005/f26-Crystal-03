package com.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Progress.
 */
@Entity
@Table(name = "progress")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Progress implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "module_completion", nullable = false)
    private Float moduleCompletion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "topics" }, allowSetters = true)
    private Module module;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "dataUser", "progresses" }, allowSetters = true)
    private UserDetail user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Progress id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getModuleCompletion() {
        return this.moduleCompletion;
    }

    public Progress moduleCompletion(Float moduleCompletion) {
        this.setModuleCompletion(moduleCompletion);
        return this;
    }

    public void setModuleCompletion(Float moduleCompletion) {
        this.moduleCompletion = moduleCompletion;
    }

    public Module getModule() {
        return this.module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Progress module(Module module) {
        this.setModule(module);
        return this;
    }

    public UserDetail getUser() {
        return this.user;
    }

    public void setUser(UserDetail userDetail) {
        this.user = userDetail;
    }

    public Progress user(UserDetail userDetail) {
        this.setUser(userDetail);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Progress)) {
            return false;
        }
        return getId() != null && getId().equals(((Progress) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Progress{" +
            "id=" + getId() +
            ", moduleCompletion=" + getModuleCompletion() +
            "}";
    }
}
