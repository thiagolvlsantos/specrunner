package org.specrunner.application.entities;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.IndexColumn;

@Entity
@Table(name = "EMP_EMPLOYEE")
@SuppressWarnings("serial")
public class Employee extends Person {

    private Double salary;
    private List<Unit> units = new LinkedList<Unit>();

    public Employee() {
        setType(PersonType.EMPLOYEE);
    }

    @Column(name = "EMP_SALARY")
    @NotNull
    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @Cascade(value = { CascadeType.SAVE_UPDATE })
    @JoinTable(name = "LINK_EMP_UNI", joinColumns = { @JoinColumn(name = "EMP_ID") }, inverseJoinColumns = { @JoinColumn(name = "UNI_ID") })
    @IndexColumn(name = "LINK_EMP_UNI_INDEX", base = 1)
    @NotNull
    public List<Unit> getUnits() {
        return units;
    }

    public void setUnits(List<Unit> units) {
        this.units = units;
    }
}