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
 * A LegalContent.
 */
@Entity
@Table(name = "legal_content")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LegalContent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "legalContent")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "legalContent" }, allowSetters = true)
    private Set<Source> sources = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LegalContent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public LegalContent name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Source> getSources() {
        return this.sources;
    }

    public void setSources(Set<Source> sources) {
        if (this.sources != null) {
            this.sources.forEach(i -> i.setLegalContent(null));
        }
        if (sources != null) {
            sources.forEach(i -> i.setLegalContent(this));
        }
        this.sources = sources;
    }

    public LegalContent sources(Set<Source> sources) {
        this.setSources(sources);
        return this;
    }

    public LegalContent addSource(Source source) {
        this.sources.add(source);
        source.setLegalContent(this);
        return this;
    }

    public LegalContent removeSource(Source source) {
        this.sources.remove(source);
        source.setLegalContent(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LegalContent)) {
            return false;
        }
        return getId() != null && getId().equals(((LegalContent) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LegalContent{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
