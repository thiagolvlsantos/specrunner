package org.specrunner.application.entities;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum(String.class)
public enum ParameterType {
    BOOLEAN, LONG, DOUBLE, STRING
}