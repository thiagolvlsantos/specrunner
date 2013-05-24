package org.specrunner.application.entities;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.IndexColumn;
import org.specrunner.application.util.tree.CompositeImpl;

@Entity
@Table(name = "UNI_UNIT")
@AttributeOverrides(value = { @AttributeOverride(name = "id", column = @Column(name = "UNI_ID")) })
@SuppressWarnings("serial")
public class Unit extends CompositeImpl<Unit, Unit> {

    private String name;

    @ManyToOne
    @JoinColumn(name = "UNI_PARENT")
    public Unit getParent() {
        return parent;
    }

    @Column(name = "UNI_NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @Cascade(value = { CascadeType.SAVE_UPDATE })
    @JoinTable(name = "LINK_UNI_CHILD", joinColumns = { @JoinColumn(name = "UNI_ID") }, inverseJoinColumns = { @JoinColumn(name = "UNI_ID_CHILD") })
    @IndexColumn(name = "LINK_UNI_CHILD_INDEX", base = 1)
    @NotNull
    public List<Unit> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return getName();
    }
}