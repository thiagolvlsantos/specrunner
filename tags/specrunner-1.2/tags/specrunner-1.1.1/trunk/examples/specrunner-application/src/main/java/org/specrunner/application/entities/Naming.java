package org.specrunner.application.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

@Embeddable
@SuppressWarnings("serial")
public class Naming implements Serializable {

    private String first;
    private String last;

    @Column(name = "NAM_FIRST")
    @NotNull
    @NotBlank
    @Size(min = 2, max = 10)
    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    @Column(name = "NAM_LAST")
    @NotNull
    @NotBlank
    @Size(min = 2, max = 10)
    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    @Override
    public String toString() {
        return last + ", " + first;
    }
}
