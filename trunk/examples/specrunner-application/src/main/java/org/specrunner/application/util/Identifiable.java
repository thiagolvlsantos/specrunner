package org.specrunner.application.util;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.springframework.beans.BeanUtils;

@MappedSuperclass
@SuppressWarnings("serial")
public class Identifiable implements Serializable {

    private Long id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDE_ID")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void copyTo(Identifiable other) {
        BeanUtils.copyProperties(this, other);
    }
}
