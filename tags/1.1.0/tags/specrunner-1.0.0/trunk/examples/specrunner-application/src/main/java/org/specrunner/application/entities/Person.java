package org.specrunner.application.entities;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.specrunner.application.util.Identifiable;

@Entity
@Table(name = "PER_PERSON")
@Inheritance(strategy = InheritanceType.JOINED)
@AttributeOverrides(value = { @AttributeOverride(name = "id", column = @Column(name = "PER_ID")) })
@SuppressWarnings("serial")
public class Person extends Identifiable {

    private PersonType type = PersonType.PERSON;
    private Naming naming = new Naming();

    @Column(name = "PER_TYPE")
    @NotNull
    public PersonType getType() {
        return type;
    }

    public void setType(PersonType type) {
        this.type = type;
    }

    @Embedded
    @AttributeOverrides(value = { @AttributeOverride(name = "first", column = @Column(name = "PER_FIRST")), @AttributeOverride(name = "last", column = @Column(name = "PER_LAST")) })
    @Valid
    public Naming getNaming() {
        return naming;
    }

    public void setNaming(Naming naming) {
        this.naming = naming;
    }
}