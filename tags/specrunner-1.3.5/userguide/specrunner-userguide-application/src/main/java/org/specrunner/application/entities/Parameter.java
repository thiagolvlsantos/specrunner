package org.specrunner.application.entities;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Parameter")
public class Parameter {

    private Long id;
    private ParameterType type;
    private String value;

    public Parameter() {
    }

    public Parameter(ParameterType type, String value) {
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void validate() throws Exception {
        if (type == null) {
            throw new Exception("Type is null.");
        }
        try {
            getObject();
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    public Object getObject() {
        if (value == null) {
            return null;
        }
        switch (type) {
        case BOOLEAN:
            return Boolean.valueOf(value);
        case LONG:
            return Long.valueOf(value);
        case DOUBLE:
            return Double.valueOf(value);
        case STRING:
            return value;
        }
        return value;
    }
}