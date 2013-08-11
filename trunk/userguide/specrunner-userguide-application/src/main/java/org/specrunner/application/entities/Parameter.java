package org.specrunner.application.entities;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Parameter")
public class Parameter {

    private Long id;
    private ParameterType type;
    private Object value;

    public Parameter() {
    }

    public Parameter(ParameterType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParameterType getType() {
        return type;
    }

    public void setType(ParameterType type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}